package io.archura.platform.global.pre.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.archura.platform.context.Context;
import io.archura.platform.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.function.ServerRequest;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.archura.platform.global.pre.filter.Forwarded.FORWARDED_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForwardedTest {

    @Mock
    private Forwarded forwarded;

    @Mock
    private ServerRequest request;

    @Mock
    private ServerRequest.Headers headers;

    @Mock
    private HttpHeaders httpHeaders;

    @Captor
    ArgumentCaptor<String> argumentCaptor;

    @Mock
    private Context context;

    @Mock
    private Logger logger;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> attributes = new HashMap<>();

    @BeforeEach
    void setup() {
        when(forwarded.apply(request)).thenCallRealMethod();
        attributes.clear();
        when(context.getLogger()).thenReturn(logger);
        doNothing().when(logger).debug(any(), any());
    }

    @Test
    void should_NotSetForwardedHeader_When_CalledWithoutRemoteIp() {
        when(request.remoteAddress()).thenReturn(Optional.empty());
        when(request.attributes()).thenReturn(attributes);
        attributes.put(Context.class.getSimpleName(), context);

        this.forwarded.apply(request);

        verifyNoInteractions(httpHeaders);
    }

    @Test
    void should_SetForwardedHeader_When_CalledWithRemoteIp() {
        // Forwarded: for=10.11.12.13
        final String remoteIp = "10.11.12.13";
        final String expectedHeaderValue = String.format("for=%s", remoteIp);
        InetSocketAddress remoteAddress = new InetSocketAddress(remoteIp, 8888);
        when(request.remoteAddress()).thenReturn(Optional.of(remoteAddress));
        when(request.headers()).thenReturn(headers);
        when(request.attributes()).thenReturn(attributes);
        attributes.put(Context.class.getSimpleName(), context);

        this.forwarded.apply(request);
        verify(forwarded).buildRequestWithForwardedHeader(any(), argumentCaptor.capture());

        final String actualHttpHeaderValue = argumentCaptor.getValue();
        assertNotNull(actualHttpHeaderValue);
        assertEquals(expectedHeaderValue, actualHttpHeaderValue);
    }

    @Test
    void should_AddForwardedHeader_When_CalledWithRemoteIpAndForwardedHeader() {
        // Forwarded: for=10.11.12.13, for=99.88.77.66
        final String initialForwardedValue = "for=99.88.77.66";
        final String remoteIp = "10.11.12.13";
        final String expectedHeaderValue = String.format("for=%s, %s", remoteIp, initialForwardedValue);
        InetSocketAddress remoteAddress = new InetSocketAddress(remoteIp, 8888);
        when(request.remoteAddress()).thenReturn(Optional.of(remoteAddress));
        when(request.headers()).thenReturn(headers);
        when(headers.header(FORWARDED_HEADER)).thenReturn(Collections.singletonList(initialForwardedValue));
        when(request.attributes()).thenReturn(attributes);
        attributes.put(Context.class.getSimpleName(), context);

        this.forwarded.apply(request);

        verify(forwarded).buildRequestWithForwardedHeader(any(), argumentCaptor.capture());

        final String actualHttpHeaderValue = argumentCaptor.getValue();
        assertNotNull(actualHttpHeaderValue);
        assertEquals(expectedHeaderValue, actualHttpHeaderValue);
    }

}