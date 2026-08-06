package com.cloudlink.repository;

import com.cloudlink.model.UrlMapping;

import java.util.Optional;

public class OracleNoSqlRepository implements UrlMappingRepository {

    @Override
    public void save(UrlMapping mapping) {

    }

    @Override
    public Optional<UrlMapping> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public void update(UrlMapping mapping) {

    }

    @Override
    public void delete(String code) {

    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        return Optional.empty();
    }
}