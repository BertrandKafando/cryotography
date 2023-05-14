package es.kf.signapp;

import es.kf.signapp.configuration.RsakeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(RsakeysConfig.class)
public class SignAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignAppApplication.class, args);
	}

}
