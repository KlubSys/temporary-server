package com.klub.temporayStorageServer.app.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klub.temporayStorageServer.app.exception.ErrorOccurredException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobSchedulerApi {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Value("${job.scheduler.server}")
    private String jobSchedulerServer;

    private final ObjectMapper defaultMapper;

    final OkHttpClient client = new OkHttpClient();

    @Autowired
    public JobSchedulerApi(ObjectMapper defaultMapper) {
        this.defaultMapper = defaultMapper;
    }

    public Object submitJob(Object message) throws Exception {
        String ctn = defaultMapper.writeValueAsString(message);
        RequestBody body = RequestBody.create(ctn, JSON);
        System.out.println("Job Api Request body " + ctn);

        Request request = new Request.Builder()
                .url(jobSchedulerServer + "/api/jobs")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new ErrorOccurredException("Request failed with " + response.code());
            if (response.body() == null) throw new ErrorOccurredException("Submit Job response body ius null");

            String bdy =  response.body().string();
            System.out.println("Body Job request api " + bdy + response.body().contentLength());
            Object content = defaultMapper.readValue(
                    bdy, HashMap.class);
            //System.out.println("Submitted job id " + content.getJobId());

            return content;
        } catch (IOException e) {
            System.err.println("Calling API " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean updateJob(String jobId, Map<String, Object> data) throws Exception {
        String ctn = defaultMapper.writeValueAsString(data);
        RequestBody body = RequestBody.create(ctn, JSON);
        System.out.println("Job Api Update Request body " + ctn);

        Request request = new Request.Builder()
                .url(jobSchedulerServer + "/api/jobs/" + jobId)
                .patch(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new ErrorOccurredException("Request failed with " + response.code());
            if (response.body() == null) throw new ErrorOccurredException("Submit Job response body ius null");

            return true;
        } catch (IOException e) {
            System.err.println("Calling API " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
