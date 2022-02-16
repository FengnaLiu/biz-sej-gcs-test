package jp.co.sej.central.dataflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.sej.central.dataflow.DataFormat.JsonContent;
import jp.co.sej.central.dataflow.DataFormat.StockContent;
import jp.co.sej.central.dataflow.DataFormat.StockItem;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

public class StockItemExtractor extends DoFn<KV<String,String>, List<StockItem>> {
    static final ObjectMapper om;
    static {
        om = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }
    private static final Logger LOG = Logger.getLogger(StockItemExtractor.class.getName());
    @ProcessElement
    public void processElement(ProcessContext c){
        KV<String,String> input = c.element();
        LOG.info("file: "+ input.getKey());
        String file_directory = input.getKey();
        String file_content = input.getValue();
        List<StockItem> stockItemList=new ArrayList<>();
        try {
            JsonContent jsonContent = om.readValue(input.getValue(), JsonContent.class);
            // idチェック。不正だった場合は当該メッセージ全体を無視する
            if(!jsonContent.isIDValid()) {
                LOG.severe(String.format("invalid id: file:[%s]", file_directory));
                return;
            }
            String id=jsonContent.id;
            // sendtimesチェック。不正だった場合は当該メッセージ全体を無視する
            if(!jsonContent.isSendtimesValid()) {
                LOG.severe(String.format("invalid sendtimes: file:[%s]", file_directory));
                return;
            }
            Integer send_times=jsonContent.sendtimes;

            /*同一pubsub message内に含まれるエントリ達に同じdatetimeを付与するようにするために、
             エントリ生成しようとする直前時点のタイムスタンプを取得してStockItemのConstructorに渡して、「processing_datetime」列として書き込む
             */
            SimpleDateFormat df = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String processing_datetime_str = df.format(new Date());
            for(StockContent stockContent:jsonContent.zaiko){
                // 日付文字列が入るべきところに日付文字列が入っておらず変換が出来ない場合および日付文字列として不正な場合、エラーログを出力して捨てる
                if(!stockContent.isDatetimeValueValid()) {
                    LOG.severe(String.format("Datetime field is invalid: StockContent:[%s], file:[%s]", stockContent.toString(), file_directory));
                    continue;
                }
                StockItem tmp=new StockItem(stockContent,id,send_times,processing_datetime_str);
                LOG.fine(String.format("Stock record: %s",tmp.toString()));
                // 必須項目が揃っている場合のみ下流に流し、揃っていない場合はエラーログを出力して捨てる
                if(!tmp.areMandatoryFieldsComplete()) {
                    LOG.severe(String.format("Mandatory field is lacked: Item:[%s], file:[%s]", tmp.toString(), file_directory));
                    continue;
                }
                // 区分値が適切な値の場合のみ下流に流し、不適切な値が入っていた場合はエラーログを出力して捨てる
                if(!tmp.areDistinctionValuesValid()) {
                    LOG.severe(String.format("Distinction value is invalid: Item:[%s], file:[%s]", tmp.toString(), file_directory));
                    continue;
                }
                // 数値が入るべきところに数値が入っておらず変換が出来ない場合、エラーログを出力して捨てる
                if(!tmp.areNumericValuesValid()) {
                    LOG.severe(String.format("Numeric field is invalid: Item:[%s], file:[%s]", tmp.toString(), file_directory));
                    continue;
                }
                stockItemList.add(tmp);
            }

        }catch (Exception e){
            LOG.severe(String.format("unexpected error occurred: %s, file:[%s]", e.getMessage(),file_directory));
            for(StackTraceElement ste : e.getStackTrace()) {
                LOG.warning(ste.toString());
            }
        }
        c.output(stockItemList);
    }
}
