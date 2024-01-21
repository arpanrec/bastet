package com.arpanrec.minerva.api;

import com.arpanrec.minerva.kv.KeyValue;
import com.arpanrec.minerva.kv.KeyValueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SecretsApi {

    private final KeyValueRepository keyValueRepository;

    public SecretsApi(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @PostMapping(path = "/v1/secret/{path}")
    public String uploadFile(@PathVariable String path, @RequestBody String secretContent) {
        var secret = new KeyValue(path, secretContent);
        keyValueRepository.save(secret);
        return "File uploaded";
    }

    @GetMapping(path = "/v1/secret/{path}")
    public String downloadFile(@PathVariable String path) {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.debug("userDetails: {}", userDetails);

        return "File Downloaded :: " + path;
    }
}
