package com.arpanrec.bastet.api;

import com.arpanrec.bastet.tfstate.StateManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/tfstate/{tfState}", produces = "application/json")
public class TFState {


    private final StateManage stateManage;

    public TFState(@Autowired StateManage stateManage) {
        this.stateManage = stateManage;
    }

    @GetMapping
    public HttpEntity<?> get(@PathVariable String tfState) {
        return this.stateManage.get(tfState);
    }

    @PostMapping
    public HttpEntity<Map<String, Object>> createOrUpdate(@PathVariable String tfState, @RequestBody Map<String,
        Object> state, @RequestParam(name = "lock_id", required = false) String lockId) {
        return this.stateManage.createOrUpdate(tfState, state, lockId);
    }

    @PostMapping(path = "/lock")
    public HttpEntity<Map<String, Object>> setLock(@PathVariable String tfState, @RequestBody Map<String,
        Object> lockData) {
        return this.stateManage.setLock(tfState, lockData);
    }

    @DeleteMapping(path = "/lock")
    public HttpEntity<?> deleteLock(@PathVariable String tfState) {
        return this.stateManage.deleteLock(tfState);
    }
}
