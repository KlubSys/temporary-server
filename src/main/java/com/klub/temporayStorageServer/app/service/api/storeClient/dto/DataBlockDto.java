package com.klub.temporayStorageServer.app.service.api.storeClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class DataBlockDto default representation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBlockDto {
    private Long id;
    private String identifier;
    private Integer size;
    private String data;
    private String blockGroupRef;
    private String dataStoreRef;
}
