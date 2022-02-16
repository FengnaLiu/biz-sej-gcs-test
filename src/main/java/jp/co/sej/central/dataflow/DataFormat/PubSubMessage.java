package jp.co.sej.central.dataflow.DataFormat;

/**
 * GCSのNotificationからPubSubトリガーされて生成されたPubSubMessageフォーマットを表すクラス
 */
public class PubSubMessage {
    public String kind;
    public String id;
    public String selfLink;
    public String name;
    public String bucket;
    public String generation;
    public String metageneration;
    public String contentType;
    public String timeCreated;
    public String updated;
    public String storageClass;
    public String timeStorageClassUpdated;
    public String size;
    public String md5Hash;
    public String mediaLink;
    public String contentLanguage;
    public String crc32c;
    public String etag;
}
