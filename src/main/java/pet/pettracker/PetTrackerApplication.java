package pet.pettracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PetTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetTrackerApplication.class, args);
	}

}
