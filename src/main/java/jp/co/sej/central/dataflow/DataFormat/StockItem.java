package jp.co.sej.central.dataflow.DataFormat;

import com.google.api.services.bigquery.model.TableRow;
import jp.co.sej.central.dataflow.IBigqueryTransform;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

public class StockItem implements Serializable, IBigqueryTransform {

    /** processing_datetimeは取り込み処理を行った日時
     <ol>
     <li>BigQueryにのみ書き込む
     <li>UTC("yyyy-MM-dd HH:mm:ss")でフォーマット
     </ol>
     */
    public String processing_datetime;
    public String id;
    public Integer send_times;
    public String store_cd;
    public String created_datetime;
    public String created_datetime_fmt;
    public String item_cd;
    public Integer stock_amnt;
    public String item_handle_flag;
    public String non_handle_focus_item;
    public String mark_down_flag;
    public String item_handle_flag_2;
    public Integer stock_date;

    private static final Logger LOG = Logger.getLogger(StockItem.class.getName());

    public int getStoreID() {
        // TODO: check precondition
        return Integer.reverse(Integer.parseInt(store_cd));
    }

    public StockItem(StockContent sc, String id, Integer send_times, String processing_datetime)throws ParseException{
        this.processing_datetime=processing_datetime;
        this.id=id;
        this.send_times=send_times;
        this.store_cd=sc.store_cd;
        this.created_datetime=sc.created_datetime;
        this.item_cd=sc.item_cd;
        this.stock_amnt=sc.stock_amnt;
        this.item_handle_flag=sc.item_handle_flag;
        this.non_handle_focus_item=sc.non_handle_focus_item;
        this.mark_down_flag=sc.mark_down_flag;
        this.item_handle_flag_2=sc.item_handle_flag_2;
        this.stock_date=sc.stock_date;
        formatTimestampString();
    }

    @Override
    public TableRow toTableRow() {
        TableRow tr = new TableRow();
        tr.set("processing_datetime",processing_datetime);
        tr.set("id",id);
        tr.set("send_times",send_times);
        tr.set("store_cd", store_cd);
        tr.set("store_id", getStoreID());
        tr.set("created_datetime", created_datetime_fmt);
        tr.set("item_cd", item_cd);
        tr.set("stock_amnt", stock_amnt);
        tr.set("item_handle_flag", item_handle_flag);
        tr.set("non_handle_focus_item", non_handle_focus_item);
        tr.set("mark_down_flag", mark_down_flag);
        tr.set("item_handle_flag_2", item_handle_flag_2);
        tr.set("stock_date", stock_date);
        return tr;
    }

    /**
     * 必須項目が全て存在しているかどうかをチェックする
     * @return 全て存在している場合はtrue、一つでも欠けている場合はfalse
     */
    public boolean areMandatoryFieldsComplete() {
        if(Objects.isNull(processing_datetime)) return false;
        if(Objects.isNull(store_cd)) return false;
        if(Objects.isNull(created_datetime)) return false;
        if(Objects.isNull(item_cd)) return false;
        if(Objects.isNull(stock_amnt)) return false;
        if(Objects.isNull(item_handle_flag)) return false;
        if(Objects.isNull(non_handle_focus_item)) return false;
        if(Objects.isNull(mark_down_flag)) return false;
        if(Objects.isNull(item_handle_flag_2)) return false;
        if(Objects.isNull(stock_date)) return false;
        return true;
    }

    /**
     * 区分値の整合性チェックを行う
     * @return 区分値に不整合があればfalse、なければtrue
     */
    public boolean areDistinctionValuesValid() {
        if (!"0".equals(item_handle_flag) && !"1".equals(item_handle_flag)) return false;
        if (!"0".equals(non_handle_focus_item) && !"1".equals(non_handle_focus_item)) return false;
        if (!"0".equals(mark_down_flag) && !"1".equals(mark_down_flag)) return false;
        if (!"1".equals(item_handle_flag_2) && !"2".equals(item_handle_flag_2)) return false;
        return true;
    }

    /**
     * 数値が来るべきところに数値が来ているかどうかをチェックする
     * @return チェック結果に問題があればfalse、なければtrue
     */
    public boolean areNumericValuesValid() {
        try {
            Integer.parseInt(store_cd);
        } catch (NumberFormatException nfe) {
            LOG.severe(String.format("store_cd isn't numeric. store_cd:%s", store_cd));
            LOG.warning(nfe.getMessage());
            return false;
        }
       return true;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        StockItem that=(StockItem) o;
        return Objects.equals(store_cd, that.store_cd)&&
                Objects.equals(processing_datetime,that.processing_datetime)&&
                Objects.equals(id,that.id)&&
                Objects.equals(send_times,that.send_times)&&
                Objects.equals(created_datetime,that.created_datetime)&&
                Objects.equals(created_datetime_fmt,that.created_datetime_fmt)&&
                Objects.equals(item_cd,that.item_cd)&&
                Objects.equals(stock_amnt,that.stock_amnt)&&
                Objects.equals(item_handle_flag,that.item_handle_flag)&&
                Objects.equals(non_handle_focus_item,that.non_handle_focus_item)&&
                Objects.equals(mark_down_flag,that.mark_down_flag)&&
                Objects.equals(item_handle_flag_2,that.item_handle_flag_2)&&
                Objects.equals(stock_date,stock_date);
    }

    public void formatTimestampString()throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        Date createDateTimeJST = sdf.parse(created_datetime);
        LOG.fine(String.format("JST time:%s",sdf.format(createDateTimeJST)));
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String createtDateTimUTCStr=df.format(createDateTimeJST);
        LOG.fine(String.format("UTC time:%s",createtDateTimUTCStr));
        this.created_datetime_fmt=createtDateTimUTCStr;
    }
    
    @Override
    public String toString() {
        return "StockItem{" +
                "store_cd='" + store_cd + '\'' +
                ", created_datetime='" + created_datetime + '\'' +
                ", created_datetime_fmt='" + created_datetime_fmt + '\'' +
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
