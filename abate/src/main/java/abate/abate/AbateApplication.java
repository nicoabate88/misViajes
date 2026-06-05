package abate.abate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbateApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbateApplication.class, args);
	}

}
