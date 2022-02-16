package jp.co.sej.central.dataflow.DataFormat;

import jp.co.sej.central.dataflow.DataFormat.StockContent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;

public class JsonContent {
    public String id;
    public Integer sendtimes;
    public StockContent[] zaiko;

    /**
     * IDが有効かどうかチェックする
     * @return 有効な場合true、そうでない場合falseを返す
     */
    public boolean isIDValid() {
        if(Objects.isNull(id)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        sdf.setLenient(false);
        try {
            sdf.parse(id);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * sendtimesが有効かどうかチェックする
     * @return 有効な場合true、そうでない場合falseを返す
     */
    public boolean isSendtimesValid() {
        if(Objects.isNull(sendtimes)) return false;
        if(sendtimes<1 || 999999 < sendtimes) return false;
        return true;
    }
}
