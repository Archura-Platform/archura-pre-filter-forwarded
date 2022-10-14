//package io.archura.platform.imperativeshell.global.pre.filter;
//
//import io.archura.platform.api.context.Context;
//import io.archura.platform.api.http.HttpServerRequest;
//import io.archura.platform.api.logger.Logger;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.net.InetSocketAddress;
//import java.util.*;
//
//import static io.archura.platform.imperativeshell.global.pre.filter.Forwarded.FORWARDED_HEADER;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ForwardedTest {
//
//    @Mock
//    private Forwarded forwarded;
//
//    @Mock
//    private HttpServerRequest request;
//
//    @Mock
//    private Map<String, List<String>> headers;
//
//    @Captor
//    ArgumentCaptor<String> argumentCaptor;
//
//    @Mock
//    private Context context;
//
//    @Mock
//    private Logger logger;
//
//    private final Map<String, Object> attributes = new HashMap<>();
//
//    @BeforeEach
//    void setup() {
//        when(forwarded.accept(request)).thenCallRealMethod();
//        attributes.clear();
//        when(context.getLogger()).thenReturn(logger);
//        doNothing().when(logger).debug(any(), any());
//    }
//
//    @Test
//    void should_NotSetForwardedHeader_When_CalledWithoutRemoteIp() {
//        when(request.getRemoteAddress()).thenReturn(null);
//        when(request.getAttributes()).thenReturn(attributes);
//        attributes.put(Context.class.getSimpleName(), context);
//
//        this.forwarded.apply(request);
//
//        verify(forwarded, never()).buildRequestWithForwardedHeader(any(), anyString());
//    }
//
//    @Test
//    void should_SetForwardedHeader_When_CalledWithRemoteIp() {
//        // Forwarded: for=10.11.12.13
//        final String remoteIp = "10.11.12.13";
//        final String expectedHeaderValue = String.format("for=%s", remoteIp);
//        InetSocketAddress remoteAddress = new InetSocketAddress(remoteIp, 8888);
//        when(request.getRemoteAddress()).thenReturn(remoteAddress);
//        when(request.getRequestHeaders()).thenReturn(headers);
//        when(request.getAttributes()).thenReturn(attributes);
//        attributes.put(Context.class.getSimpleName(), context);
//
//        this.forwarded.apply(request);
//        verify(forwarded).buildRequestWithForwardedHeader(any(), argumentCaptor.capture());
//
//        final String actualHttpHeaderValue = argumentCaptor.getValue();
//        assertNotNull(actualHttpHeaderValue);
//        assertEquals(expectedHeaderValue, actualHttpHeaderValue);
//    }
//
//    @Test
//    void should_AddForwardedHeader_When_CalledWithRemoteIpAndForwardedHeader() {
//        // Forwarded: for=10.11.12.13, for=99.88.77.66
//        final String initialForwardedValue = "for=99.88.77.66";
//        final String remoteIp = "10.11.12.13";
//        final String expectedHeaderValue = String.format("for=%s, %s", remoteIp, initialForwardedValue);
//        InetSocketAddress remoteAddress = new InetSocketAddress(remoteIp, 8888);
//        when(request.getRemoteAddress()).thenReturn(remoteAddress);
//        when(request.getRequestHeaders()).thenReturn(headers);
//        when(headers.get(FORWARDED_HEADER)).thenReturn(Collections.singletonList(initialForwardedValue));
//        when(request.getAttributes()).thenReturn(attributes);
//        attributes.put(Context.class.getSimpleName(), context);
//
//        this.forwarded.apply(request);
//
//        verify(forwarded).buildRequestWithForwardedHeader(any(), argumentCaptor.capture());
//
//        final String actualHttpHeaderValue = argumentCaptor.getValue();
//        assertNotNull(actualHttpHeaderValue);
//        assertEquals(expectedHeaderValue, actualHttpHeaderValue);
//    }
//
//}