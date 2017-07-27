package com.firstTaste.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 第一个版本数据层模型和业务模型就混用哈
 * 
 * @author yinwenjie
 * @version 1.0.0 snapshot
 */
@SpringBootApplication
public class ServerStarter extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(ServerStarter.class, args);
	}
}
