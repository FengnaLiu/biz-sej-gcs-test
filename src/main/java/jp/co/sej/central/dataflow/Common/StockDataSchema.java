package jp.co.sej.central.dataflow.Common;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;

import java.util.ArrayList;
import java.util.List;

public class StockDataSchema {
    public TableSchema toTableSchema(){
        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("store_id").setType("INTEGER").setMode("REQUIRED").setDescription("店舗ID"));
        fields.add(new TableFieldSchema().setName("id").setType("STRING").setMode("REQUIRED").setDescription("通番"));
        fields.add(new TableFieldSchema().setName("send_times").setType("INTEGER").setMode("REQUIRED").setDescription("送信回数"));
        fields.add(new TableFieldSchema().setName("store_cd").setType("STRING").setMode("REQUIRED").setDescription("店舗コード"));
        fields.add(new TableFieldSchema().setName("created_datetime").setType("TIMESTAMP").setMode("REQUIRED").setDescription("年月日時分秒"));
        fields.add(new TableFieldSchema().setName("item_cd").setType("STRING").setMode("REQUIRED").setDescription("商品CD"));
        fields.add(new TableFieldSchema().setName("stock_amnt").setType("INTEGER").setMode("REQUIRED").setDescription("在庫数"));
        fields.add(new TableFieldSchema().setName("item_handle_flag").setType("STRING").setMode("REQUIRED").setDescription("取扱商品フラグ"));
        fields.add(new TableFieldSchema().setName("non_handle_focus_item").setType("STRING").setMode("REQUIRED").setDescription("未導入重点商品フラグ"));
        fields.add(new TableFieldSchema().setName("mark_down_flag").setType("STRING").setMode("REQUIRED").setDescription("処分商品フラグ"));
        fields.add(new TableFieldSchema().setName("item_handle_flag_2").setType("STRING").setMode("REQUIRED").setDescription("取扱区分"));
        fields.add(new TableFieldSchema().setName("stock_date").setType("INTEGER").setMode("REQUIRED").setDescription("在庫引継日数"));
        fields.add(new TableFieldSchema().setName("processing_datetime").setType("TIMESTAMP").setMode("REQUIRED").setDescription("更新日時"));
        TableSchema tableSchema = new TableSchema();
        tableSchema.setFields(fields);

        return tableSchema;
    }
}
