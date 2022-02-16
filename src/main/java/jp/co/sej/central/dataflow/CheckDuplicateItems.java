package jp.co.sej.central.dataflow;

import jp.co.sej.central.dataflow.DataFormat.StockItem;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CheckDuplicateItems extends DoFn<List<StockItem>, List<StockItem>> {
    @ProcessElement
    public void processElement(ProcessContext c) {
        List<StockItem> input = c.element();
        Map<String, StockItem> mss = new HashMap<>();
        for(StockItem si : input) {
            if(!mss.containsKey(si.item_cd)) {
                mss.put(si.item_cd, si);
                continue;
            }
            // TODO: compare created_datetime as timestamp
            // COMMENT: this implement discards old one
            StockItem existing = mss.remove(si.item_cd);
            com.google.cloud.Timestamp existing_ts = com.google.cloud.Timestamp.parseTimestamp(existing.created_datetime_fmt);
            com.google.cloud.Timestamp processing_ts = com.google.cloud.Timestamp.parseTimestamp(si.created_datetime_fmt);
            if(existing_ts.compareTo(processing_ts)<=0) {
                mss.put(si.item_cd, si);
            } else {
                mss.put(existing.item_cd, existing);
            }
        }
        List<StockItem> output = new ArrayList<>();
        for(StockItem si : mss.values()) {
            output.add(si);
        }
        c.output(output);
    }
}
