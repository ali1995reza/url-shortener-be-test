package com.vivy.shortener.repository.url;

import com.vivy.shortener.model.UrlEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UrlRepository extends CrudRepository<UrlEntity, Long> {

    Optional<UrlEntity> findByUrlId(String urlId);

    Long deleteByUrlId(String urlId);

}
