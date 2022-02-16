package jp.co.sej.central.dataflow;

import org.apache.beam.sdk.io.gcp.bigquery.BigQueryInsertError;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.logging.Logger;

public class PersistentErrorLog extends DoFn<BigQueryInsertError, String> {
    private static final Logger LOG = Logger.getLogger(PersistentErrorLog.class.getName());

    @ProcessElement
    public void processElement(@Element BigQueryInsertError error){
        LOG.severe(String.format("Persistent error occurred. [PubSubMessage: %s], error: %s", error.getRow(), error.getError()));
    }
}
