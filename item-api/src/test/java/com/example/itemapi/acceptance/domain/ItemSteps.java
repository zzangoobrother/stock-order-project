package com.example.itemapi.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import com.example.itemapi.interfaces.presentation.request.ItemRequest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class ItemSteps extends AcceptanceTestSteps {

    public static ExtractableResponse<Response> 제품_등록_요청(ItemRequest.AddItem request) {
        Map<String, String> params = new HashMap<>();
        params.put("name", request.name());
        params.put("price", request.price().intValue() + "");
        params.put("stock", request.stock() + "");

        return given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/items")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 재고_차감_요청(Long itemId, int decreaseCount) {
        Map<String, String> params = new HashMap<>();
        params.put("decreaseCount", decreaseCount + "");

        return given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/items/" + itemId + "/decrease-stock")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 제품_단건_조회_요청(Long itemId) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/items/" + itemId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 제품_단건_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }
}
