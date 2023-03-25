package com.klub.temporayStorageServer.app.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CentralServerLogMessage {
    private String from;
    private String text;
}