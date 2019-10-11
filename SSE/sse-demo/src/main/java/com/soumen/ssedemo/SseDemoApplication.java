package com.soumen.ssedemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class SseDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SseDemoApplication.class, args);
    }

}

@RestController
@AllArgsConstructor
class CommentController {
    private final CommentRepository commentRepository;

    @GetMapping(path = "/comment/feed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Comment> feed() {
        return this.commentRepository.findAll();
    }
}
@Repository
class ReactiveCommentRepo implements CommentRepository {

    @Override
    public Flux<Comment> findAll() {
        return Flux.interval(Duration.ofSeconds(1))
                .onBackpressureDrop()
                .map(this::generateComment)
                .flatMapIterable(x -> x);
    }

    private List<Comment> generateComment(Long interval) {
        Comment comment = new Comment(CommentGenerator.randomAuthor(),
                CommentGenerator.randomMessage(), CommentGenerator.getCurrentTimeStamp());
        return Arrays.asList(comment);
    }
}

@Controller
class WebMvcController {
    @GetMapping("/")
    public String index(final Model model) {
        return "index";
    }
}

interface CommentRepository {
    Flux<Comment> findAll();
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Comment {
    private String author;
    private String message;
    private String timestamp;
}

class CommentGenerator {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final List<String> COMMENT_AUTHOR =
            Arrays.asList(
                    "Soumen", "Madhumita", "Jack", "Harry", "Jacob",
                    "Isla", "Emily", "Poppy", "Ava", "Isabella");


    private static final List<String> COMMENT_MESSAGE =
            Arrays.asList(
                    "I Love this!",
                    "Me too!",
                    "Wow",
                    "True!",
                    "Hello everyone here?",
                    "Good!");

    public static String randomAuthor() {
        return COMMENT_AUTHOR.get(RANDOM.nextInt(COMMENT_AUTHOR.size()));
    }

    public static String randomMessage() {
        return COMMENT_MESSAGE.get(RANDOM.nextInt(COMMENT_MESSAGE.size()));
    }

    public static String getCurrentTimeStamp() {
        return dtf.format(LocalDateTime.now());
    }
}
