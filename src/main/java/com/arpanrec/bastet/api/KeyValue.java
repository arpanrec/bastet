package com.arpanrec.bastet.api;

import com.arpanrec.bastet.physical.KVData;
import com.arpanrec.bastet.physical.KVDataServiceImpl;
import com.arpanrec.bastet.physical.KVDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/keyvalue/**", produces = "application/json", consumes = "application/json")
public class KeyValue {

    private final KVDataService kvDataService;

    public KeyValue(@Autowired KVDataServiceImpl kvDataServiceImpl) {
        this.kvDataService = kvDataServiceImpl;
    }

    @GetMapping
    public HttpEntity<KVData> get(HttpServletRequest request) {
        String key = new AntPathMatcher().extractPathWithinPattern(
            request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(), request.getRequestURI());
        Optional<KVData> keyValue = kvDataService.getMaybe(key);
        return keyValue.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public HttpEntity<?> save(@RequestBody KVData keyValue) {
        kvDataService.saveOrUpdate(keyValue);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public HttpEntity<?> update(@RequestBody KVData keyValue) {
        this.kvDataService.saveOrUpdate(keyValue);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
