package com.klub.temporayStorageServer.app.service.listener;

import com.klub.temporayStorageServer.app.configs.ftp.CustomFtpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApplicationStartFtpListener implements ApplicationListener<ApplicationStartedEvent> {

    private final CustomFtpClient ftpClient;

    @Autowired
    public ApplicationStartFtpListener(CustomFtpClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            ftpClient.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
