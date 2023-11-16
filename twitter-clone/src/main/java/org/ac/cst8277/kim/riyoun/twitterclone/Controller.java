package org.ac.cst8277.kim.riyoun.twitterclone;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import lombok.Data;

@RestController
public class Controller {
    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private USMConnector usmConnector;

    @GetMapping("/producer/{id}/message")
    public Mono<ResponseEntity<?>> getAllMessagesByProducer(@PathVariable("id") String id) {
        return authenticate(id, user -> {
            return Mono.just(ResponseEntity.ok().body(messageRepo.findAllByProducer(UUID.fromString(id))));
        });
    }

    @GetMapping("/subscriber/{id}/message")
    public Mono<ResponseEntity<?>> getAllMessagesBySubscriber(@PathVariable("id") String id) {
        return authenticate(id, user -> {
            return Mono.just(ResponseEntity.ok().body(messageRepo.findAllBySubscriber(UUID.fromString(id))));
        });
    }

    @GetMapping("/subscriber/{id}/subscription")
    public Mono<ResponseEntity<?>> getSubscriptionsBySubscriber(@PathVariable("id") String id) {
        return authenticate(id, user -> {
            return Mono.just(ResponseEntity.ok().body(subscriptionRepo.findAllBySubscriber(UUID.fromString(id))));
        });
    }

    @PostMapping("/message")
    public Mono<ResponseEntity<?>> createMessage(@RequestBody CreateMessageRequest request) {
        return authenticate(request.getProducerId(), res -> {
            Message msg = new Message();
            Producer producer = new Producer();
            producer.setId(UUID.fromString(request.getProducerId()));
            msg.setContent(request.getContent());
            msg.setProducer(producer);
            return Mono.just(ResponseEntity.ok(messageRepo.save(msg)));
        });
    }

    @PostMapping("/subscription")
    public Mono<ResponseEntity<?>> subscribe(@RequestBody CreateSubscriptionRequest request) {
        String producerId = request.getProducerId();
        String subscriberId = request.getSubscriberId();

        return authenticate(producerId, res1 -> {
            return authenticate(subscriberId, res2 -> {
                Producer producer = new Producer();
                producer.setId(UUID.fromString(producerId));
                Subscriber subscriber = new Subscriber();
                subscriber.setId(UUID.fromString(subscriberId));

                Subscription s = new Subscription();
                s.setProducer(producer);
                s.setSubscriber(subscriber);

                return Mono.just(ResponseEntity.ok().body(subscriptionRepo.save(s)));
            });
        });
    }

    @DeleteMapping("/message/{id}")
    public void deleteMEssageById(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        messageRepo.deleteById(uuid);
    }

    @DeleteMapping("/subscription/{id}")
    public void deleteSubscriptionById(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        subscriptionRepo.deleteById(uuid);
    }

    private Mono<ResponseEntity<?>> authenticate(
            String id,
            Function<Map<String, Object>, Mono<ResponseEntity<?>>> callback) {
        URI redirectUri = usmConnector.getUsmUrl(id);
        return usmConnector.retrieveUsmData(id)
                .map(res -> Optional.of(res)).defaultIfEmpty(Optional.empty())
                .flatMap(res -> {
                    if (res.isEmpty() || !res.get().get("id").equals(id)) {
                        return Mono.just(
                                ResponseEntity
                                        .status(HttpStatus.SEE_OTHER)
                                        .location(redirectUri)
                                        .build());
                    }
                    return callback.apply(res.get());
                });
    }
}

@Data
class CreateMessageRequest {
    private String producerId;
    private String content;
}

@Data
class CreateSubscriptionRequest {
    private String producerId;
    private String subscriberId;
}