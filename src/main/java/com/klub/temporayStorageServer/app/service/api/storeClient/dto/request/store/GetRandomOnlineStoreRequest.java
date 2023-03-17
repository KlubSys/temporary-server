package com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class GetRandomOnlineStoreRequest Request made while distributing, the klub system wanted
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetRandomOnlineStoreRequest {
    /**
     * The data size that must be available
     */
    private Integer dataSize;

    /**
     * The block group ref
     */
    private String blockGroupRef;
}
