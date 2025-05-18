package pl.harpi.tutorials.bitemporal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.ZoneId;
import java.util.TimeZone;

@EnableJpaRepositories
@SpringBootApplication
class App {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Warsaw")));
        SpringApplication.run(App.class, args);
    }
}