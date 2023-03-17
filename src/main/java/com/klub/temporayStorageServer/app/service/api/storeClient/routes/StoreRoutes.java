package com.klub.temporayStorageServer.app.service.api.storeClient.routes;

/**
 * @class StoreRoutes holds urls used on the store controller
 */
public class StoreRoutes {

    public static final String STORE_BASE_URL = "/api/v1/stores";
    public static final String STORE_UPDATE_SIZE_URL = STORE_BASE_URL + "/{store_id}/_size/{value}";
    public static final String STORE_UPDATE_ONLINE_URL = STORE_BASE_URL + "/{store_id}/_online/{value}";
    public static final String STORE_GET_SINGLE_URL = STORE_BASE_URL + "/{store_id}";
    public static final String STORE_GET_DATA_BLOCKS_URL = STORE_BASE_URL + "/{store_id}/data_block";
    public static final String STORE_GET_DATA_BLOCK_URL = STORE_BASE_URL + "/{store_id}/data_block/{data_block_id}";
    public static final String STORE_GET_RANDOM_ONLINE_URL = STORE_BASE_URL + "/_online/_random";

    public static final String STORE_GET_RANDOM_ONLINE_URL2 = STORE_BASE_URL + "/_online/_random?dataSize={dataSize}&blocGroupRef={blocGroupRef}";
    public static final String STORE_GET_BY_BLOCK_GROUP_REF_URL = STORE_BASE_URL + "/_blockGroup/{ref}";

    public static String getStoreBaseUrl() {
        return STORE_BASE_URL;
    }

    public static String getCreateUrl() {
        return STORE_BASE_URL + "";
    }

    public static String getByIdUrl(String storeId) {
        return STORE_GET_SINGLE_URL.replace("{store_id}", storeId);
    }

    /**
     * Get all the stores
     *
     * @return
     */
    public static String getAll() {
        return "";
    }

    /**
     * Update the store size
     *
     * @param storeId
     * @param value
     * @return
     */
    public static String getUpdateSizeUrl(String storeId, int value) {
        return STORE_UPDATE_SIZE_URL.replace("{store_id}", storeId)
                .replace("{value}", String.valueOf(value));
    }

    /**
     * Update the online state: on or off
     *
     * @param storeId
     * @param state
     * @return
     */
    public static String getUpdateOnlineUrl(String storeId, String state) {
        return STORE_UPDATE_ONLINE_URL.replace("{store_id}", storeId)
                .replace("{value}", state);
    }

    /**
     * Get all data bloc in a store
     *
     * @param storeId
     * @return
     */
    public static String getGetDataBlocksUrl(String storeId) {
        return STORE_GET_DATA_BLOCKS_URL.replace("{store_id}", storeId);
    }

    /**
     * Get a single data block from a data store
     *
     * @param storeId
     * @param dataBlockId
     * @return
     */
    public static String getGetDataBlockUrl(String storeId, String dataBlockId) {
        return STORE_GET_DATA_BLOCK_URL.replace("{store_id}", storeId)
                .replace("{data_block_id}", dataBlockId);
    }

    /**
     * Get a random store with online status on
     *
     * @return
     */
    public static String getGetRandomOnlineUrl() {
        return STORE_GET_RANDOM_ONLINE_URL;
    }

    /**
     * Get a random store with online status on
     *
     * @return
     */
    public static String getGetRandomOnlineUrl(int dataSize, String blocGroupRef) {
        return STORE_GET_RANDOM_ONLINE_URL2.replace("{blocGroupRef}", blocGroupRef)
                .replace("{dataSize}", String.valueOf(dataSize));
    }

    /**
     * Get url for get store by block reference identifier
     *
     * @param blockGroupRef
     * @return
     */
    public static String getStoreGetByBlockGroupRefUrl(String blockGroupRef) {
        return STORE_GET_BY_BLOCK_GROUP_REF_URL.replace("{ref}", blockGroupRef);
    }
}