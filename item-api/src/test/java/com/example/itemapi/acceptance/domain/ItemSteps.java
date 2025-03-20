package com.example.itemapi.acceptance.domain;

import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class ItemSteps {

    public static ExtractableResponse<Response> 제품_등록_요청(ItemRequest.AddItem request) {
        Map<String, String> params = new HashMap<>();
        params.put("name", request.name());
        params.put("price", request.price().intValue() + "");
        params.put("stock", request.stock() + "");

        return RestAssured.given().log().all()
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

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/v1/items/" + itemId + "/decrease-stock")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 제품_단건_조회_요청(Long itemId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/items/" + itemId)
                .then().log().all()
                .extract();
    }
}
