package com.cloudlink.service;

import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.UrlMappingRepository;
import com.cloudlink.util.ShortCodeGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

public class UrlService {
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlMappingRepository repository;

    public UrlService(UrlMappingRepository repository, ShortCodeGenerator shortCodeGenerator){
        this.repository = repository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public String shortenUrl(String originalUrl) {
        String code = shortCodeGenerator.generateCode();
        UrlMapping mapping = new UrlMapping(code, originalUrl, 0, LocalDateTime.now().toString());

        repository.save(mapping);

        return code;
    }

    public String redirect(String code){
        Optional<UrlMapping> mapping = repository.findByCode(code);
        if(mapping.isEmpty()){
            return null;
        }
            UrlMapping urlMapping = mapping.get();
            urlMapping.setClickCount(urlMapping.getClickCount() +1);
            return urlMapping.getOriginalUrl();
    }


}
