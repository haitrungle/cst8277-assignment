package org.ac.cst8277.kim.riyoun.twitterclone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class USMConnector {
    // get value from the configuration properties file
    @Value("${usm.base}")
    private String baseUsm;

    @Value("${usm.public-base}")
    private String publicBaseUsm;

    @Value("${usm.paths.user}")
    private String uriUser;

    public URI getUsmUrl(String id) {
        try {
            return new URI(publicBaseUsm + uriUser + "/" + id);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Mono<Map<String, Object>> retrieveUsmData(String id) {
        WebClient client = WebClient.builder().baseUrl(baseUsm)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

        return client
            .method(HttpMethod.GET)
            .uri(uriUser + "/" + id)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("UTF-8"))
            .retrieve()
            .toEntity(String.class)
            .flatMap(res -> {
                try {
                    if (res.getStatusCode() != HttpStatus.OK)
                        return Mono.empty();
                    ObjectMapper objectMapper = new ObjectMapper();
                    return Mono.just(objectMapper.readValue(res.getBody(), new TypeReference<Map<String, Object>>() {}));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return Mono.empty();
                }
            })
            .onErrorResume(error -> {
                return Mono.empty();
            });
    }
}