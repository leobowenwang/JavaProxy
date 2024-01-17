package micronaut;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.client.ProxyHttpClient;
import jakarta.inject.Inject;

import java.net.URI;

import org.reactivestreams.Publisher;

@Filter("/**")
public class ProxyFilter implements HttpServerFilter {

    @Inject
    ProxyHttpClient client;

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return client.proxy(request.mutate().uri(URI.create("http://localhost:8080")));
    }
}
