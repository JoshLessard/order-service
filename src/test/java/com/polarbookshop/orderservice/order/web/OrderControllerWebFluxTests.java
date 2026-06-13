package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderService;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest( OrderController.class )
public class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    public void whenBookNotAvailableThenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest( "1234567890", 3 );
        Order expectedOrder = Order.of( orderRequest.isbn(), null, null, 3, OrderStatus.REJECTED );
        given( orderService.submitOrder( orderRequest.isbn(), orderRequest.quantity() ) )
            .willReturn( Mono.just( expectedOrder ) );

        webClient
            .post()
            .uri( "/orders" )
            .bodyValue( orderRequest )
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody( Order.class ).value( actualOrder -> {
                assertThat( actualOrder ).isNotNull();
                assertThat( actualOrder.status() ).isEqualTo( OrderStatus.REJECTED );
            });
    }
}
