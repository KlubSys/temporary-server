package com.klub.temporayStorageServer.app.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CentralServerLogMessage {
    private String from;
    private String text;


    private final HashMap<String, Object> data = new HashMap<>();

    public CentralServerLogMessage addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}