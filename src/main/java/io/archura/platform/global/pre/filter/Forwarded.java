package io.archura.platform.global.pre.filter;

import io.archura.platform.context.Context;
import io.archura.platform.logging.Logger;
import org.springframework.web.servlet.function.ServerRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class Forwarded implements UnaryOperator<ServerRequest> {

    public static final String FORWARDED_HEADER = "Forwarded";
    private Logger logger;

    @Override
    public ServerRequest apply(final ServerRequest request) {
        final Map<String, Object> attributes = request.attributes();
        final Context context = (Context) attributes.get(Context.class.getSimpleName());
        logger = context.getLogger();
        final Optional<InetSocketAddress> inetSocketAddressOptional = request.remoteAddress();
        if (inetSocketAddressOptional.isPresent()) {
            final InetSocketAddress inetSocketAddress = inetSocketAddressOptional.get();
            final InetAddress address = inetSocketAddress.getAddress();
            final String remoteAddress = address.getHostAddress();
            final List<String> forwardedValues = new ArrayList<>(request.headers().header(FORWARDED_HEADER));
            forwardedValues.add(String.format("for=%s", remoteAddress));
            Collections.reverse(forwardedValues);
            final String forwardedValue = String.join(", ", forwardedValues);
            logger.debug("RemoteAddress found in the request, will add Forwarded header: %s", forwardedValue);
            return buildRequestWithForwardedHeader(request, forwardedValue);
        } else {
            logger.debug("No remoteAddress in the request found, will not add Forwarded header.");
            return request;
        }
    }

    ServerRequest buildRequestWithForwardedHeader(ServerRequest request, String forwardedValue) {
        return ServerRequest.from(request)
                .header(FORWARDED_HEADER, forwardedValue)
                .build();
    }
}
