package jp.co.sej.central.dataflow;

import com.google.cloud.spanner.*;
import jp.co.sej.central.dataflow.DataFormat.StockItem;
import jp.co.sej.central.dataflow.Common.StringConverterForStockItemList;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.*;
import java.util.logging.Logger;

class WriteSpannerIfNewer extends DoFn<List<StockItem>, StockItem> {
    private static final Logger LOG = Logger.getLogger(WriteSpannerIfNewer.class.getName());
    static SpannerOptions options;
    static Spanner spanner;
    static DatabaseId db;
    static DatabaseClient dbClient;
    void initialize() {
        if(dbClient!=null) return;
        if(db!=null && spanner!=null) {
            dbClient = spanner.getDatabaseClient(db);
            return;
        }
        SessionPoolOptions spo = SessionPoolOptions.newBuilder().setBlockIfPoolExhausted().setMaxSessions(10).setWriteSessionsFraction(0.9f).build();
        options = SpannerOptions.newBuilder().setSessionPoolOption(spo). build();
        spanner = options.getService();
        db = DatabaseId.of(options.getProjectId(), "seven-central-dwh", "store-items");
        dbClient = spanner.getDatabaseClient(db);
    }
    @ProcessElement
    public void processElement(ProcessContext c) {
        initialize();
        List<StockItem> lsi = c.element();
        Set<String> item_set = new HashSet<>();
        for (StockItem stockItem : lsi) {
            item_set.add(stockItem.item_cd);
        }

        //SQLの商品コードリストを取得
        List<String> item_list = new ArrayList<>(item_set);
        Value itemArrayValue = Value.stringArray(item_list);

        StringConverterForStockItemList stringConverter= new StringConverterForStockItemList();
        try {
            synchronized (dbClient) {
                dbClient
                        .readWriteTransaction()
                        .run((transactionContext) -> {
                            long startDateSec = new Date().getTime();
                            // 既存在庫エントリの存在チェック
                            Map<String, com.google.cloud.Timestamp> stock_items = new HashMap<>();
                            Statement statement = Statement
                                    .newBuilder("SELECT item_cd, created_datetime FROM Stock WHERE store_id = @STORE_ID AND item_cd in UNNEST(@ITEM_LIST)")
                                    .bind("STORE_ID").to(lsi.get(0).getStoreID())
                                    .bind("ITEM_LIST").to(itemArrayValue)
                                    .build();
                            LOG.fine(statement.toString());
                            ResultSet resultSet = transactionContext.executeQuery(statement);
                            while (resultSet.next()) {
                                String item_cd = resultSet.getString("item_cd");
                                com.google.cloud.Timestamp created_datetime = resultSet.getTimestamp("created_datetime");
                                stock_items.put(item_cd, created_datetime);
                            }
                            LOG.fine(String.format("store_cd:%s, store_id:%d, count(item_cd):%d", lsi.get(0).store_cd, lsi.get(0).getStoreID(), stock_items.size()));

                            // 一括書き込み用のリストを作成
                            List<Statement> stmts = new ArrayList<>();
                            int inserts = 0;
                            int updates = 0;
                            for (StockItem item : lsi) {
                                if (!stock_items.keySet().contains(item.item_cd)) {
                                    // 既存在庫エントリが存在しない場合はINSERT
                                    Statement insert = Statement
                                            .newBuilder("INSERT INTO Stock (id,send_times,store_id, item_cd, commit_timestamp, created_datetime, stock_amnt, store_cd, item_handle_flag, item_handle_flag_2, mark_down_flag, non_handle_focus_item, stock_date) VALUES (@ID,@SEND_TIMES,@STORE_ID, @ITEM_CD, PENDING_COMMIT_TIMESTAMP(), @CREATED_DATETIME, @STOCK_AMNT, @STORE_CD, @ITEM_HANDLE_FLAG, @ITEM_HANDLE_FLAG_2, @MARK_DOWN_FLAG, @NON_HANDLE_FOCUS_ITEM, @STOCK_DATE)")
                                            .bind("ID").to(item.id)
                                            .bind("send_times").to(item.send_times)
                                            .bind("STORE_ID").to(item.getStoreID())
                                            .bind("ITEM_CD").to(item.item_cd)
                                            .bind("CREATED_DATETIME").to(item.created_datetime_fmt)
                                            .bind("STOCK_AMNT").to(item.stock_amnt)
                                            .bind("STORE_CD").to(item.store_cd)
                                            .bind("ITEM_HANDLE_FLAG").to(item.item_handle_flag)
                                            .bind("ITEM_HANDLE_FLAG_2").to(item.item_handle_flag_2)
                                            .bind("MARK_DOWN_FLAG").to(item.mark_down_flag)
                                            .bind("NON_HANDLE_FOCUS_ITEM").to(item.non_handle_focus_item)
                                            .bind("STOCK_DATE").to(item.stock_date)
                                            .build();
                                    stmts.add(insert);
                                    LOG.fine(insert.toString());
                                    inserts++;
                                    c.output(item);
                                } else {
                                    // 既存在庫エントリが存在する場合はUPDATE
                                    // TODO: timezone convert
                                    com.google.cloud.Timestamp inputTimestamp = com.google.cloud.Timestamp.parseTimestamp(item.created_datetime_fmt);
                                    com.google.cloud.Timestamp oldTimestamp = stock_items.get(item.item_cd);
                                    if (inputTimestamp.compareTo(oldTimestamp) < 0) {
                                        // いま受け取ったもののほうが古いデータのため書き込みは行わずスキップする
                                        LOG.warning(String.format("older data is detected, skipping. existing:%s, processing:%s, store_cd:%s, item_cd:%s", oldTimestamp.toString(), inputTimestamp.toString(),lsi.get(0).store_cd,item.item_cd));
                                        continue;
                                    }
                                    Statement update = Statement
                                            .newBuilder("UPDATE Stock SET id=@ID, send_times=@SEND_TIMES, created_datetime = @CREATED_DATETIME, commit_timestamp = PENDING_COMMIT_TIMESTAMP(), stock_amnt = @STOCK_AMNT, store_cd = @STORE_CD, item_handle_flag = @ITEM_HANDLE_FLAG, item_handle_flag_2 = @ITEM_HANDLE_FLAG_2, mark_down_flag = @MARK_DOWN_FLAG, non_handle_focus_item = @NON_HANDLE_FOCUS_ITEM, stock_date = @STOCK_DATE WHERE store_id = @STORE_ID AND item_cd = @ITEM_CD")
                                            .bind("ID").to(item.id)
                                            .bind("SEND_TIMES").to(item.send_times)
                                            .bind("STORE_ID").to(item.getStoreID())
                                            .bind("ITEM_CD").to(item.item_cd)
                                            .bind("CREATED_DATETIME").to(item.created_datetime_fmt)
                                            .bind("STOCK_AMNT").to(item.stock_amnt)
                                            .bind("STORE_CD").to(item.store_cd)
                                            .bind("ITEM_HANDLE_FLAG").to(item.item_handle_flag)
                                            .bind("ITEM_HANDLE_FLAG_2").to(item.item_handle_flag_2)
                                            .bind("MARK_DOWN_FLAG").to(item.mark_down_flag)
                                            .bind("NON_HANDLE_FOCUS_ITEM").to(item.non_handle_focus_item)
                                            .bind("STOCK_DATE").to(item.stock_date)
                                            .build();
                                    stmts.add(update);
                                    LOG.fine(update.toString());
                                    updates++;
                                    c.output(item);
                                }
                            }
                            if (stmts.size() <= 0) {
                                LOG.warning(String.format("there's no effective entry, skipping. StockItemList:[%s]", stringConverter.exec(lsi)));
                                return null;
                            }

                            long[] rowCounts;
                            try {
                                rowCounts = transactionContext.batchUpdate(stmts);
                            } catch (SpannerBatchUpdateException sbue) {
                                LOG.warning(String.format("SpannerBatchUpdateException message: %s. StockItemList:[%s]",sbue.getMessage(),stringConverter.exec(lsi)));
                                for(StackTraceElement ste : sbue.getStackTrace()) {
                                    LOG.warning(ste.toString());
                                }
                                rowCounts = sbue.getUpdateCounts();
                            } catch (Exception e) {
                                LOG.warning(String.format("Exception message: %s. StockItemList:[%s]",e.getMessage(),stringConverter.exec(lsi)));
                                for(StackTraceElement ste : e.getStackTrace()) {
                                    LOG.warning(ste.toString());
                                }
                                return null;
                            }
                            long total_updates = 0L;
                            for (int i = 0; i < rowCounts.length; i++) {
                                total_updates += rowCounts[i];
                            }
                            LOG.info(String.format("%d inserts and %d updates executed, and %d record updated.", inserts, updates, total_updates));
                            long endDateSec = new Date().getTime();
                            LOG.fine(String.format("whole:%d, start:%d, end:%d", endDateSec - startDateSec, startDateSec, endDateSec));
                            return null;
                        });
            }
        } catch(Exception e) {
            LOG.severe(String.format("unexpected error occurred: ErrorDetails[message(%s), class(%s), cause(%s)]. StockItemList:[%s]",
                    e.getMessage(), e.getClass().toString(), e.getCause().toString(), stringConverter.exec(lsi)));
            for(StackTraceElement ste : e.getStackTrace()) {
                LOG.warning(ste.toString());
            }
            return; // TODO: modify to appropriate handling
        }
    }
}
