package com.klub.temporayStorageServer.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.klub.temporayStorageServer.app.helper.JobStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileDecompositionJobPayload {
    @JsonProperty("file_id")
    private String fileId;
    /**
     * The name of the file in the temporary storage
     */
    @JsonProperty("filename")
    private String filename;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_status")
    private JobStatusEnum jobStatus;
}