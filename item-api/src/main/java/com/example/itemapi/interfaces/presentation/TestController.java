package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.global.config.properties.TestProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class TestController {

    private final TestProperties testProperties;

    @GetMapping("/test")
    public void test() {
        log.info(testProperties.getTest());
    }
}
