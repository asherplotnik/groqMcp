package com.mcp.groq.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
@Slf4j
public class JsonUtils {

    public static <T> T readJsonFile(String filePath, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.readValue(new File(filePath), clazz);
    }

    public static void printJson(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (obj instanceof String strObj) {
            try {
                JsonNode tree = mapper.readTree(strObj);
                String pretty = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(tree);
                log.info(pretty);
            } catch (JsonProcessingException e) {
                log.info(strObj);
            }
            return;
        }
        log.info(mapper.writeValueAsString(obj));
    }
}