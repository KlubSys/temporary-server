package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

 /**
 * @class GetRandomOnlineStoreResponse default representation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetRandomOnlineStoreResponse {
    /**
     * The store reference found of not
     */
    private String storeRef;
}
