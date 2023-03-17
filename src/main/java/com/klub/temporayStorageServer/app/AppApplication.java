package com.klub.temporayStorageServer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(AppApplication.class, args);
	}

}
