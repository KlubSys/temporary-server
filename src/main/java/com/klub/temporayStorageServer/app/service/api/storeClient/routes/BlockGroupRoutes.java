package com.klub.temporayStorageServer.app.service.api.storeClient.routes;

/**
 * @class BlockGroupRoutes holds urls used in block group controller
 */
public class BlockGroupRoutes {
    public static final String BLOCK_GROUP_BASE_URL =  "/api/v1/block_groups";
    public static final String BLOCK_GROUP_FIND_NEXT_URL = BLOCK_GROUP_BASE_URL + "/{currentRef}/_nextBlock";

    /**
     * @value ""
     * @return
     */
    public static String getCreateUrl(){
        return BLOCK_GROUP_BASE_URL;
    }

    /**
     * @value "/{currentRef}/_nextBlock"
     * @param currentBlockRef
     * @return
     */
    public static String getGetGroupFindNextUrl(String currentBlockRef){
        return BLOCK_GROUP_FIND_NEXT_URL.replace("{currentRef}", currentBlockRef);
    }
}
