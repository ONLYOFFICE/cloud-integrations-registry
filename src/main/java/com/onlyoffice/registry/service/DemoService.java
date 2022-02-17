package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.model.Demo;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;

public interface DemoService {
    DemoInfoDTO getDemoInfo(WorkspaceID id);
    Demo createDemo(WorkspaceID id);
}
