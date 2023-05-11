package com.vivy.shortener.test.service;

import com.vivy.shortener.model.UrlEntity;
import com.vivy.shortener.repository.url.UrlRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Primary
@Repository

public class ObserverUrlRepository implements UrlRepository {

    private final UrlRepository wrapped;

    private int totalFindByUrlIdCall = 0;

    public ObserverUrlRepository(UrlRepository wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Optional<UrlEntity> findByUrlId(String urlId) {
        ++totalFindByUrlIdCall;
        return wrapped.findByUrlId(urlId);
    }

    @Override
    public Long deleteByUrlId(String urlId) {
        return wrapped.deleteByUrlId(urlId);
    }

    @Override
    public <S extends UrlEntity> S save(S entity) {
        return wrapped.save(entity);
    }

    @Override
    public <S extends UrlEntity> Iterable<S> saveAll(Iterable<S> entities) {
        return wrapped.saveAll(entities);
    }

    @Override
    public Optional<UrlEntity> findById(Long aLong) {
        return wrapped.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return wrapped.existsById(aLong);
    }

    @Override
    public Iterable<UrlEntity> findAll() {
        return wrapped.findAll();
    }

    @Override
    public Iterable<UrlEntity> findAllById(Iterable<Long> longs) {
        return wrapped.findAllById(longs);
    }

    @Override
    public long count() {
        return wrapped.count();
    }

    @Override
    public void deleteById(Long aLong) {
        wrapped.deleteById(aLong);
    }

    @Override
    public void delete(UrlEntity entity) {
        wrapped.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        wrapped.deleteAllById(longs);
    }

    @Override
    public void deleteAll(Iterable<? extends UrlEntity> entities) {
        wrapped.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        wrapped.deleteAll();
    }

    public void refresh() {
        totalFindByUrlIdCall = 0;
    }

    public int getTotalFindByUrlIdCall() {
        return totalFindByUrlIdCall;
    }
}
