package jp.co.sej.central.dataflow;

import com.google.api.services.bigquery.model.TableRow;
import jp.co.sej.central.dataflow.DataFormat.StockItem;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.List;

public class TransformFromItemToTableRow extends DoFn<List<StockItem>, TableRow> {
    @ProcessElement
    public void processElement(ProcessContext c){
        List<StockItem> li=c.element();
        for(IBigqueryTransform ib:li){
            c.output(ib.toTableRow());
        }
    }

}
