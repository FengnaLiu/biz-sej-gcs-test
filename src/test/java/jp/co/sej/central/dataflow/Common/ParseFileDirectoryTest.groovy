package jp.co.sej.central.dataflow.Common

import spock.lang.Specification
import spock.lang.Unroll

class ParseFileDirectoryTest extends Specification{
    static final String file_directory = "plu_master_csv_files_21000_30000_20210327/plu_012473_20210327034442_2.csv"
    ParseFileDirectory parseFileDirectory

    def setup() {
        parseFileDirectory = new ParseFileDirectory(file_directory)
    }

    def 'file_directory正しくParseされること'() {
        when:
            ParseFileDirectory pfd = parseFileDirectory
        then:
            pfd.created_datetime == "20210327034442"
            pfd.store_cd == "012473"
            pfd.send_times == "2"
    }
    def 'getCreatedDatetime正しく値を取得できること'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
        then:
            pfd.getCreatedDatetime() == "20210327034442"
    }

    def 'getCreatedDatetimeUTC正しく値を取得できること'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
        then:
            pfd.getCreatedDatetimeUTC() == "2021-03-26 18:44:42"
    }

    def 'getIntegerSendTimes正しく値を取得できること'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
        then:
            pfd.getIntegerSendTimes() == 2
    }

    def 'getStoreCd正しく値を取得できること'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
        then:
            pfd.getStoreCd() == "012473"
    }

    @Unroll
    def 'created_datetime=#created_datetime_valのデータに対してはisCreatedDateTimeValueValidが#statusを返す'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
            pfd.created_datetime = created_datetime_val
        then:
            pfd.isCreatedDateTimeValueValid() == status
        where:
            created_datetime_val || status
            "20200131000000"     || true
            "20200132000000"     || false
            "202001310000b0"     || false
            ""                   || false
            null                 || false
    }

    @Unroll
    def 'store_cd=#store_cd_val, send_times=#send_times_valの組のデータに対してはareNumericValuesValidが#statusを返す'(){
        when:
            ParseFileDirectory pfd = parseFileDirectory
            pfd.store_cd = store_cd_val
            pfd.send_times = send_times_val
        then:
            pfd.areNumericValuesValid() == status
        where:
            store_cd_val || send_times_val || status
            "012345"     ||  "1"           ||  true
            "01234a"     ||  "1"           ||  false
            "012345"     ||  "l"           ||  false
            ""           ||  "1"           ||  false
            null         ||  "1"           ||   false
            "012345"     ||  ""            ||   false
            "012345"     ||  null          ||   false
    }

}
