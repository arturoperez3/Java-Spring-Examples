package org.magnum.dataup;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

// Auto-wire our application.
@EnableAutoConfiguration
// look for controllers, etc. starting in the current package.
@ComponentScan
//This annotation tells Spring that this class contains configuration information for the application.
@Configuration
public class Application {

	private static final int MAX_REQUEST_SIZE_IN_MB = 150;

	// The entry point to the application.
	public static void main(String[] args) {
		// This call tells spring to launch the application and
		// use the configuration specified in LocalApplication to
		// configure the application's components.
		SpringApplication.run(Application.class, args);
	}

	// This configuration element adds the ability to accept multipart
	// requests to the web container.
	@Bean
    public MultipartConfigElement multipartConfigElement() {
		// Setup the application container to be accept multipart requests
		final MultipartConfigFactory factory = new MultipartConfigFactory();

		// Ensure max request sizes so application doesn't get maliciously attacked
		factory.setMaxFileSize(DataSize.ofMegabytes(MAX_REQUEST_SIZE_IN_MB));
		factory.setMaxRequestSize(DataSize.ofMegabytes(MAX_REQUEST_SIZE_IN_MB));
		return factory.createMultipartConfig();
	}
}
