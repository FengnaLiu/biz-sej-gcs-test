package jp.co.sej.central.dataflow.DataFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;

public class StockContent {
    public String store_cd;
    public String created_datetime;
    public String item_cd;
    public Integer stock_amnt;
    public String item_handle_flag;
    public String non_handle_focus_item;
    public String mark_down_flag;
    public String item_handle_flag_2;
    public Integer stock_date;

    /**
     * 日付文字列が来るべきところに日付文字列が来ているかどうかをチェックする
     * @return チェック結果に問題があればfalse、なければtrue
     */
    public boolean isDatetimeValueValid() {
        if(Objects.isNull(created_datetime)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        sdf.setLenient(false);
        try {
            sdf.parse(created_datetime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "StockContent{" +
                "store_cd='" + store_cd + '\'' +
                ", created_datetime='" + created_datetime + '\'' +
                ", item_cd='" + item_cd + '\'' +
                ", stock_amnt=" + stock_amnt +
                ", item_handle_flag='" + item_handle_flag + '\'' +
                ", non_handle_focus_item='" + non_handle_focus_item + '\'' +
                ", mark_down_flag='" + mark_down_flag + '\'' +
                ", item_handle_flag_2='" + item_handle_flag_2 + '\'' +
                ", stock_date=" + stock_date +
                '}';
    }
}
