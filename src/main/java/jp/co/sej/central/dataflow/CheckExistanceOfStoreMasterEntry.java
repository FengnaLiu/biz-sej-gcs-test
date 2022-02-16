package jp.co.sej.central.dataflow;

import com.google.cloud.spanner.*;
import jp.co.sej.central.dataflow.DataFormat.StockItem;
import jp.co.sej.central.dataflow.Common.StringConverterForStockItemList;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.List;
import java.util.logging.Logger;

class CheckExistanceOfStoreMasterEntry extends DoFn<List<StockItem>, List<StockItem>> {
    private static final Logger LOG = Logger.getLogger(CheckExistanceOfStoreMasterEntry.class.getName());
    static SpannerOptions options;
    static Spanner spanner;
    static DatabaseId db;
    static DatabaseClient dbClient;
    void initialize() {
        if(dbClient!=null) {
            dbClient = spanner.getDatabaseClient(db);
            return;
        }
        if(db!=null && spanner!=null) {
            dbClient = spanner.getDatabaseClient(db);
            return;
        }
        SessionPoolOptions spo = SessionPoolOptions.newBuilder().setBlockIfPoolExhausted().setMaxSessions(1).setWriteSessionsFraction(0.0f).build();
        options = SpannerOptions.newBuilder().setSessionPoolOption(spo). build();
        spanner = options.getService();
        db = DatabaseId.of(options.getProjectId(), "seven-central-dwh", "store-items");
        dbClient = spanner.getDatabaseClient(db);
    }
    @ProcessElement
    public void processElement(ProcessContext c) {
        initialize();
        List<StockItem> lsi = c.element();
        StockItem item = lsi.get(0);
        // 対応する店舗マスタデータの存在チェック
        Statement st1 = Statement
                .newBuilder("SELECT COUNT(*) AS count FROM StoreMaster WHERE store_id = @STORE_ID")
                .bind("STORE_ID").to(item.getStoreID())
                .build();
        long master_count = 0L;
        synchronized (dbClient) {
            try (ReadOnlyTransaction rot = dbClient.singleUseReadOnlyTransaction()) {
                ResultSet rs1 = rot.executeQuery(st1);
                while (rs1.next()) {
                    master_count = rs1.getLong("count");
                    LOG.fine(String.format("store_cd:%s, store_id:%s, master count:%d", item.store_cd, item.getStoreID(), master_count));
                }
            }
        }
        if(master_count<=0) {
            StringConverterForStockItemList stringConverter = new StringConverterForStockItemList();
            LOG.severe(String.format("store-master entry doesn't exist for store_cd:%s. StockItemList:[%s]", item.store_cd,stringConverter.exec(lsi)));
            return;
        }
        c.output(lsi);
    }
}
