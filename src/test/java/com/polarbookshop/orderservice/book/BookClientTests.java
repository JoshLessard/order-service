package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

public class BookClientTests {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        bookClient = new BookClient(
            WebClient.builder()
                .baseUrl( mockWebServer.url( "/" ).uri().toString() )
                .build()
        );
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void whenBookExistsThenReturnBook() {
        String bookIsbn = "1234567890";
        MockResponse mockResponse = new MockResponse()
            .addHeader( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE )
            .setBody(
                """
                {
                    "isbn": %s,
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90,
                    "publisher": "Polarsophia"
                }
                """.formatted( bookIsbn )
            );
        mockWebServer.enqueue( mockResponse );

        Mono<Book> book = bookClient.getBookByIsbn( bookIsbn );

        StepVerifier.create( book )
            .expectNextMatches( b -> b.isbn().equals( bookIsbn ) )
            .verifyComplete();
    }
}
