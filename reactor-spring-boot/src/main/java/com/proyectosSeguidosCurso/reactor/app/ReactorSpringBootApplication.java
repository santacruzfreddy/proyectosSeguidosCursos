package com.proyectosSeguidosCurso.reactor.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactorSpringBootApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ReactorSpringBootApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<String> nombres= Flux.just("freddy","rocio","Gaby")
				.doOnNext(elemento-> System.out.println(elemento));

//		nombres= Flux.just("freddy","rocio","Gaby")
//				.doOnNext(System.out::println);

		nombres.subscribe();
	}
}
