package com.arpanrec.minerva.api;

import com.arpanrec.minerva.tfstate.StateManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/tfstate/{tfState}", produces = "application/json")
public class TFStateApi {


    private final StateManage stateManage;

    public TFStateApi(@Autowired StateManage stateManage) {
        this.stateManage = stateManage;
    }

    @GetMapping
    public HttpEntity<?> get(@PathVariable String tfState) {
        return this.stateManage.get(tfState);
    }

    @RequestMapping
    public HttpEntity<Map<String, Object>> createOrUpdate(@PathVariable String tfState, @RequestBody Map<String,
        Object> state, @RequestParam(name = "lock_id", required = false) String lockId) {
        return this.stateManage.createOrUpdate(tfState, state, lockId);
    }

}
