package com.example.itemapi.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class AcceptanceTestSteps {

	static RequestSpecification given() {
		return RestAssured
			.given().log().all();
	}
}
