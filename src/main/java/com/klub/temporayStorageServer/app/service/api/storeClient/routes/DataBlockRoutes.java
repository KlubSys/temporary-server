package com.klub.temporayStorageServer.app.service.api.storeClient.routes;

public class DataBlockRoutes {
    public static final String DATA_BLOCK_BASE_URL =  "/api/v1/data_block";
    public static final String DATA_BLOCK_CREATE_URL =  DATA_BLOCK_BASE_URL + "";
    public static final String DATA_BLOCK_UPDATE_DOWNLOAD_STATE =
            DATA_BLOCK_BASE_URL + "/{block_id}/_download/{value}";
    public static final String DATA_BLOCK_GET_REF_BY_BLOCK_GROUP_URL =
            DATA_BLOCK_BASE_URL + "/_block_group/{block_group_id}/_store/{store_id}/_ref";

    /**
     * Create data block url
     *
     * @return
     */
    public static String getCreateUrl(){
        return DATA_BLOCK_CREATE_URL;
    }

    /**
     * Data block reference by a block group
     *
     * @param blockGroupId
     * @param storeId
     * @return
     */
    public static String getGetDataBlockGetRefByBlockGroupUrl(String blockGroupId, String storeId){
        return DATA_BLOCK_GET_REF_BY_BLOCK_GROUP_URL
                .replace("{block_group_id}", blockGroupId)
                .replace("{store_id}", storeId);
    }

    public static String getUpdateDataBlockDownloadStateUrl(String blockId, Boolean value){
        return DATA_BLOCK_UPDATE_DOWNLOAD_STATE
                .replace("{block_id}", blockId)
                .replace("{value}", value.toString());
    }


}
