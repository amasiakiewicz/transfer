package com.casinoroyale.transfer;

import java.time.ZoneOffset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransferApplication {

    public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;

    public static void main(String[] args) {
		SpringApplication.run(TransferApplication.class, args);
	}
	
}
