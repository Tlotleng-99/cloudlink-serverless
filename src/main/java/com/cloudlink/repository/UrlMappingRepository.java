package com.cloudlink.repository;
import com.cloudlink.model.UrlMapping;
import java.util.Optional;

public interface UrlMappingRepository {
    void save(UrlMapping mapping);
    Optional<UrlMapping> findByCode(String code);
    void update(UrlMapping mapping);
    void delete(String code);
    Optional<UrlMapping>findByOriginalUrl(String originalUrl);
}
