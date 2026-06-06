package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            @Value("${app.internal-api-key}") String internalApiKey,
            @Value("${ngo.service.url}") String ngoServiceUrl
    ) {
        String ngoBase = trimTrailingSlash(ngoServiceUrl);
        return builder
                .interceptors(List.of(ngoRegisterInternalKeyInterceptor(internalApiKey, ngoBase)))
                .build();
    }

    /**
     * Adds {@code X-Internal-Api-Key} only for {@code POST} to NGO {@code /api/ngos/register/{userId}}.
     */
    private static ClientHttpRequestInterceptor ngoRegisterInternalKeyInterceptor(String key, String ngoBaseUrl) {
        return (request, body, execution) -> {
            if (isNgoRegisterPost(request, ngoBaseUrl)) {
                request.getHeaders().set("X-Internal-Api-Key", key);
            }
            return execution.execute(request, body);
        };
    }

    private static boolean isNgoRegisterPost(HttpRequest request, String ngoBaseUrl) {
        if (!StringUtils.hasText(ngoBaseUrl)
                || request.getMethod() == null
                || !HttpMethod.POST.equals(request.getMethod())) {
            return false;
        }
        String url = request.getURI().toString();
        int q = url.indexOf('?');
        if (q >= 0) {
            url = url.substring(0, q);
        }
        url = trimTrailingSlash(url);
        if (!url.startsWith(ngoBaseUrl)) {
            return false;
        }
        String path = request.getURI().getPath();
        return path != null && path.startsWith("/api/ngos/register/");
    }

    private static String trimTrailingSlash(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
