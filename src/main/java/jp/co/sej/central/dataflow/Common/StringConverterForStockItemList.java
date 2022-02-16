package jp.co.sej.central.dataflow.Common;

import jp.co.sej.central.dataflow.DataFormat.StockItem;

import java.util.List;

public class StringConverterForStockItemList {
    public String exec(List<StockItem> stl) {
        StringBuilder sb = new StringBuilder();
        for (StockItem stockItem : stl) {
            sb.append(stockItem.toString() + "\n");
        }
        return String.format(sb.toString());
    }
}
