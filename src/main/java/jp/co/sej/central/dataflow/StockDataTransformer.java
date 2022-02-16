package jp.co.sej.central.dataflow;

import jp.co.sej.central.dataflow.Common.StockDataSchema;
import jp.co.sej.central.dataflow.DataFormat.StockItem;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.InsertRetryPolicy;
import org.apache.beam.sdk.io.gcp.bigquery.WriteResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import java.util.List;
import java.util.logging.Logger;

public class StockDataTransformer {
    private static final Logger LOG=Logger.getLogger(StockDataTransformer.class.getName());

    public static void main(String[] args){
        LOG.info(System.getProperty("user.dir"));
        StockDataSchema stockDataSchema = new StockDataSchema();
        StockDataTransformerOptions opt = PipelineOptionsFactory.fromArgs(args).withValidation().as(StockDataTransformerOptions.class);
        Pipeline p = Pipeline.create(opt);

        PCollection<List<StockItem>> p1 = p
                .apply("ReadMessageFromPubsubSubscription", PubsubIO.readStrings().fromSubscription(opt.getPubsubSubscriptionPath()))
                .apply("GetFileContentsFromGcs", ParDo.of(new GetJsonFileContents()))
                .apply(ParDo.of(new StockItemExtractor()));

        WriteResult writeResult = p1
                .apply("ConvertTableRow",ParDo.of(new TransformFromItemToTableRow()))
                .apply("WriteToBigQuery", BigQueryIO.writeTableRows()
                        .to(opt.getOutputBQPath())
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND)
                        .withCustomGcsTempLocation(opt.getCustomGcsTempLocation())
                        .withFailedInsertRetryPolicy(InsertRetryPolicy.retryTransientErrors())
                        .withExtendedErrorInfo());

        writeResult
                .getFailedInsertsWithErr()
                .apply("PersistentErrorLog", ParDo.of(new PersistentErrorLog()));

        p1
                .apply("CheckConsistOfOneStoreOnly", ParDo.of(new CheckConsistOfOneStoreOnly()))
                .apply("CheckExistanceOfStoreMasterEntry", ParDo.of(new CheckExistanceOfStoreMasterEntry()))
                .apply("CheckDuplicateItems", ParDo.of(new CheckDuplicateItems()))
                .apply("WriteSpannerIfNewer", ParDo.of(new WriteSpannerIfNewer()));
        p.run();
    }

}
