package com.klub.temporayStorageServer.app.service.listener;

import com.klub.temporayStorageServer.app.configs.ftp.CustomFtpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationStopFtpListener implements ApplicationListener<ContextClosedEvent> {

    private final CustomFtpClient ftpClient;

    @Autowired
    public ApplicationStopFtpListener(CustomFtpClient ftpClient) {
        this.ftpClient = ftpClient;
    }


    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            ftpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
