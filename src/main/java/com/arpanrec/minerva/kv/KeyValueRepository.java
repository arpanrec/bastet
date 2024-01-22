package com.arpanrec.minerva.kv;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KeyValueRepository extends CrudRepository<KeyValue, Long> {

    Optional<KeyValue> findByKey(String key);

}
