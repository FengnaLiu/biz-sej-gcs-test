package jp.co.sej.central.dataflow;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;

public interface StockDataTransformerOptions extends GcpOptions {
    @Description("pubsub subscription")
    ValueProvider<String> getPubsubSubscriptionPath();
    void setPubsubSubscriptionPath(ValueProvider<String> value);

    @Description("Path of the table to write")
    ValueProvider<String> getOutputBQPath();
    void setOutputBQPath(ValueProvider<String> value);

    @Description("Path of the GcsTempLocation for using BigQuery")
    ValueProvider<String> getCustomGcsTempLocation();
    void setCustomGcsTempLocation(ValueProvider<String> value);

    @Description("Spanner InstanceId to write")
    ValueProvider<String> getSpannerInstanceId();
    void setSpannerInstanceId(ValueProvider<String> value);

    @Description("Spanner DatabseId to write")
    ValueProvider<String> getSpannerDatabaseId();
    void setSpannerDatabaseId(ValueProvider<String> value);

    @Description("Spanner ProjectId to write")
    ValueProvider<String> getSpannerProjectId();
    void setSpannerProjectId(ValueProvider<String> value);
}
