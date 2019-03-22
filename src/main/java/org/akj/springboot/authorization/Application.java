package org.akj.springboot.authorization;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;

/**
 * Springboot application - Entry point
 * 
 * @author Jamie Zhang
 */

@SpringBootApplication
public class Application implements ServletContextInitializer{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.getSessionCookieConfig().setName("client-session");
	}
	
	

}
