package com.cloudlink.repository;
import com.cloudlink.model.UrlMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUrlRepository implements UrlMappingRepository {

    private  final Map<String, UrlMapping> urlMappings = new HashMap<>();
    @Override
    public void save(UrlMapping mapping){
        urlMappings.put(mapping.getShortCode(), mapping);
    }

    @Override
    public Optional<UrlMapping> findByCode(String code){
        return Optional.ofNullable(urlMappings.get(code));
    }

    @Override
    public void update(UrlMapping mapping){
        urlMappings.put(mapping.getShortCode(),mapping);
    }


}
