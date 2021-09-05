package com.ericlam.qqbot.valbot.service;

import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@EnableAsync
@Service
public class ValDataService {

    private final Logger logger;
    private final ObjectMapper mapper;

    @Resource(name = "data")
    private File folder;

    public ValDataService(Logger logger, ObjectMapper mapper) {
        this.logger = logger;
        this.mapper = mapper;
    }


    private ValBotData data;
    private File dataFile;

    @PostConstruct
    public void onCreate() throws IOException {
        dataFile = new File(folder, "valData.json");
        if (!dataFile.exists()) {
            mapper.writeValue(dataFile, new ValBotData());
            logger.info("valData.json has been created.");
        }
        this.data = mapper.readValue(dataFile, ValBotData.class);
    }


    public ValBotData getData() {
        return Optional.ofNullable(data).orElseThrow(IllegalArgumentException::new);
    }

    public void printData(){
        logger.info("正在打印 ValData 的内容");
        logger.info(getData().toString());
    }

    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            try {
                mapper.writeValue(dataFile, data);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }


    @Async
    @Scheduled(fixedRate = 300000)
    public void scheduleSave(){
        logger.info("Saving ValBot.json....");
        try {
            mapper.writeValue(dataFile, data);
            logger.info("save completed.");
        } catch (IOException e) {
            logger.warn("Error while saving ValData.json ", e);
        }
    }

    @Async
    @PreDestroy
    public void onDestroy() {
        logger.info("Saving ValBot.json....");
        try {
            mapper.writeValue(dataFile, data);
            logger.info("save completed.");
        } catch (IOException e) {
            logger.warn("Error while saving ValData.json ", e);
        }
    }

}
