package com.croppredict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.croppredict")
public class CropPriceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CropPriceApplication.class, args);
    }
}