package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetDataBlockRefResponse {
    private String reference;
}
