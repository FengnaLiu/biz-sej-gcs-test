package jp.co.sej.central.dataflow

import jp.co.sej.central.dataflow.DataFormat.PluMasterItem
import spock.lang.Specification
import java.text.SimpleDateFormat

class PluMasterItemExtractorExtendedTest extends Specification{
    static final String PROCESSING_DATETIME = "2021-03-23 12:34:56"
    static final id = "202103230101010"
    static final Integer send_times = 1
    static final String created_datetime_utc = "2021-03-22 16:01:01"
    static final String file_directory = "012345/plu_012345_202103230101010_1"
    String file_contents = '''
"012345","20210323","9817855999998","780044","無料券""nｾﾞﾘｰ,ﾊﾞｰ各種  "," ","0","1","A","123","124","2","0","1","       1  ","0","1","100","00","0","000","000","000","00","00","00","0","1","100","00","0","000","000","000","00","00","00"," ","2","000","000","00","00","000","000","00","00"," "," 1","000","000","000","000","000","00","00","00","00","00","000","000","000","000","000","00","00","00","00","00"
"012345","20210323","9817855999997","780045","無料券""nｾﾞﾘｰ\\ﾊﾞｰ各種  "," ","0","1","A","123","124","2","0","1","       1  ","0","1","100","00","0","000","000","000","00","00","00","0","1","100","00","0","000","000","000","00","00","00"," ","2","000","000","00","00","000","000","00","00"," "," 1","000","000","000","000","000","00","00","00","00","00","000","000","000","000","000","00","00","00","00","00"
'''
    def 'getProcessingDatetime正しくprocesing_datetime_strを返すこと'(){
        when:
            PluMasterItemExtractor mc = new PluMasterItemExtractor()
            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            jdf.setTimeZone(TimeZone.getTimeZone("JST"))
            String processing_datetime_utc_str = mc.getProcessingDatetime(jdf.parse(PROCESSING_DATETIME))
        then:
            processing_datetime_utc_str=="2021-03-23 03:34:56"
    }

    def 'getProcessingDate正しくprocesing_dateを返すこと'(){
        when:
            PluMasterItemExtractor mc = new PluMasterItemExtractor()
            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            jdf.setTimeZone(TimeZone.getTimeZone("JST"))
            String processing_date = mc.getProcessingDate(jdf.parse(PROCESSING_DATETIME))
        then:
            processing_date=="2021-03-23"
    }

    def 'getPluMasterItems正しくList<PluMasterItem>を返すこと'() {
        setup:
            PluMasterItem pluMasterItem1 = new PluMasterItem()
            PluMasterItem pluMasterItem2 = new PluMasterItem()
            pluMasterItem1.id = id
            pluMasterItem1.send_times = send_times
            pluMasterItem1.created_datetime_utc = created_datetime_utc
            pluMasterItem1.processing_date = "2021-03-23"
            pluMasterItem1.store_cd = "012345"
            pluMasterItem1.today_date = "2021-03-23"
            pluMasterItem1.label_cd = "9817855999998"
            pluMasterItem1.item_cd = "780044"
            pluMasterItem1.item_name = "無料券\"nｾﾞﾘｰ,ﾊﾞｰ各種  "
            pluMasterItem1.sales_report_type = " "
            pluMasterItem1.link_content_flag = "0"
            pluMasterItem1.freshness_management_type = "1"
            pluMasterItem1.out_of_freshness_time_type = "A"
            pluMasterItem1.delivery_freshness = "123"
            pluMasterItem1.sales_freshness = "124"
            pluMasterItem1.freshness_date_unit = "2"
            pluMasterItem1.in_store_item_aggregate_flag = "0"
            pluMasterItem1.sales_stop_flag = "1"
            pluMasterItem1.payment_status_1 = "       1  "
            pluMasterItem1.unit_price_today = 0
            pluMasterItem1.tax_class_today = "1"
            pluMasterItem1.tax_rate_today = "100"
            pluMasterItem1.store_discount_class_today = "00"
            pluMasterItem1.store_price_up_down_today = 0
            pluMasterItem1.promotion_no_1_today = "000"
            pluMasterItem1.promotion_no_2_today = "000"
            pluMasterItem1.promotion_no_3_today = "000"
            pluMasterItem1.promotion_category_no_1_today = "00"
            pluMasterItem1.promotion_category_no_2_today = "00"
            pluMasterItem1.promotion_category_no_3_today = "00"
            pluMasterItem1.unit_price_next_day = 0
            pluMasterItem1.tax_class_next_day = "1"
            pluMasterItem1.tax_rate_next_day = "100"
            pluMasterItem1.store_discount_class_next_day = "00"
            pluMasterItem1.store_price_up_down_next_day = 0
            pluMasterItem1.promotion_no_1_next_day = "000"
            pluMasterItem1.promotion_no_2_next_day = "000"
            pluMasterItem1.promotion_no_3_next_day = "000"
            pluMasterItem1.promotion_category_no_1_next_day = "00"
            pluMasterItem1.promotion_category_no_2_next_day = "00"
            pluMasterItem1.promotion_category_no_3_next_day = "00"
            pluMasterItem1.seven_eleven_fair_target_flag = " "
            pluMasterItem1.duty_free_class = "2"
            pluMasterItem1.promotion_no_4_today = "000"
            pluMasterItem1.promotion_no_5_today = "000"
            pluMasterItem1.promotion_category_no_4_today = "00"
            pluMasterItem1.promotion_category_no_5_today = "00"
            pluMasterItem1.promotion_no_4_next_day = "000"
            pluMasterItem1.promotion_no_5_next_day = "000"
            pluMasterItem1.promotion_category_no_4_next_day = "00"
            pluMasterItem1.promotion_category_no_5_next_day = "00"
            pluMasterItem1.provisional_settlement_prohibition_flag = " "
            pluMasterItem1.payment_status_2 = " 1"
            pluMasterItem1.processing_datetime = "2021-03-23 03:34:56"
            pluMasterItem1.promotion_no_6_today = "000"
            pluMasterItem1.promotion_no_7_today = "000"
            pluMasterItem1.promotion_no_8_today = "000"
            pluMasterItem1.promotion_no_9_today = "000"
            pluMasterItem1.promotion_no_10_today = "000"
            pluMasterItem1.promotion_category_no_6_today = "00"
            pluMasterItem1.promotion_category_no_7_today = "00"
            pluMasterItem1.promotion_category_no_8_today = "00"
            pluMasterItem1.promotion_category_no_9_today = "00"
            pluMasterItem1.promotion_category_no_10_today = "00"
            pluMasterItem1.promotion_no_6_next_day = "000"
            pluMasterItem1.promotion_no_7_next_day = "000"
            pluMasterItem1.promotion_no_8_next_day = "000"
            pluMasterItem1.promotion_no_9_next_day = "000"
            pluMasterItem1.promotion_no_10_next_day = "000"
            pluMasterItem1.promotion_category_no_6_next_day = "00"
            pluMasterItem1.promotion_category_no_7_next_day = "00"
            pluMasterItem1.promotion_category_no_8_next_day = "00"
            pluMasterItem1.promotion_category_no_9_next_day = "00"
            pluMasterItem1.promotion_category_no_10_next_day = "00"
            pluMasterItem1.PROMOTION_EXTENDED = true

            pluMasterItem2.id = id
            pluMasterItem2.send_times = send_times
            pluMasterItem2.created_datetime_utc = created_datetime_utc
            pluMasterItem2.processing_date = "2021-03-23"
            pluMasterItem2.store_cd = "012345"
            pluMasterItem2.today_date = "2021-03-23"
            pluMasterItem2.label_cd = "9817855999997"
            pluMasterItem2.item_cd = "780045"
            pluMasterItem2.item_name = "無料券\"nｾﾞﾘｰ\\ﾊﾞｰ各種  "
            pluMasterItem2.sales_report_type = " "
            pluMasterItem2.link_content_flag = "0"
            pluMasterItem2.freshness_management_type = "1"
            pluMasterItem2.out_of_freshness_time_type = "A"
            pluMasterItem2.delivery_freshness = "123"
            pluMasterItem2.sales_freshness = "124"
            pluMasterItem2.freshness_date_unit = "2"
            pluMasterItem2.in_store_item_aggregate_flag = "0"
            pluMasterItem2.sales_stop_flag = "1"
            pluMasterItem2.payment_status_1 = "       1  "
            pluMasterItem2.unit_price_today = 0
            pluMasterItem2.tax_class_today = "1"
            pluMasterItem2.tax_rate_today = "100"
            pluMasterItem2.store_discount_class_today = "00"
            pluMasterItem2.store_price_up_down_today = 0
            pluMasterItem2.promotion_no_1_today = "000"
            pluMasterItem2.promotion_no_2_today = "000"
            pluMasterItem2.promotion_no_3_today = "000"
            pluMasterItem2.promotion_category_no_1_today = "00"
            pluMasterItem2.promotion_category_no_2_today = "00"
            pluMasterItem2.promotion_category_no_3_today = "00"
            pluMasterItem2.unit_price_next_day = 0
            pluMasterItem2.tax_class_next_day = "1"
            pluMasterItem2.tax_rate_next_day = "100"
            pluMasterItem2.store_discount_class_next_day = "00"
            pluMasterItem2.store_price_up_down_next_day = 0
            pluMasterItem2.promotion_no_1_next_day = "000"
            pluMasterItem2.promotion_no_2_next_day = "000"
            pluMasterItem2.promotion_no_3_next_day = "000"
            pluMasterItem2.promotion_category_no_1_next_day = "00"
            pluMasterItem2.promotion_category_no_2_next_day = "00"
            pluMasterItem2.promotion_category_no_3_next_day = "00"
            pluMasterItem2.seven_eleven_fair_target_flag = " "
            pluMasterItem2.duty_free_class = "2"
            pluMasterItem2.promotion_no_4_today = "000"
            pluMasterItem2.promotion_no_5_today = "000"
            pluMasterItem2.promotion_category_no_4_today = "00"
            pluMasterItem2.promotion_category_no_5_today = "00"
            pluMasterItem2.promotion_no_4_next_day = "000"
            pluMasterItem2.promotion_no_5_next_day = "000"
            pluMasterItem2.promotion_category_no_4_next_day = "00"
            pluMasterItem2.promotion_category_no_5_next_day = "00"
            pluMasterItem2.provisional_settlement_prohibition_flag = " "
            pluMasterItem2.payment_status_2 = " 1"
            pluMasterItem2.processing_datetime = "2021-03-23 03:34:56"
            pluMasterItem2.promotion_no_6_today = "000"
            pluMasterItem2.promotion_no_7_today = "000"
            pluMasterItem2.promotion_no_8_today = "000"
            pluMasterItem2.promotion_no_9_today = "000"
            pluMasterItem2.promotion_no_10_today = "000"
            pluMasterItem2.promotion_category_no_6_today = "00"
            pluMasterItem2.promotion_category_no_7_today = "00"
            pluMasterItem2.promotion_category_no_8_today = "00"
            pluMasterItem2.promotion_category_no_9_today = "00"
            pluMasterItem2.promotion_category_no_10_today = "00"
            pluMasterItem2.promotion_no_6_next_day = "000"
            pluMasterItem2.promotion_no_7_next_day = "000"
            pluMasterItem2.promotion_no_8_next_day = "000"
            pluMasterItem2.promotion_no_9_next_day = "000"
            pluMasterItem2.promotion_no_10_next_day = "000"
            pluMasterItem2.promotion_category_no_6_next_day = "00"
            pluMasterItem2.promotion_category_no_7_next_day = "00"
            pluMasterItem2.promotion_category_no_8_next_day = "00"
            pluMasterItem2.promotion_category_no_9_next_day = "00"
            pluMasterItem2.promotion_category_no_10_next_day = "00"
            pluMasterItem2.PROMOTION_EXTENDED = true
            List<PluMasterItem> list = new ArrayList<>()
            list.add(pluMasterItem1)
            list.add(pluMasterItem2)
        when:
            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            jdf.setTimeZone(TimeZone.getTimeZone("JST"))
            Date date = jdf.parse(PROCESSING_DATETIME)
            PluMasterItemExtractor mc = new PluMasterItemExtractor()
            List<PluMasterItem> out = mc.getPluMasterItems(file_contents,id,send_times,created_datetime_utc,date,file_directory)
        then:
            out == list
        

    }

}
