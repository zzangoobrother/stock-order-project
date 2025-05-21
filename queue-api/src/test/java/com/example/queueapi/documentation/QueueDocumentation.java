package com.example.queueapi.documentation;

import com.example.queueapi.application.service.QueueService;
import com.example.queueapi.application.service.dto.QueueServiceDto;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class QueueDocumentation extends Documentation {

    @MockitoBean
    private QueueService queueService;

    @Test
    void registerUser() {
        QueueServiceDto dto = new QueueServiceDto("order", 1L);
        when(queueService.registerUser(anyString(), anyLong())).thenReturn(dto);

        RestAssured
                .given(spec).log().all()
                .filter(document("queue/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("queue").description("대기열 이름"),
                                parameterWithName("userId").description("회원 id")
                        )))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("queue", "order")
                .queryParam("userId", 1L)
                .when().get("/api/v1/queue")
                .then().log().all().extract();
    }

    @Test
    void getBy() {
        when(queueService.getRankUser(anyString(), anyString())).thenReturn(1L);

        RestAssured
                .given(spec).log().all()
                .filter(document("queue/rank",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("queue").description("대기열 이름")
                        )))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie("queue-wait-order-token", "asdf")
                .queryParam("queue", "order")
                .when().get("/api/v1/queue/rank")
                .then().log().all().extract();
    }

    @Test
    void isAllowedUser() {
        when(queueService.isAllowedUser(anyString(), anyString())).thenReturn(true);

        RestAssured
                .given(spec).log().all()
                .filter(document("queue/allowed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("queue").description("대기열 이름")
                        )))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie("queue-wait-order-token", "asdf")
                .queryParam("queue", "order")
                .when().get("/api/v1/queue/allowed")
                .then().log().all().extract();
    }
}
