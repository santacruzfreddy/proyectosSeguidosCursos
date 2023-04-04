package com.webfluxcurso.springbootwebflux;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import com.webfluxcurso.springbootwebflux.models.repository.ProductRepository;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    
    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }
    
    

    @Override
    public void run(String... args) throws Exception {
        reactiveMongoTemplate.dropCollection("products").subscribe();
        
        Flux.just(new Product("Zandalia talonera", 19.0),
                new Product("Gusano", 19.0),
                new Product("nike", 19.0),
                new Product("Zapatilla de banio hombre", 19.0),
                new Product("Limpio", 19.0),
                new Product("Salvavidas", 19.0))
                .flatMap(product -> {
                    product.setCreateAt(new Date());
                    return productRepository.save(product);})
                .subscribe(
                        product -> log.info(product.getId())
                );
    }
}
