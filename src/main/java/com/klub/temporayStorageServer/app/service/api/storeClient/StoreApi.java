package com.klub.temporayStorageServer.app.service.api.storeClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klub.temporayStorageServer.app.exception.ErrorOccurredException;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.blockGroup.SaveBlockGroupRequest;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.datablock.SaveDataBlockRequest;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.request.store.GetRandomOnlineStoreRequest;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup.SaveBlockGroupResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock.SaveDataBlockResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.store.GetRandomOnlineStoreResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.routes.BlockGroupRoutes;
import com.klub.temporayStorageServer.app.service.api.storeClient.routes.DataBlockRoutes;
import com.klub.temporayStorageServer.app.service.api.storeClient.routes.StoreRoutes;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class StoreApi {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Value("${store.api.server}")
    private String storeApiServer;

    private final ObjectMapper defaultMapper;

    final OkHttpClient client = new OkHttpClient();

    @Autowired
    public StoreApi(ObjectMapper defaultMapper) {
        this.defaultMapper = defaultMapper;
    }

    /**
     *
     * @param next
     * @param previous
     * @param data to be stored
     * @param dataSize the size in bytes
     * @return
     * @throws JsonProcessingException
     */
    public SaveBlockGroupResponse createBlockGroup(String next, String previous,
                                                   String data, int dataSize) throws JsonProcessingException {
        SaveBlockGroupRequest req = new SaveBlockGroupRequest();
        req.setData(data);
        req.setDataSize(dataSize);
        req.setNext(next);
        req.setPrevious(previous);

        String ctn = defaultMapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(ctn, JSON);
        System.out.println("Request body " + ctn);

        Request request = new Request.Builder()
                .url(storeApiServer + BlockGroupRoutes.getCreateUrl())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new ErrorOccurredException("Request failed with " + response.code());
            if (response.body() == null) throw new ErrorOccurredException("response body ius null");

            String bdy = response.body().string();
            System.out.println("Body Job request api " + bdy + response.body().contentLength());
            SaveBlockGroupResponse content = defaultMapper.readValue(
                    bdy, SaveBlockGroupResponse.class);
            //System.out.println("Submitted job id " + content.getJobId());

            return content;
        } catch (IOException | ErrorOccurredException e) {
            System.err.println("Calling API " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param dataSize
     * @param blocGroupRef
     * @return
     */
    public GetRandomOnlineStoreResponse getRandomOnlineStore(int dataSize, String blocGroupRef)
            throws JsonProcessingException {
        GetRandomOnlineStoreRequest req = new GetRandomOnlineStoreRequest();
        req.setDataSize(dataSize);
        req.setBlockGroupRef(blocGroupRef);

        String ctn = defaultMapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(ctn, JSON);
        System.out.println("Request body " + ctn);

        Request request = new Request.Builder()
                .url(storeApiServer + StoreRoutes.getGetRandomOnlineUrl(dataSize, blocGroupRef))
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new ErrorOccurredException("Request failed with " + response.code());
            if (response.body() == null) throw new ErrorOccurredException("response body ius null");

            String bdy = response.body().string();
            System.out.println("Body Job request api " + bdy + response.body().contentLength());
            GetRandomOnlineStoreResponse content = defaultMapper.readValue(
                    bdy, GetRandomOnlineStoreResponse.class);
            return content;
        } catch (IOException | ErrorOccurredException e) {
            System.err.println("Calling API " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * @param data
     * @param dataSize
     * @param storeRef
     * @param blocGroupRef
     * @throws JsonProcessingException
     */
    public SaveDataBlockResponse createDataBloc(String data, int dataSize, String storeRef, String blocGroupRef) throws JsonProcessingException {
        SaveDataBlockRequest req = new SaveDataBlockRequest();
        req.setData(data);
        req.setStore(storeRef);
        req.setBlockGroup(blocGroupRef);
        req.setSize(dataSize);

        String ctn = defaultMapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(ctn, JSON);
        System.out.println("Request body " + ctn);

        Request request = new Request.Builder()
                .url(storeApiServer + DataBlockRoutes.getCreateUrl())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new ErrorOccurredException("Request failed with " + response.code());
            if (response.body() == null) throw new ErrorOccurredException("response body ius null");

            String bdy = response.body().string();
            System.out.println("Body Job request api " + bdy + response.body().contentLength());
            SaveDataBlockResponse content = defaultMapper.readValue(
                    bdy, SaveDataBlockResponse.class);
            return content;
        } catch (IOException | ErrorOccurredException e) {
            System.err.println("Calling API " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
