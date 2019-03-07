package com.user.application.config;



import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.user.application.service" })
public class ServiceConfig {

}
