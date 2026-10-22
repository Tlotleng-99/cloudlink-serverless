package com.cloudlink.repository;

import com.cloudlink.exception.CodeAlreadyExistsException;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingRepository {

    /**
     * Stores a new mapping.
     *
     * @throws CodeAlreadyExistsException if the short code is already taken (nothing is overwritten)
     */
    void save(UrlMapping mapping);

    Optional<UrlMapping> findByCode(String code);

    /**
     * Replaces an existing mapping. Do NOT use this to count clicks - use
     * {@link #incrementClickCount(String)}, which is safe when several clicks arrive at once.
     *
     * @throws UrlNotFoundException if the mapping does not exist
     */
    void update(UrlMapping mapping);

    /**
     * Atomically adds one click to the stored count.
     *
     * @throws UrlNotFoundException if the mapping does not exist
     */
    void incrementClickCount(String code);

    void delete(String code);

    /** @return a stored, non-expired mapping for this URL, if there is one */
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}