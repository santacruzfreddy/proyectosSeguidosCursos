package com.webfluxcurso.springbootwebflux.Controller;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import com.webfluxcurso.springbootwebflux.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;

    @GetMapping({"/listar", "/"})
    public String listar(Model model) {
        Flux<Product> products = productService.findAllToUpperCaseName();        
        products.subscribe(product -> {log.info(product.getName());});
        model.addAttribute("products", products);
        model.addAttribute("title","List of the products");
        return "listar";
    }

    @GetMapping("/listar-dataDriver")
    public String listarDataDriver(Model model) {
        Flux<Product> products = productService.findAllToUpperCaseName().delayElements(Duration.ofSeconds(1));
        products.subscribe(product -> {log.info(product.getName());});
        model.addAttribute("products",new ReactiveDataDriverContextVariable(products,2) );
        model.addAttribute("title","List of the products");
        return "listar";
    }

    @GetMapping("/listar-full")
    public String listarFull(Model model) {
        Flux<Product> products = productService.findAllToUpperCaseNameWithRepeat();

        products.subscribe(product -> {log.info(product.getName());});

        model.addAttribute("products", products);
        model.addAttribute("title","List of the products");

        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {
        Flux<Product> products = productService.findAllToUpperCaseNameWithRepeat();
        products.subscribe(product -> {log.info(product.getName());});

        model.addAttribute("products", products);
        model.addAttribute("title","List of the products");

        return "listar-chunked";
    }


}
