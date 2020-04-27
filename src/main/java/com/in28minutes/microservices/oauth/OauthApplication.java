package com.in28minutes.microservices.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;


@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@EnableAuthorizationServer
public class OauthApplication implements CommandLineRunner {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;


	public static void main(String[] args) {
		SpringApplication.run(OauthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//usuario_agu
		String token_agu =bCryptPasswordEncoder.encode("12345");

		//usuario antonio

		String token_antonio=bCryptPasswordEncoder.encode("67890");

		System.out.println(token_agu);
		System.out.println(token_antonio);

	}
}
