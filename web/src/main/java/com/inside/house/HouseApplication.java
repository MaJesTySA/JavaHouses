package com.inside.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HouseApplication {
    private int test;
    public static void main(String[] args) {
        SpringApplication.run(HouseApplication.class, args);
    }

}
