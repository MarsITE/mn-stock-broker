package ua.apryby.udemy;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.apryby.udemy.broker.Symbol;
import ua.apryby.udemy.broker.SymbolsController;
import ua.apryby.udemy.broker.data.InMemoryStore;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SymbolsControllerTest {

    @Inject
    @Client("/symbols")
    HttpClient client;

    @Inject
    InMemoryStore inMemoryStore;

    private static final Logger LOG = LoggerFactory.getLogger(SymbolsController.class);

    @BeforeEach
    void setUp() {
        inMemoryStore.initializeWith(10);
    }

    @Test
    void symbolsEndpointReturnsListOfSymbol() {
        var response = client.toBlocking().exchange("/", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(10, response.getBody().get().size());
    }

    @Test
    void symbolsEndpointReturnsTheCorrectSymbol() {
        var testSymbol = new Symbol("Test");
        inMemoryStore.getSymbols().put(testSymbol.value(), testSymbol);

        var response = client.toBlocking().exchange("/" + testSymbol.value(), Symbol.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testSymbol, response.getBody().get());
    }

    @Test
    void symbolsEndpointReturnsListOfSymbolTakingQuerryParametersIntoAccount() {
        var response = client.toBlocking().exchange("/filter?max=10", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());

        LOG.debug("Max: 10 {}", response.getBody().get().toPrettyString());
        assertEquals(10, response.getBody().get().size());

        var offset7 = client.toBlocking().exchange("/filter?offset=7", JsonNode.class);
        assertEquals(HttpStatus.OK, offset7.getStatus());

        LOG.debug("Offset: 7 {}", offset7.getBody().get().toPrettyString());
        assertEquals(3, offset7.getBody().get().size());

        var max2offset7 = client.toBlocking().exchange("/filter?max=2&offset=7", JsonNode.class);
        assertEquals(HttpStatus.OK, max2offset7.getStatus());

        LOG.debug("Max 2, Offset: 7 {}", max2offset7.getBody().get().toPrettyString());
        assertEquals(2, max2offset7.getBody().get().size());
    }
}
