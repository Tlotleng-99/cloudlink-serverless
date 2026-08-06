package com.cloudlink.service;


import com.cloudlink.exception.InvalidUrlException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;
import com.cloudlink.repository.UrlMappingRepository;
import com.cloudlink.util.ShortCodeGenerator;
import com.cloudlink.validation.UrlValidation;


import java.time.Instant;
import java.util.Optional;


public class UrlService {
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlMappingRepository repository;
    private final UrlValidation validator = new UrlValidation();

    public UrlService(UrlMappingRepository repository, ShortCodeGenerator shortCodeGenerator){
        this.repository = repository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public String shortenUrl(String originalUrl) {
        return shortenUrl(originalUrl, null);
    }

    public String shortenUrl(String originalUrl, Instant expiresAt){
        if(!validator.isValid(originalUrl)){
            throw new InvalidUrlException("Invalid URL");
        }
        Optional<UrlMapping>existing = repository.findByOriginalUrl(originalUrl);
        if(existing.isPresent()){
            return existing.get().getShortCode();
        }
        String code = shortCodeGenerator.generateCode();
        UrlMapping mapping = new UrlMapping(code, originalUrl, expiresAt);
        repository.save(mapping);
        return code;
    }

    public String redirect(String code){
        Optional<UrlMapping> mapping = repository.findByCode(code);
        if(mapping.isEmpty()){
            throw new UrlNotFoundException("Short url does not exist");
        }
            UrlMapping urlMapping = mapping.get();

        if(urlMapping.isExpired()){
            throw new UrlNotFoundException("This short URL has expired.");
        }
            urlMapping.incrementClickCount();
            repository.update(urlMapping);
            return urlMapping.getOriginalUrl();
    }
    public UrlMapping getStats(String code){
        Optional<UrlMapping> mapping = repository.findByCode(code);

        if(mapping.isEmpty()){
            throw new  UrlNotFoundException("Short Url not found");
        }
        return mapping.get();
    }

    public void deleteUrl(String code){
        Optional<UrlMapping> mapping = repository.findByCode(code);
        if(mapping.isEmpty()){
            throw new UrlNotFoundException("Short URL does not exist");

        }
        repository.delete(code);
    }


}
