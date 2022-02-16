## 事前準備
BigQueryテーブル作成

`$ bq mk --table --time_partitioning_field created_datetime ${PROJECT_ID}:stock_test.StockData ./StockData.json`

Spannerテーブル作成

`$ gcloud spanner databases ddl update store-items --instance=seven-central-dwh --ddl="$(cat StockData.ddl)" --project=${PROJECT_ID}`

## 実行方法
### テストコード実行

```
$ mvn -Pdirect-runner compile test -Dexec.mainClass=jp.co.sej.central.dataflow.StockDataTransformer -Dexec.args=--project=${PROJECT_ID}
```

### local実行

```
$ mvn -Pdirect-runner compile exec:java -Dexec.mainClass=jp.co.sej.central.dataflow.StockDataTransformer \
       -Dexec.args="--project=${PROJECT_ID} \
                    --pubsubSubscriptionPath=${INPUT_SUBSCRIPTION_PATH} \
                    --outputBQPath=${OUTPUT_BQ_PATH} \
                    --customGcsTempLocation=${CustomGcsTempLocation} \
                    --runner=DirectRunner"`
```

### GCP上で実行

```
$ mvn -Pdataflow-runner compile exec:java -Dexec.mainClass=jp.co.sej.central.dataflow.StockDataTransformer \
       -Dexec.args="--project=${PROJECT_ID} \
                    --region=asia-northeast1 \
                    --stagingLocation=gs://${YOUR_BUCKET}/staging \
                    --network=dev-dwh \
                    --subnetwork=regions/asia-northeast1/subnetworks/dev-dwh \
                    --enableStreamingEngine \
                    --serviceAccount=${serviceAccount} \ 
                    --pubsubSubscriptionPath=${INPUT_SUBSCRIPTION_PATH} \
                    --outputBQPath=${OUTPUT_BQ_PATH} \
                    --customGcsTempLocation=${CustomGcsTempLocation} \
                    --runner=DataflowRunner"
```

## サンプルデータをpubsubへpublish

```
$ gcloud pubsub topics publish projects/${PROJECT_ID}/topics/${TOPIC_NAME} --message="$(cat testData/Json_stock_test.json)"
```
