package jp.co.sej.central.dataflow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.ServiceOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jp.co.sej.central.dataflow.Common.ParseFileDirectory;
import jp.co.sej.central.dataflow.DataFormat.PubSubMessage;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * GCSからCSVをよみこみためのDoFn
 * <p>
 * 処理しているファイルの「created_datetime」は「Memorystore redis」に登録している「created_datetime」より古い場合Skipする。
 * そうではなければGCSからCSVファイルを読み込む
 */
public class GetJsonFileContents extends DoFn<String, KV<String, String>> {
    static final ObjectMapper om;
    static StorageOptions options = StorageOptions.newBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build();
    static Storage storage = options.getService();

    static {
        om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }

    private static final Logger LOG = Logger.getLogger(GetJsonFileContents.class.getName());

    @ProcessElement
    public void processElement(ProcessContext c) {
        String mes = c.element();
        LOG.fine("pubsub message string: " + mes);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        sdf.setLenient(false);

        try {
            long startDateSec = new Date().getTime();
            PubSubMessage pubSubMessage = om.readValue(mes, PubSubMessage.class);
            String file_directory = pubSubMessage.name;
            ParseFileDirectory parseFileDirectory = new ParseFileDirectory(file_directory);
            if (!parseFileDirectory.areNumericValuesValid()) {
                LOG.severe(String.format("store_cd or send_times in file name(%s) is not numeric.", file_directory));
                return;
            } else if (!parseFileDirectory.isCreatedDateTimeValueValid()) {
                LOG.severe(String.format("created_datetime in file name(%s) is illegal.", file_directory));
                return;
            }

            Blob blob = storage.get(pubSubMessage.bucket, file_directory);
            String updated = pubSubMessage.updated;
            String value = new String(blob.getContent(), StandardCharsets.UTF_8);
            LOG.fine("File contents:" + value);
            c.output(KV.of(file_directory, value));
            long endDateSec = new Date().getTime();
            LOG.fine(String.format("Read file contents for file(%s), whole:%d seconds, start:%d, end:%d", file_directory, (endDateSec - startDateSec) / 1000, startDateSec, endDateSec));
        } catch (Exception e) {
            LOG.severe(String.format("unexpected error occurred: %s. mes:[%s]", e.getMessage(), mes));
            for (StackTraceElement ste : e.getStackTrace()) {
                LOG.warning(ste.toString());
            }
        }

    }


}
