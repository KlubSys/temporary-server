package com.klub.temporayStorageServer.app.configs.ftp;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomFtpClient {

    @Value("${ftp.port}")
    private Integer ftpPort;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    private FTPClient ftp;

    public void open() throws IOException {
        if (ftp != null) return;

        ftp = new FTPClient();


        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(ftpHost, ftpPort);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(username, password);
    }

    public void close() throws IOException {
        ftp.disconnect();
    }

    private void check() throws IOException {
        open();
        if (!ftp.isConnected() || !ftp.isAvailable()) {
            //open();
        }
    }

    public FTPClient getInstance() throws IOException {
        check();
        return ftp;
    }
}
