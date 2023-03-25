package com.klub.temporayStorageServer.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klub.temporayStorageServer.app.configs.ftp.CustomFtpClient;
import com.klub.temporayStorageServer.app.helper.JobExecutionStatusEnum;
import com.klub.temporayStorageServer.app.helper.JobStatusEnum;
import com.klub.temporayStorageServer.app.model.FileDecompositionJobPayload;
import com.klub.temporayStorageServer.app.model.FileMetadataUpdatePayload;
import com.klub.temporayStorageServer.app.service.api.CentralLoggerServerApi;
import com.klub.temporayStorageServer.app.service.api.JobSchedulerApi;
import com.klub.temporayStorageServer.app.service.api.dto.CentralServerLogMessage;
import com.klub.temporayStorageServer.app.service.api.storeClient.StoreApi;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup.SaveBlockGroupResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock.SaveDataBlockResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.store.GetRandomOnlineStoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaListeners {

    private final ObjectMapper defaultMapper;
    private final StoreApi storeApi;
    private final JobSchedulerApi jobSchedulerApi;
    private final CustomFtpClient ftpClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CentralLoggerServerApi centralLoggerServerApi;

    @Value("${batch}")
    private Integer batch;

    @Autowired
    public KafkaListeners(ObjectMapper defaultMapper, StoreApi storeApi, JobSchedulerApi jobSchedulerApi, CustomFtpClient ftpClient, KafkaTemplate<String, String> kafkaTemplate, CentralLoggerServerApi centralLoggerServerApi) {
        this.defaultMapper = defaultMapper;
        this.storeApi = storeApi;
        this.jobSchedulerApi = jobSchedulerApi;
        this.ftpClient = ftpClient;
        this.kafkaTemplate = kafkaTemplate;
        this.centralLoggerServerApi = centralLoggerServerApi;
    }

    @KafkaListener(topics = "file_decomposition", groupId = "groupId")
    void listener(String data) {
        //This must contain the file name
        //And some file sensitive informations
        System.out.println("Received Data " + data);
        try {
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Got action from queue [data]" + data).build());

            FileDecompositionJobPayload payload = defaultMapper.readValue(
                    data, FileDecompositionJobPayload.class);
            //Claim the job
            Map<String, Object> jobUpdateData = new HashMap<>();
            //TODO Master action
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.CLAIMED);
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update job execution status " + payload.getJobId()).build());
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update Job execution Status [OK]").build());


            FileOutputStream out = new FileOutputStream(payload.getFilename());
            ftpClient.getInstance().retrieveFile(
                    String.format("/klub/temp_storage/%s", payload.getFilename()), out);

            /*File file = new File(
                    new File("D:\\FtpServer\\klub\\temp_storage"),
                    payload.getFilename());*/
            File file = new File(payload.getFilename());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("File loaded" + data).build());

            //Worket processing the job
            jobUpdateData = new HashMap<>();
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update job status " + payload.getJobId()).build());
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.PROCESSING);
            jobUpdateData.put("job_status", JobStatusEnum.STARTED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update job execution status updated " + payload.getJobId()).build());

            //Read byte for a given data block
            //Create a block group ref the databloc
            //Replicate the dataBlock
            //Find a free store
            //Upload the databloc

            byte[] container = new byte[batch];
            boolean hasProcessedFirstBlock = false;
            String previousBlocGroupRef = null;

            //randomAccessFile.seek(1);
            int i = 0;
            int blocCount = 0;
            while ((i = randomAccessFile.read(container)) != -1) {
                byte[] rd = new byte[i];
                System.arraycopy(container, 0, rd, 0, i);

                String encoded = Base64.getEncoder().encodeToString(rd);
                System.out.println(i);
                System.out.println(encoded);

                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text(String.format("Data bloc red batch %d | %d bytes enc %s", batch, rd.length, encoded)).build());

                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Creating block group").build());
                SaveBlockGroupResponse blocGroupRes = storeApi
                        .createBlockGroup(null, previousBlocGroupRef, encoded, encoded.length());
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Bloc group group created" + data).build());
                System.out.println("Block Group created " + blocGroupRes.getData().getId());
                previousBlocGroupRef = blocGroupRes.getData().getIdentifier();

                //TODO for the length use a variable
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Requested a free online store").build());
                GetRandomOnlineStoreResponse storeRes = storeApi
                        .getRandomOnlineStore(encoded.length(),
                                blocGroupRes.getData().getIdentifier()
                        );
                if (storeRes.getStoreRef() == null || storeRes.getStoreRef().length() == 0) {
                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                            .text("Find free online store failed").build());
                    throw new RuntimeException("No Store found");
                }
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Free online store found [OK]" + data).build());

                //Data bloc
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Creating data block").build());

                SaveDataBlockResponse blocDataRes = storeApi.createDataBloc(
                        encoded, encoded.length(), storeRes.getStoreRef(),
                        blocGroupRes.getData().getIdentifier()
                );
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("Got action from queue [data]" + data).build());

                if (!hasProcessedFirstBlock) {
                    //TODO message kafka on file channel tp update the file data
                    //TODO use constants for topic name
                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                            .text("Processing first block group").build());

                    final FileMetadataUpdatePayload p = new FileMetadataUpdatePayload();
                    p.setFileId(payload.getFileId());
                    p.getData().put("first_blockGroup_id", blocGroupRes.getData().getIdentifier());

                    kafkaTemplate.send("file_metadata", defaultMapper.writeValueAsString(p))
                            .addCallback(result -> {
                                try {
                                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                            .text("File Update action pushed to queue").build());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("File Update Sent");
                            }, ex -> {
                                System.err.println("Error occured");
                                try {
                                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                            .text("File Update to queue failed").build());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                ex.printStackTrace();
                            });
                    hasProcessedFirstBlock = true;
                }

                blocCount += 1;
            }
            System.out.println();
            System.out.println("Bloc Count " + blocCount);
            System.out.println(new String(container));

            randomAccessFile.close();
            file.delete();

            //TODO Update job as completed
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update job status " + payload.getJobId()).build());


            jobUpdateData = new HashMap<>();
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.SUCCESS);
            jobUpdateData.put("job_status", JobStatusEnum.COMPLETED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update job execution status updated  to success and completed" + payload.getJobId()).build());

            //Update file to have the count data
            final FileMetadataUpdatePayload p = new FileMetadataUpdatePayload();
            p.setFileId(payload.getFileId());
            p.getData().put("data_bloc_count", blocCount);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Updating file with bloc count " + blocCount).build());
            kafkaTemplate.send("file_metadata", defaultMapper.writeValueAsString(p))
                    .addCallback(result -> {
                        System.out.println("File Update Sent For bloc Count");
                    }, ex -> {
                        System.err.println("Error odccured");
                        ex.printStackTrace();
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
