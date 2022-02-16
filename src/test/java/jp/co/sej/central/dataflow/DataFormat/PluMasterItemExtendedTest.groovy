package jp.co.sej.central.dataflow.DataFormat

import jp.co.sej.central.dataflow.Common.MyCSVFormat
import org.apache.commons.csv.CSVRecord
import spock.lang.Specification
import spock.lang.Unroll

class PluMasterItemExtendedTest extends Specification {
    static final String processing_date = "2021-03-23"
    static final String processing_datetime = "2021-03-23 01:01:30"
    static final String id = "20210323010101"
    static final Integer send_times = 1
    static final String created_datetime_utc = "2021-03-22 16:01:01"
    String file_contents = '''
"012345","20210323","9817855999998","780044","無料券""nｾﾞﾘｰ,ﾊﾞｰ各種  "," ","0","1","A","123","124","2","0","1","       1  ","0","1","100","00","0","000","000","000","00","00","00","0","1","100","00","0","000","000","000","00","00","00"," ","2","000","000","00","00","000","000","00","00"," "," 1","000","000","000","000","000","00","00","00","00","00","000","000","000","000","000","00","00","00","00","00"
'''

    CSVRecord line
    PluMasterItem pluMasterItem

    void setup() {
        line = MyCSVFormat.getMyCSVFormat().parse(new StringReader(file_contents)).getRecords().get(0)
        pluMasterItem = new PluMasterItem(processing_date, processing_datetime, id, send_times, created_datetime_utc,line)
    }

    def 'PluMasterItem正しく設定されること'() {
        when:
            PluMasterItem pl = pluMasterItem
        then:
            pl.id == "20210323010101"
            pl.send_times == 1
            pl.created_datetime_utc == "2021-03-22 16:01:01"
            pl.processing_date == "2021-03-23"
            pl.store_cd == "012345"
            pl.today_date == "20210323"
            pl.label_cd == "9817855999998"
            pl.item_cd == "780044"
            pl.item_name == "無料券\"nｾﾞﾘｰ,ﾊﾞｰ各種  "
            pl.sales_report_type == " "
            pl.link_content_flag == "0"
            pl.freshness_management_type == "1"
            pl.out_of_freshness_time_type == "A"
            pl.delivery_freshness == "123"
            pl.sales_freshness == "124"
            pl.freshness_date_unit == "2"
            pl.in_store_item_aggregate_flag == "0"
            pl.sales_stop_flag == "1"
            pl.payment_status_1 == "       1  "
            pl.unit_price_today == 0
            pl.tax_class_today == "1"
            pl.tax_rate_today == "100"
            pl.store_discount_class_today == "00"
            pl.store_price_up_down_today == 0
            pl.promotion_no_1_today == "000"
            pl.promotion_no_2_today == "000"
            pl.promotion_no_3_today == "000"
            pl.promotion_category_no_1_today == "00"
            pl.promotion_category_no_2_today == "00"
            pl.promotion_category_no_3_today == "00"
            pl.unit_price_next_day == 0
            pl.tax_class_next_day == "1"
            pl.tax_rate_next_day == "100"
            pl.store_discount_class_next_day == "00"
            pl.store_price_up_down_next_day == 0
            pl.promotion_no_1_next_day == "000"
            pl.promotion_no_2_next_day == "000"
            pl.promotion_no_3_next_day == "000"
            pl.promotion_category_no_1_next_day == "00"
            pl.promotion_category_no_2_next_day == "00"
            pl.promotion_category_no_3_next_day == "00"
            pl.seven_eleven_fair_target_flag == " "
            pl.duty_free_class == "2"
            pl.promotion_no_4_today == "000"
            pl.promotion_no_5_today == "000"
            pl.promotion_category_no_4_today == "00"
            pl.promotion_category_no_5_today == "00"
            pl.promotion_no_4_next_day == "000"
            pl.promotion_no_5_next_day == "000"
            pl.promotion_category_no_4_next_day == "00"
            pl.promotion_category_no_5_next_day == "00"
            pl.provisional_settlement_prohibition_flag == " "
            pl.payment_status_2 == " 1"
            pl.processing_datetime == "2021-03-23 01:01:30"
            pl.promotion_no_6_today == "000"
            pl.promotion_no_7_today == "000"
            pl.promotion_no_8_today == "000"
            pl.promotion_no_9_today == "000"
            pl.promotion_no_10_today == "000"
            pl.promotion_category_no_6_today == "00"
            pl.promotion_category_no_7_today == "00"
            pl.promotion_category_no_8_today == "00"
            pl.promotion_category_no_9_today == "00"
            pl.promotion_category_no_10_today == "00"
            pl.promotion_no_6_next_day == "000"
            pl.promotion_no_7_next_day == "000"
            pl.promotion_no_8_next_day == "000"
            pl.promotion_no_9_next_day == "000"
            pl.promotion_no_10_next_day == "000"
            pl.promotion_category_no_6_next_day == "00"
            pl.promotion_category_no_7_next_day == "00"
            pl.promotion_category_no_8_next_day == "00"
            pl.promotion_category_no_9_next_day == "00"
            pl.promotion_category_no_10_next_day == "00"
            pl.PROMOTION_EXTENDED == true
    }

    @Unroll
    def 'store_cd=#store_cd_valのデータに対してはareNumericValuesValidが#statusを返す'(){
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_cd = store_cd_val
        then:
            pl.areNumericValuesValid() == status
        where:
            store_cd_val || status
            "012345"     ||  true
            "01234a"     ||  false
            ""           ||  false
            null         ||   false
    }

    @Unroll
    def 'today_date=#today_date_valのデータに対してはisDateValueValidが#statusを返す'(){
        when:
            PluMasterItem pl = pluMasterItem
            pl.today_date = today_date_val
        then:
            pl.isDateValueValid() == status
        where:
            today_date_val || status
            "20210131"     ||  true
            "20210132"     ||  false
            ""           ||  false
            null         ||   false
    }

    def 'areMandatoryFieldCompleteがtrueを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
        then:
            pl.areMandatoryFieldsComplete() == true
    }

    def 'store_cdがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_cd = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'today_dateがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.today_date = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'label_cdがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.label_cd = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'item_cdがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.item_cd = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'item_nameがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.item_name = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'sales_report_typeがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.sales_report_type = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'link_content_flagがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.link_content_flag = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'freshness_management_typeがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.freshness_management_type = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'out_of_freshness_time_typeがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.out_of_freshness_time_type = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'delivery_freshnessがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.delivery_freshness = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'sales_freshnessがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.delivery_freshness = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'freshness_date_unitがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.delivery_freshness = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'in_store_item_aggregate_flagがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.in_store_item_aggregate_flag = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'sales_stop_flagがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.sales_stop_flag = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'payment_status_1がnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.payment_status_1 = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'unit_price_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.unit_price_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'tax_class_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.tax_class_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'tax_rate_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.tax_rate_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'store_discount_class_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_discount_class_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'store_price_up_down_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_price_up_down_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_no_1_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_1_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_no_2_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_2_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_no_3_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_3_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_category_no_1_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_1_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_2_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_2_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_category_no_3_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_3_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'unit_price_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.unit_price_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'tax_class_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.tax_class_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'tax_rate_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.tax_rate_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'store_discount_class_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_discount_class_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'store_price_up_down_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.store_price_up_down_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }

    def 'promotion_no_1_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_1_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_2_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_2_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_3_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_3_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_1_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_1_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_2_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_2_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_3_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_3_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'seven_eleven_fair_target_flagがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.seven_eleven_fair_target_flag = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'duty_free_classがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.duty_free_class = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_4_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_4_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_5_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_5_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_4_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_4_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_5_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_5_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_4_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_4_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_5_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_5_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_4_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_4_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_5_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_5_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'provisional_settlement_prohibition_flagがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.provisional_settlement_prohibition_flag = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'payment_status_2がnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.payment_status_2 = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_6_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_6_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_7_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_7_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_8_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_8_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_9_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_9_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_10_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_10_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_6_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_6_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_7_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_7_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_8_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_8_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_9_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_9_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_10_todayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_10_today = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_6_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_6_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_7_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_7_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_8_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_8_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_9_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_9_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_no_10_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_no_10_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_6_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_6_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_7_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_7_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_8_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_8_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_9_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_9_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
    def 'promotion_category_no_10_next_dayがnullならばareMandatoryFieldCompleteがfalseを返す' () {
        when:
            PluMasterItem pl = pluMasterItem
            pl.promotion_category_no_10_next_day = null
        then:
            pl.areMandatoryFieldsComplete() == false
    }
}
