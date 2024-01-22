package com.arpanrec.minerva.api;

import com.arpanrec.minerva.kv.KeyValue;
import com.arpanrec.minerva.kv.KeyValueRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    private final KeyValueRepository keyValueRepository;

    public KeyValueApi(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @GetMapping(path = "/keyvaule/**", produces = "text/plain")
    public ResponseEntity<String> get(HttpServletRequest request) {
        String key = new AntPathMatcher().extractPathWithinPattern(
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),
                request.getRequestURI());
        Optional<KeyValue> keyValue = keyValueRepository.findByKey(key);
        return keyValue.map(value -> ResponseEntity.ok(value.getValue()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found"));
    }

    @PostMapping(path = "/keyvaule/**", consumes = "text/plain", produces = "text/plain")
    public String set(@RequestBody String secretContent, HttpServletRequest request) {
        String key = new AntPathMatcher().extractPathWithinPattern(
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),
                request.getRequestURI());

        var secret = new KeyValue(key, secretContent);
        keyValueRepository.save(secret);
        return "File Uploaded :: " + key;
    }

}
