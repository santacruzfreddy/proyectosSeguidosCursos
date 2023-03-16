package com.proyectosSeguidosCurso.reactor.app;

import com.proyectosSeguidosCurso.reactor.app.models.Comentarios;
import com.proyectosSeguidosCurso.reactor.app.models.Usuario;
import com.proyectosSeguidosCurso.reactor.app.models.UsuarioComentarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class ReactorSpringBootApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReactorSpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReactorSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ejemploUsuarioComentariosFlatMap();
    }

    public Usuario crearUsuario() {
        return new Usuario("Freddy", "Santacruz");
    }

    public Comentarios crearComentarios() {
        Comentarios comentarios = new Comentarios();
        comentarios.addComentario("hola freddy");
        comentarios.addComentario("fui al shoping");
        comentarios.addComentario("compre un yogourt");
        return comentarios;
    }


    public void ejemploUsuarioComentariosFlatMap() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> crearUsuario());
        Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> crearComentarios());

        usuarioMono.flatMap(usuario -> comentariosMono.map(comentario -> new UsuarioComentarios(usuario, comentario)))
                .subscribe(usuarioComentarios -> log.info(usuarioComentarios.toString()));
        
    }


    public void convertirFLuxAMono() {
        List<Usuario> usuarios = Arrays.asList(new Usuario("freddy", "santacruz"), new Usuario("juan", "quimba"),
                new Usuario("rocio", "mora"), new Usuario("Gaby", "gahuancela"),
                new Usuario("Bruce", "Willis"), new Usuario("bruce", "lee"));

        Flux.fromIterable(usuarios)
                .collectList()
                .subscribe(e -> {
                    e.forEach(item -> log.info(item.toString()));
                });
    }

    public void convertirFLuxAList() {
        List<Usuario> usuarios = Arrays.asList(new Usuario("freddy", "santacruz"), new Usuario("juan", "quimba"),
                new Usuario("rocio", "mora"), new Usuario("Gaby", "gahuancela"),
                new Usuario("Bruce", "Willis"), new Usuario("bruce", "lee"));

        Flux.fromIterable(usuarios)
                .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                .flatMap(nombre -> {
                    if (nombre.contains("bruce".toUpperCase())) {
                        return Mono.just(nombre);
                    }
                    return Mono.empty();
                }).map(nombre -> {
                    return nombre.toUpperCase();
                }).subscribe(e -> log.info(e.toString()));

    }

    public void ejemploUsoDeFlatMap() {
        List<String> nombresList = Arrays.asList("freddy santacruz", "juan quimba",
                "rocio mora", "Gaby gahuancela", "Bruce Willis", "bruce lee");

        Flux.fromIterable(nombresList)
                .map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .flatMap(usuario -> {
                    if (usuario.getNombre().equalsIgnoreCase("bruce")) {
                        return Mono.just(usuario);
                    }
                    return Mono.empty();
                }).subscribe(e -> log.info(e.toString()));

    }


    public void ejemploFluxJust() {
        Flux<String> nombres = Flux.just("freddy santacruz", "juan quimba",
                "rocio mora", "Gaby gahuancela", "Bruce Willis", "bruce lee");

        nombres.subscribe(e -> log.info(e),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("ha finalizado la ejecucion del observable de nombres");
                    }
                });

    }

    public void ejemploFluxJustConvirtiendoAObjetoUsuario() {
        Flux<String> nombres = Flux.just("freddy santacruz", "juan quimba",
                "rocio mora", "Gaby gahuancela", "Bruce Willis", "bruce lee");

        Flux<Usuario> usuarios = nombres.map(nombre -> {
                    return nombre.toUpperCase();
                })
                .map(nombre -> new Usuario(nombre.split(" ")[0],
                        nombre.split(" ")[1]))
                .filter(usuario -> usuario.getNombre().equals("FREDDY"))
                .doOnNext(e -> {
                    if (e == null) {
                        throw new RuntimeException("Nombre no pueden ser vacios");
                    }
                    System.out.println(e.getNombre() + " " +
                            e.getApellido());
                })
                .map(usuario -> {
                    return usuario;
                });

        usuarios.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("ha finalizado la ejecucion del observable de usuarios");
                    }
                });
    }

    public void ejemploCrearFluxDesdeUnIterable() {
        List<String> nombresList = Arrays.asList("freddy santacruz", "juan quimba",
                "rocio mora", "Gaby gahuancela", "Bruce Willis", "bruce lee");

        Flux<String> nombresFromIterable = Flux.fromIterable(nombresList);


//		nombres= Flux.just("freddy","rocio","Gaby")Burce lee
//				.doOnNext(System.out::println);


        nombresFromIterable.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("ha finalizado la ejecucion del observable de nombresFromIterable");
                    }
                });

    }
}
