package jp.co.sej.central.dataflow;

import com.google.api.services.bigquery.model.TableRow;

public interface IBigqueryTransform {
    TableRow toTableRow();
}
