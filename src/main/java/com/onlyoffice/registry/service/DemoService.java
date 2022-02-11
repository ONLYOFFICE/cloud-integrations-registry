package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.model.Demo;

public interface DemoService {
    DemoInfoDTO getDemoInfo(String workspaceID);
    Demo createDemo(String workspaceID);
}
