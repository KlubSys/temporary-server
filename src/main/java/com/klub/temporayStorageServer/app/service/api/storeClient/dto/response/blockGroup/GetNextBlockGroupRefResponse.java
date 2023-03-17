package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class GetNextBlockGroupRefResponse default representation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetNextBlockGroupRefResponse {
    /**
     * The next block reference
     */
    private String ref;
}
