package com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.datablock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class SaveDataBlockRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaveDataBlockRequest {
    private String data;
    private Integer size;
    private String store;
    private String blockGroup;
}
