package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.store;

import com.klub.temporayStorageServer.app.service.api.storeClient.dto.StoreDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class SaveStoreResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaveStoreResponse {
    /**
     * main body data
     */
    private StoreDto data;
}
