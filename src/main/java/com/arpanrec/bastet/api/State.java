package com.arpanrec.bastet.api;

import com.arpanrec.bastet.state.StateManage;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/state")
public class State {

    private final StateManage stateManage;

    public State(@Autowired StateManage stateManage) {
        this.stateManage = stateManage;
    }

    @PostMapping(path = "/init", produces = "application/json")
    public Map<String, String> get() {
        return stateManage.init();
    }
}
