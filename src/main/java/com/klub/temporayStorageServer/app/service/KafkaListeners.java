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
                    .text("Got action from queue").build());

            FileDecompositionJobPayload payload = defaultMapper.readValue(
                    data, FileDecompositionJobPayload.class);
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Load payload").build()
                    .addData("data", payload));


            //Claim the job
            Map<String, Object> jobUpdateData = new HashMap<>();
            //TODO Master action
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.CLAIMED);
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("[Job claimed] Update job execution status " + payload.getJobId()).build()
                    .addData("job_id", payload.getJobId())
                    .addData("new_execution_status", JobExecutionStatusEnum.CLAIMED));
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Update Job execution Status [OK]").build()
                    .addData("data", jobUpdateData));


            FileOutputStream out = new FileOutputStream(payload.getFilename());
            ftpClient.getInstance().retrieveFile(
                    String.format("/klub/temp_storage/%s", payload.getFilename()), out);

            /*File file = new File(
                    new File("D:\\FtpServer\\klub\\temp_storage"),
                    payload.getFilename());*/
            File file = new File(payload.getFilename());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("Temporarily File loaded").build()
                    .addData("file", payload.getFilename())
                    .addData("job", payload));

            //Worket processing the job
            jobUpdateData = new HashMap<>();
            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("[Job Started | Processing] Updating job " + payload.getJobId()).build());
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.PROCESSING);
            jobUpdateData.put("job_status", JobStatusEnum.STARTED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("[Job Started | Processing] Updating job " + payload.getJobId()).build()
                    .addData("job", payload.getJobId())
                    .addData("data", jobUpdateData));

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
                        .text("Data bloc red " + (blocCount + 1)).build()
                        .addData("batch", batch)
                        .addData("N°", (blocCount + 1))
                        .addData("bytes_red", rd.length)
                        .addData("encoded_bloc", encoded));

                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Bloc Group] Creating").build());
                SaveBlockGroupResponse blocGroupRes = storeApi
                        .createBlockGroup(null, previousBlocGroupRef, encoded, encoded.length());
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Bloc group] created").build()
                        .addData("batch", batch)
                        .addData("bloc_group", blocGroupRes.getData())
                        .addData("N°", (blocCount + 1))
                        .addData("bytes_red", rd.length)
                        .addData("encoded_bloc", encoded));

                System.out.println("Block Group created " + blocGroupRes.getData().getId());
                previousBlocGroupRef = blocGroupRes.getData().getIdentifier();

                //TODO for the length use a variable
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Store] Requested a free online store").build()
                        .addData("bloc_group", blocGroupRes.getData().getIdentifier())
                        .addData("data_size_", encoded.length() + " bytes"));
                GetRandomOnlineStoreResponse storeRes = storeApi
                        .getRandomOnlineStore(encoded.length(),
                                blocGroupRes.getData().getIdentifier()
                        );
                if (storeRes.getStoreRef() == null || storeRes.getStoreRef().length() == 0) {

                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                            .text("[Store] Find free online store failed").build()
                            .addData("bloc_group", blocGroupRes.getData().getIdentifier())
                            .addData("", encoded.length() + " bytes"));

                    throw new RuntimeException("No Store found");
                }

                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Store] Free online store found [OK]").build()
                        .addData("store", storeRes.getStoreRef())
                        .addData("bloc_group", blocGroupRes.getData().getIdentifier())
                        .addData("data_size", encoded.length() + " bytes"));

                //Data bloc
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Data bloc] Creating").build()
                        .addData("store", storeRes.getStoreRef())
                        .addData("bloc_group", blocGroupRes.getData().getIdentifier())
                        .addData("data_size", encoded.length() + " bytes"));

                SaveDataBlockResponse blocDataRes = storeApi.createDataBloc(
                        encoded, encoded.length(), storeRes.getStoreRef(),
                        blocGroupRes.getData().getIdentifier()
                );
                centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                        .text("[Data Bloc] Created N° " + (blocCount + 1)).build()
                        .addData("data", blocDataRes.getData()));

                if (!hasProcessedFirstBlock) {
                    //TODO message kafka on file channel tp update the file data
                    //TODO use constants for topic name
                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                            .text("[File and first bloc] Processing first block group").build());

                    final FileMetadataUpdatePayload p = new FileMetadataUpdatePayload();
                    p.setFileId(payload.getFileId());
                    p.getData().put("first_blockGroup_id", blocGroupRes.getData().getIdentifier());

                    kafkaTemplate.send("file_metadata", defaultMapper.writeValueAsString(p))
                            .addCallback(result -> {
                                try {
                                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                            .text("[File and first bloc] File Update action pushed to queue").build()
                                            .addData("data", p));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("File Update Sent");
                            }, ex -> {
                                System.err.println("Error occurred");
                                try {
                                    centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                            .text("[File and first bloc] File Update to queue failed").build()
                                            .addData("data", p));
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
                    .text("[Job Completed | Success] Update job " + payload.getJobId()).build());


            jobUpdateData = new HashMap<>();
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.SUCCESS);
            jobUpdateData.put("job_status", JobStatusEnum.COMPLETED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("[Job Completed | Success] Update job " + payload.getJobId()).build()
                    .addData("data", jobUpdateData));

            //Update file to have the count data
            final FileMetadataUpdatePayload p = new FileMetadataUpdatePayload();
            p.setFileId(payload.getFileId());
            p.getData().put("data_bloc_count", blocCount);

            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                    .text("[File Update] Updating file with bloc count " + blocCount).build()
                    .addData("bloc_count", blocCount)
                    .addData("file", payload.getFileId()));
            kafkaTemplate.send("file_metadata", defaultMapper.writeValueAsString(p))
                    .addCallback(result -> {
                        System.out.println("File Update Sent For bloc Count");
                        try {
                            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                    .text("[File Update] Update file with bloc count pushed to queue ").build()
                                    .addData("data", p));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, ex -> {
                        System.err.println("Error occurred");
                        try {
                            centralLoggerServerApi.dispatchLog(CentralServerLogMessage.builder()
                                    .text("[File Update] Updating file with bloc count Failed").build());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        ex.printStackTrace();
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
