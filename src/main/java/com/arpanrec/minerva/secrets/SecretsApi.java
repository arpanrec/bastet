package com.arpanrec.minerva.secrets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SecretsApi {

    private final SecretRepository secretRepository;

    public SecretsApi(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    @PostMapping(path = "/v1/secret/{path}")
    public String uploadFile(@PathVariable String path, @RequestBody String secretContent) {
        var secret = new Secret(path, secretContent);
        secretRepository.save(secret);
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
