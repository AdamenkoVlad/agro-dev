package com.abi.agro_back;

import com.abi.agro_back.service.DelBucket;
import com.abi.agro_back.service.Parser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgroBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgroBackApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner(Parser parser) {
//		return args -> {
//			parser.jsonToHashMap();
//			parser.start();
//		};
//	}
//	@Bean
//	CommandLineRunner runner(DelBucket delBucket) {
//		return args -> {
//			delBucket.delBuck();
//		};
//	}
}
