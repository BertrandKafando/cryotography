package com.example.auditjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AuditJpaApplication {

        public static void main(String[] args) {
            SpringApplication.run(AuditJpaApplication.class, args);
        }
}
