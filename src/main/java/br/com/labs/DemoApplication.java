package br.com.labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        log.info("===========================================");
        log.info("   Music API started successfully!");
        log.info("   Swagger UI: http://localhost:8080/swagger-ui.html");
        log.info("   API Docs:   http://localhost:8080/api-docs");
        log.info("===========================================");
    }
}
