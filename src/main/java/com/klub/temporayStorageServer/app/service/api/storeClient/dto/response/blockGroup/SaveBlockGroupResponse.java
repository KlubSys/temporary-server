package com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup;

import com.klub.temporayStorageServer.app.service.api.storeClient.dto.BlockGroupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @class SaveBlockGroupResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveBlockGroupResponse {
    /**
     * The main body
     */
    private BlockGroupDto data;
}
