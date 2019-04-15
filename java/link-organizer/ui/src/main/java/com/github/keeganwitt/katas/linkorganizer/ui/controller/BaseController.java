package com.github.keeganwitt.katas.linkorganizer.ui.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.blackpepper.bowman.ClientFactory;
import uk.co.blackpepper.bowman.Configuration;

import java.nio.charset.StandardCharsets;

class BaseController {
    @Value("${baseApiUrl}")
    String baseApiUrl;

    ClientFactory createClientFactory() {
        String token = Base64.encodeBase64String((lookupUsername() + ":" + lookupPassword()).getBytes(StandardCharsets.UTF_8));
        return Configuration.builder().setBaseUri(baseApiUrl).setRestTemplateConfigurer(restTemplate ->
                restTemplate.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Basic " + token);
                    return execution.execute(request, body);
                })).build().buildClientFactory();
    }

    private String lookupUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    private String lookupPassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (String) authentication.getCredentials();
        }
        return null;
    }
}
