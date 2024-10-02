package mx.ipn.cerradurasweb.controlador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "mx.ipn.cerradurasweb")
public class CerraduraswebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CerraduraswebApplication.class, args);
    }
}