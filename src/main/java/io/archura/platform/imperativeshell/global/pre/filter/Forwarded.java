package io.archura.platform.imperativeshell.global.pre.filter;

import io.archura.platform.api.context.Context;
import io.archura.platform.api.http.HttpServerRequest;
import io.archura.platform.api.logger.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class Forwarded implements Consumer<HttpServerRequest> {

    public static final String FORWARDED_HEADER = "Forwarded";
    private Logger logger;

    @Override
    public void accept(final HttpServerRequest request) {
        final Map<String, Object> attributes = request.getAttributes();
        final Context context = (Context) attributes.get(Context.class.getSimpleName());
        logger = context.getLogger();
        final InetSocketAddress inetSocketAddress = request.getRemoteAddress();
        if (nonNull(inetSocketAddress)) {
            final InetAddress address = inetSocketAddress.getAddress();
            final String remoteAddress = address.getHostAddress();
            final List<String> forwardedValues = request.getRequestHeaders().get(FORWARDED_HEADER);
            forwardedValues.add(String.format("for=%s", remoteAddress));
            Collections.reverse(forwardedValues);
            final String forwardedValue = String.join(", ", forwardedValues);
            buildRequestWithForwardedHeader(request, forwardedValue);
            logger.debug("RemoteAddress found in the request, will add Forwarded header: %s", forwardedValue);
        } else {
            logger.debug("No remoteAddress in the request found, will not add Forwarded header.");
        }
    }

    void buildRequestWithForwardedHeader(
            final HttpServerRequest request, final String forwardedValue
    ) {
        request.getRequestHeaders().put(FORWARDED_HEADER, List.of(forwardedValue));
    }
}
