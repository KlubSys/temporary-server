package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup;


import com.klub.temporayStorageServer.app.service.api.storeClient.dto.BlockGroupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @class SaveBlockGroupResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetAllBlockGroupResponse {
    /**
     * The main body
     */
    private List<BlockGroupDto> data;
}
