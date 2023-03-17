package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock;

import com.klub.temporayStorageServer.app.service.api.storeClient.dto.DataBlockDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @class SaveDataBlockResponse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetAllDataBlockResponse {
    /**
     * main body data
     */
    private List<DataBlockDto> data;
}
