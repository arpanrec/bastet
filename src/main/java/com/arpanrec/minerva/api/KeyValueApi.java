package com.arpanrec.minerva.api;

import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

@RestController
public class KeyValueApi {

    private final KeyValuePersistence keyValuePersistence;

    public KeyValueApi(@Autowired KeyValuePersistence keyValuePersistence) {
        this.keyValuePersistence = keyValuePersistence;
    }

    @GetMapping(path = "/keyvaule/**", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> get(HttpServletRequest request) {
        String key = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(), request.getRequestURI());
        Optional<KeyValue> keyValue = keyValuePersistence.get(key, 0);
        return keyValue.map(value -> ResponseEntity.ok(value.getValue())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found"));
    }

    @PostMapping(path = "/keyvaule/**", consumes = "application/json", produces = "application/json")
    public KeyValue set(@RequestBody KeyValue keyValue, HttpServletRequest request) {
        String key = new AntPathMatcher().extractPathWithinPattern(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(), request.getRequestURI());
        keyValue.setKey(key);
        return keyValuePersistence.save(keyValue);
    }
}
