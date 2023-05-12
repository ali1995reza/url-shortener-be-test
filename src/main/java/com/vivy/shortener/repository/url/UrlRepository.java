package com.vivy.shortener.repository.url;

import com.vivy.shortener.model.UrlEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UrlRepository extends CrudRepository<UrlEntity, Long> {

    Optional<UrlEntity> findByUrlId(String urlId);

    @Transactional
    Long deleteByUrlId(String urlId);

}
