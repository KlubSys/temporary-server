package com.klub.temporayStorageServer.app.service.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitDecompositionJobApiResponse {

    @JsonProperty("job_id")
    private String jobId;
}
