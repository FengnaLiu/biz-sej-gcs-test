package jp.co.sej.central.dataflow.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * 連携されたCSVファイルのURLから必要な情報を抽出するためのクラスである。
 */
public class ParseFileDirectory {
    private static final Logger LOG = Logger.getLogger(ParseFileDirectory.class.getName());
    private String created_datetime;
    private String send_times;
    private String store_cd;

    /**
     * 連携されたCSVファイルのURLからcreated_datetime,send_times,store_cdを抽出する。
     *
     * @param file_directory
     */
    public ParseFileDirectory(String file_directory) {
        String[] file_directory_parts = file_directory.split("/");
        String file_name = file_directory_parts[file_directory_parts.length - 1];
        LOG.info(file_name);
        String [] file_name_parts = file_name.split("_");
        this.send_times = file_name_parts[file_name_parts.length-1].replace(".json","");
        this.created_datetime = file_name_parts[file_name_parts.length-2];
        this.store_cd = file_name_parts[file_name_parts.length - 3];
        LOG.fine(String.format("%s,%s,%s", send_times, store_cd, created_datetime));
    }

    /**
     * 数値が来るべきところに数値が来ているかどうかをチェックする
     *
     * @return チェック結果に問題があればfalse、なければtrue
     */
    public boolean areNumericValuesValid() {
        try {
            Integer.parseInt(send_times);
            Integer.parseInt(store_cd);
        } catch (NumberFormatException nfe) {
            LOG.warning(nfe.getMessage());
            return false;
        }
        return true;
    }

    /**
     * タイムスタンプ文字列が来るべきところに日付文字列が来ているかどうかをチェックする
     *
     * @return チェック結果に問題があればfalse、なければtrue
     */
    public boolean isCreatedDateTimeValueValid() {
        if (Objects.isNull(created_datetime)) return false;
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

    /**
     * CSVファイル中のcreated_datetimeのJST文字列(yyyyMMddHHmmss)からUTCの文字列を取得する。
     *
     * @return UTCの文字列(yyyy-MM-dd HH:mm:ss)
     */
    public String getCreatedDatetimeUTC() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        SimpleDateFormat udf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        udf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String created_datetime_utc =udf.format(sdf.parse(created_datetime));
        return created_datetime_utc;
    }

    public String getCreatedDatetime(){
        return created_datetime;
    }

    public Integer getIntegerSendTimes() {
        return Integer.valueOf(send_times);
    }

    public String getStoreCd() {
        return store_cd;
    }

}
