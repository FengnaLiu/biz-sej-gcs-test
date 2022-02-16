package jp.co.sej.central.dataflow.Common

import com.google.cloud.bigquery.Schema
import spock.lang.Specification

/**
 * 手動でBigQueryテーブル作成する際に使われているスキーマJSONファイルから生成されたcom.google.cloud.bigquery.Schemaオブジェクト
 * とパイプライン自動でBigQueryテーブル作成するさいに使われているものと同じであることを確認するためのテスト
 */
class GenerateSchemaFromJsonTest extends Specification {
    //手動でBigQueryテーブル作成する際に使われているJSONスキーマファイル
    static final String jsonFilePath = "PluMaster.json"

    //パイプライン自動でBigQueryテーブル作成するさいに、使われているcom.google.cloud.bigquery.Schemaオブジェクト
    static final Schema schema = PluBigQuerySchema.getPluBigQuerySchema()

    def 'BigQueryテーブル手動で作成する際に使われているスキーマと自動で作成された際に使われているもの一致すること'() {
        when:
            GenerateSchemaFromJson schemaFromJson = new GenerateSchemaFromJson(jsonFilePath)
        then:
            schemaFromJson.schema == schema

    }
}
