package com.klub.temporayStorageServer.app.service.listener;

import com.klub.temporayStorageServer.app.configs.ftp.CustomFtpClient;
import com.klub.temporayStorageServer.app.service.api.CentralLoggerServerApi;
import com.klub.temporayStorageServer.app.service.api.dto.CentralServerLogMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationStartFtpListener implements ApplicationListener<ApplicationStartedEvent> {

    private final CustomFtpClient ftpClient;
    private final CentralLoggerServerApi centralLoggerServerApi;

    @Autowired
    public ApplicationStartFtpListener(CustomFtpClient ftpClient, CentralLoggerServerApi centralLoggerServerApi) {
        this.ftpClient = ftpClient;
        this.centralLoggerServerApi = centralLoggerServerApi;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            ftpClient.open();
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Application started").build()
                    .addData("ftp", "started"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
