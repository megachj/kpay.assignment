package megachj.kpay.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"megachj.kpay.assignment.*"})
public class ApplicationServer {

    /**
     * VM Arguments
     * -Dspring.profiles.active=local
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationServer.class, args);
    }
}
