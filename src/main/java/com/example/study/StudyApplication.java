package com.example.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

@EnableRetry
@EnableAspectJAutoProxy
@SpringBootApplication
public class StudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

	@Bean
	RestClientCustomizer logbookCustomizer(
			LogbookClientHttpRequestInterceptor interceptor) {
		return restClient -> restClient.requestInterceptor(interceptor);
	}

}
