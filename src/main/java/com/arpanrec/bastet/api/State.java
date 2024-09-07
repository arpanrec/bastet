package com.arpanrec.bastet.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class State {

    @GetMapping(path = "/api/v1/state", produces = "application/json")
    public String get() {
        return "{\"status\": \"ok\"}";
    }
}
