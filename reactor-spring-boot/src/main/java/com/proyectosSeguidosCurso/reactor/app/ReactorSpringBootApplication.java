package com.proyectosSeguidosCurso.reactor.app;

import com.proyectosSeguidosCurso.reactor.app.models.Comentarios;
import com.proyectosSeguidosCurso.reactor.app.models.Usuario;
import com.proyectosSeguidosCurso.reactor.app.models.UsuarioComentarios;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


@SpringBootApplication
public class ReactorSpringBootApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReactorSpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReactorSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ejemploCOntrapresion();
        // ejemploUsuarioComentariosZipWith();
    }

    public void ejemploCOntrapresion() throws InterruptedException {
        Flux.range(0, 10)
                .log()
                .limitRate(2)
                .subscribe();

    }

    public void ejemploCOntrapresionSobreEscribiendoMetodoOnSubcriber() throws InterruptedException {
        Flux.range(0, 10)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    private Integer limit = 2;
                    private Integer used = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(limit);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info(integer.toString());
                        used++;
                        if (used == limit) {
                            used = 0;
                            subscription.request(limit);
                        }

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void crearFluxDesdeCero() throws InterruptedException {
        Flux.create(emitter -> {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        private Integer contador = 0;

                        @Override
                        public void run() {
                            emitter.next(++contador);
                            if (contador == 10) {
                                timer.cancel();
                                emitter.complete();
                            }

                            if (contador == 5) {
                                timer.cancel();
                                emitter.error(new InterruptedException("Error, se ha detenido el flux en 5"));
                            }
                        }
                    }, 1000, 1000);
                })
                //.doOnNext(next -> log.info(next.toString()))
                //.doOnComplete(() -> log.info("Hemos terminado"))
                .subscribe(next -> log.info(next.toString()),
                        error -> log.error(error.toString()),
                        () -> log.info("Hemos terminado"));
    }

    public void intervaloInfinito() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(latch::countDown)
                .flatMap(i -> {
                    if (i > 5) {
                        return Flux.error(new InterruptedException("solamente hasta 5!"));
                    }
                    return Flux.just(i);
                })
                .map(i -> "Hola " + i)
                //.doOnNext(s -> log.info(s))
                .retry(2)
                .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

        latch.await();
    }

    public void ejemploIntervalElementos() {
        Flux<Integer> rango = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.info(i.toString()));

        rango.blockLast();

    }

    public void ejemploInterval() {
        Flux<Integer> rango = Flux.range(1, 12);
        Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

        //este aqui utiliza hilos pero no se visualiza
        //rango.zipWith(retraso, (ran, retr) -> ran).doOnNext(i -> log.info(i.toString())).subscribe();
        //el block last hace que se bloque todos los flux
        rango.zipWith(retraso, (ran, retr) -> ran).doOnNext(i -> log.info(i.toString())).blockLast();
    }

    public void ejemploUsuarioComentariosZipWithRango() {
        Flux.just(1, 2, 3, 4).
                map(i -> (i * 2))
                .zipWith(Flux.range(1, 4), (uno, dos) -> {
                    return String.format("Primer FLux: %d, Segundo Flux: %d", uno, dos);
                }).subscribe(texto -> log.info(texto));
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


    public void ejemploUsuarioComentariosZipWith2() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> crearUsuario());
        Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> crearComentarios());

        Mono<UsuarioComentarios> usuarioComentariosMono = usuarioMono.zipWith(comentariosMono)
                .map(tuplaUsuarioComentario -> {
                    Usuario usuario = tuplaUsuarioComentario.getT1();
                    Comentarios comentarios = tuplaUsuarioComentario.getT2();
                    return new UsuarioComentarios(usuario, comentarios);
                });

        usuarioComentariosMono.subscribe(usuarioComentarios -> log.info(usuarioComentarios.toString()));

    }

    public void ejemploUsuarioComentariosZipWith() {
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> crearUsuario());
        Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> crearComentarios());

        Mono<UsuarioComentarios> usuarioComentariosMono = usuarioMono.zipWith(comentariosMono,
                (usuario, comentario) -> new UsuarioComentarios(usuario, comentario));

        usuarioComentariosMono.subscribe(usuarioComentarios -> log.info(usuarioComentarios.toString()));

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
