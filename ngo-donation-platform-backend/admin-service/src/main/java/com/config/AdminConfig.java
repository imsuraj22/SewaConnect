package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
public class AdminConfig {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            @Value("${app.internal-api-key}") String internalApiKey,
            @Value("${user.service.url}") String userServiceUrl
    ) {
        return builder
                .interceptors(List.of(
                        userInternalApiKeyInterceptor(internalApiKey, userServiceUrl),
                        bearerForwardingInterceptor()
                ))
                .build();
    }

    /**
     * Sends {@code X-Internal-Api-Key} only for calls to user-service {@code /internal/*} (not for NGO or others).
     */
    private static ClientHttpRequestInterceptor userInternalApiKeyInterceptor(String key, String userServiceBaseUrl) {
        String base = trimTrailingSlash(userServiceBaseUrl);
        return (request, body, execution) -> {
            if (isUserServiceInternalRequest(request, base)) {
                request.getHeaders().set("X-Internal-Api-Key", key);
            }
            return execution.execute(request, body);
        };
    }

    private static boolean isUserServiceInternalRequest(HttpRequest request, String userServiceBaseUrl) {
        if (!StringUtils.hasText(userServiceBaseUrl)) {
            return false;
        }
        String url = request.getURI().toString();
        int q = url.indexOf('?');
        if (q >= 0) {
            url = url.substring(0, q);
        }
        url = trimTrailingSlash(url);
        if (!url.startsWith(userServiceBaseUrl)) {
            return false;
        }
        String path = request.getURI().getPath();
        return path != null && path.startsWith("/internal/");
    }

    private static String trimTrailingSlash(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Forwards the incoming admin JWT to NGO service (and other outbound calls that need it).
     */
    private static ClientHttpRequestInterceptor bearerForwardingInterceptor() {
        return (request, body, execution) -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                String auth = servletAttrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.hasText(auth)) {
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, auth);
                }
            }
            return execution.execute(request, body);
        };
    }
}
