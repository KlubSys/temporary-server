package com.klub.temporayStorageServer.app.service.api.storeClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class StoreDto default representation of a store
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreDto {
    private Long id;
    private String identifier;
    private Boolean online = false;
    private Integer size = 1000; //TODO CHANGE TO 1 MILLION AS NECESSARY
    private Integer free = 100;
}
