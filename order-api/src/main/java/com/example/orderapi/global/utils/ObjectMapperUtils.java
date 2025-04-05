package com.example.orderapi.global.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperUtils {

    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public static String createObjectToJson(Object obj) {
        try {
            return objectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 변경 오류");
        }

        return "";
    }
}
