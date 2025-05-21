package com.example.orderapi.documentation;

import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.interfaces.presentation.feign.QueueClient;
import com.example.orderapi.interfaces.presentation.request.OrderRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class OrderDocumentation extends Documentation {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private QueueClient queueClient;

    @Test
    void createOrder() {
        OrderRequest request = new OrderRequest(1L, 10);

        when(queueClient.getBy(anyString())).thenReturn(true);

        RestAssured
                .given(spec).log().all()
                .filter(document("order/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("itemId").description("제품 id"),
                                fieldWithPath("quantity").description("수량")
                        )))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/v1/orders")
                .then().log().all().extract();
    }
}
