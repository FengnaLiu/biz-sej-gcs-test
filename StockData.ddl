CREATE TABLE Stock (
    commit_timestamp TIMESTAMP NOT NULL OPTIONS (allow_commit_timestamp=true),
    store_id INT64 NOT NUll,
    id STRING(14) NOT NULL,
    send_times INT64 NOT NULL,
    store_cd STRING(6) NOT NULL,
    created_datetime TIMESTAMP,
    item_cd STRING(6) NOT NULL,
    stock_amnt INT64 NOT NULL,
    item_handle_flag STRING(1) NOT NULL,
    non_handle_focus_item STRING(1) NOT NULL,
    mark_down_flag STRING(1) NOT NULL,
    item_handle_flag_2 STRING(1) NOT NULL,
    stock_date INT64 NOT NULL,
) PRIMARY KEY (store_id,item_cd),
INTERLEAVE IN PARENT StoreMaster ON DELETE CASCADE