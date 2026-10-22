package com.cloudlink.repository;

import com.cloudlink.exception.CodeAlreadyExistsException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUrlRepository implements UrlMappingRepository {

    private final Map<String, UrlMapping> urlMappings = new HashMap<>();

    @Override
    public void save(UrlMapping mapping) {
        if (urlMappings.containsKey(mapping.getShortCode())) {
            throw new CodeAlreadyExistsException(mapping.getShortCode());
        }
        urlMappings.put(mapping.getShortCode(), mapping);
    }

    @Override
    public Optional<UrlMapping> findByCode(String code) {
        return Optional.ofNullable(urlMappings.get(code));
    }

    @Override
    public void update(UrlMapping mapping) {
        urlMappings.put(mapping.getShortCode(), mapping);
    }

    @Override
    public void incrementClickCount(String code) {
        UrlMapping mapping = urlMappings.get(code);
        if (mapping == null) {
            throw new UrlNotFoundException("Short URL does not exist");
        }
        mapping.incrementClickCount();
    }

    @Override
    public void delete(String code) {
        urlMappings.remove(code);
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        return urlMappings.values().stream()
                .filter(mapping -> mapping.getOriginalUrl().equals(originalUrl))
                .filter(mapping -> !mapping.isExpired())
                .findFirst();
    }
}