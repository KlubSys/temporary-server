package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock;

import com.klub.temporayStorageServer.app.service.api.storeClient.dto.DataBlockDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class SaveDataBlockResponse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SaveDataBlockResponse {
    /**
     * main body data
     */
    private DataBlockDto data;
}
