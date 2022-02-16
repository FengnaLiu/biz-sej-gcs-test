package jp.co.sej.central.dataflow;

import jp.co.sej.central.dataflow.DataFormat.StockItem;
import jp.co.sej.central.dataflow.Common.StringConverterForStockItemList;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.List;
import java.util.logging.Logger;

/**
 * 上流から来たStockItem達の中に、store_cdが異なるエントリが含まれていないかどうか確認する
 */
class CheckConsistOfOneStoreOnly extends DoFn<List<StockItem>, List<StockItem>> {
    private static final Logger LOG=Logger.getLogger(StockDataTransformer.class.getName());
    @ProcessElement
    public void processElement(ProcessContext c) {
        List<StockItem> lsi = c.element();
        if(lsi.size()<=0) return;
        String store_cd = lsi.get(0).store_cd;
        for(StockItem si : lsi) {
            if(!store_cd.equals(si.store_cd)) {
                StringConverterForStockItemList stringConverter = new StringConverterForStockItemList();
                LOG.severe(String.format("another store_cd detected:%s and %s. StockItemList:[%s]", store_cd, si.store_cd,stringConverter.exec(lsi)));
                return;
            }
        }
        c.output(lsi);
    }
}
