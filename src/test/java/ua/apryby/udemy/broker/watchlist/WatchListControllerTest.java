package ua.apryby.udemy.broker.watchlist;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.apryby.udemy.broker.Symbol;
import ua.apryby.udemy.broker.data.InMemoryAccountStore;

import java.util.UUID;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    HttpClient client;

    @Inject
    InMemoryAccountStore inMemoryAccountStore;

    @BeforeEach
    void setUp() {
        inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void returnsEmptyWatchListForTestAccount() {
        final WatchList result = client.toBlocking().retrieve(GET("/"), WatchList.class);
        assertNull(result.symbols());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

   /* @Test()
    void returnsWatchListForTestAccount() {
        inMemoryAccountStore.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
                Stream.of("AAPL", "GOOGL", "MSFT")
                        .map(Symbol::new)
                        .toList()
        ));

        var response = client.toBlocking().exchange("/", JsonNode.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("""
                {
                  "symbols" : [ {
                    "value" : "AAPL"
                  }, {
                    "value" : "GOOGL"
                  }, {
                    "value" : "MSFT"
                  } ]
                }""", response.getBody().get().toPrettyString());
    }
*/
    @Test
    void canUpdateWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();

        final var request = HttpRequest.PUT("/", new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON);

        final HttpResponse<Object> added = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
    }

    @Test
    void canDeleteWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();
        inMemoryAccountStore.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));

        assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());

        var deleted = client.toBlocking().exchange(HttpRequest.DELETE("/"));

        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatus());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }
}
