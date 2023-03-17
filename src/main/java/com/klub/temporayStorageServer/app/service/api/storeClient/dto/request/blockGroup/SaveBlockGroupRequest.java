package com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.blockGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class SaveBlockGroupRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SaveBlockGroupRequest {
    private String next;
    private String previous;

    private String data;
    private Integer dataSize;
}
