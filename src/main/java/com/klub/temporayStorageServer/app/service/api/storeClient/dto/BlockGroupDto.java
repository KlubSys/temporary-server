package com.klub.temporayStorageServer.app.service.api.storeClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class BlockGroupDto default representation of a store
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockGroupDto {
    private Long id;
    private String identifier;

    private Integer deployed = 0;
    private Integer maxBlock;
    private String next;
    private String previous;
    private String data;
    private Integer dataSize;
}
