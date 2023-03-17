package com.klub.temporayStorageServer.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klub.temporayStorageServer.app.helper.JobExecutionStatusEnum;
import com.klub.temporayStorageServer.app.helper.JobStatusEnum;
import com.klub.temporayStorageServer.app.model.FileDecompositionJobPayload;
import com.klub.temporayStorageServer.app.model.FileMetadataUpdatePayload;
import com.klub.temporayStorageServer.app.service.api.JobSchedulerApi;
import com.klub.temporayStorageServer.app.service.api.storeClient.StoreApi;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.blockGroup.SaveBlockGroupResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.datablock.SaveDataBlockResponse;
import com.klub.temporayStorageServer.app.service.api.storeClient.dto.response.store.GetRandomOnlineStoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
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
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaListeners(ObjectMapper defaultMapper, StoreApi storeApi, JobSchedulerApi jobSchedulerApi, KafkaTemplate<String, String> kafkaTemplate) {
        this.defaultMapper = defaultMapper;
        this.storeApi = storeApi;
        this.jobSchedulerApi = jobSchedulerApi;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "file_decomposition", groupId = "groupId")
    void listener(String data) {
        //This must contain the file name
        //And some file sensitive informations
        System.out.println("Received Data " + data);
        try {
            FileDecompositionJobPayload payload = defaultMapper.readValue(
                    data, FileDecompositionJobPayload.class);
            //Claim the job
            Map<String, Object> jobUpdateData = new HashMap<>();
            //TODO Master action
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.CLAIMED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);



            File file = new File(
                    new File("D:\\FtpServer\\klub\\temp_storage"),
                    payload.getFilename());
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

            //Worket processing the job
            jobUpdateData = new HashMap<>();
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.PROCESSING);
            jobUpdateData.put("job_status", JobStatusEnum.STARTED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            //Read byte for a given data block
            //Create a block group ref the databloc
            //Replicate the dataBlock
            //Find a free store
            //Upload the databloc

            byte[] container = new byte[100];
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

                SaveBlockGroupResponse blocGroupRes = storeApi
                        .createBlockGroup(null, previousBlocGroupRef, encoded, encoded.length());
                System.out.println("Block Group created " + blocGroupRes.getData().getId());
                previousBlocGroupRef = blocGroupRes.getData().getIdentifier();

                //TODO for the length use a variable
                GetRandomOnlineStoreResponse storeRes = storeApi
                        .getRandomOnlineStore(encoded.length(),
                                blocGroupRes.getData().getIdentifier()
                        );
                if (storeRes.getStoreRef() == null || storeRes.getStoreRef().length() == 0){
                    throw new RuntimeException("No Store found");
                }
                SaveDataBlockResponse blocDataRes = storeApi.createDataBloc(
                        encoded, encoded.length(), storeRes.getStoreRef(),
                        blocGroupRes.getData().getIdentifier()
                );

                if (!hasProcessedFirstBlock) {
                    //TODO message kafka on file channel tp update the file data
                    //TODO use constants for topic name
                    final FileMetadataUpdatePayload p =  new FileMetadataUpdatePayload();
                    p.setFileId(payload.getFileId());
                    p.getData().put("first_blockGroup_id", blocGroupRes.getData().getIdentifier());

                    kafkaTemplate.send("file_metadata", defaultMapper.writeValueAsString(p))
                            .addCallback(result -> {
                                System.out.println("File Update Sent");
                            }, ex -> {
                                System.err.println("Error odccured");
                                ex.printStackTrace();
                            });
                    hasProcessedFirstBlock = true;
                }

                blocCount += 1;
            }
            System.out.println();
            System.out.println(new String(container));

            //TODO Update job as completed
            jobUpdateData = new HashMap<>();
            jobUpdateData.put("execution_status", JobExecutionStatusEnum.SUCCESS);
            jobUpdateData.put("job_status", JobStatusEnum.COMPLETED);
            jobSchedulerApi.updateJob(payload.getJobId(), jobUpdateData);

            //Update file to have the count data
            final FileMetadataUpdatePayload p = new FileMetadataUpdatePayload();
            p.setFileId(payload.getFileId());
            p.getData().put("data_bloc_count", blocCount);

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
