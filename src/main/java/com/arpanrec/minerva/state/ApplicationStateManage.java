package com.arpanrec.minerva.state;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Slf4j
@Component
public class ApplicationStateManage {

    private final ApplicationState applicationStateObj;

    private final String applicationStateKey;

    private final ObjectMapper objectMapper;

    private final KeyValuePersistence keyValuePersistence;

    public ApplicationStateManage(@Autowired KeyValuePersistence keyValuePersistence) {
        this.keyValuePersistence = keyValuePersistence;
        this.objectMapper = new ObjectMapper();
        this.applicationStateKey = keyValuePersistence.getInternalStorageKey() + "/application-state";

        log.info("Application state key: {}", applicationStateKey);

        log.info("Checking if application state exists");
        Optional<KeyValue> applicationState = keyValuePersistence.get(applicationStateKey);
        if (applicationState.isEmpty()) {
            log.info("Application state does not exist. Creating application state");
            applicationStateObj = new ApplicationState();
        } else {
            try {
                log.info("Application state exists. Parsing application state");
                applicationStateObj = objectMapper.readValue(applicationState.get().getValue(), ApplicationState.class);
            } catch (JsonProcessingException e) {
                throw new MinervaException("Unable to parse application state", e);
            }
        }
    }

    private void save() {
        try {
            log.info("Saving application state");
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(applicationStateKey);
            keyValue.setValue(objectMapper.writeValueAsString(applicationStateObj));
            keyValuePersistence.save(keyValue);
            log.info("Application state saved in {}", applicationStateKey);
        } catch (JsonProcessingException e) {
            throw new MinervaException("Unable to save application state", e);
        }
    }

    public boolean isRootUserCreated() {
        return applicationStateObj.rootUserCreated;
    }

    public void setRootUserCreated(boolean rootUserCreated) {
        applicationStateObj.rootUserCreated = rootUserCreated;
        save();
    }

    public String getRootUserName() {
        return applicationStateObj.rootUserName;
    }

    public void setRootUserName(String rootUserName) {
        applicationStateObj.rootUserName = rootUserName;
        save();
    }
}
