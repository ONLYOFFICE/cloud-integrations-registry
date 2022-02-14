package com.onlyoffice.registry.interceptor;

import com.onlyoffice.registry.model.Workspace;
import com.onlyoffice.registry.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class WorkspaceTypeInterceptor implements HandlerInterceptor {
    private WorkspaceService workspaceService;
    @Autowired
    public WorkspaceTypeInterceptor(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Boolean shouldFilter = pathVariables != null && pathVariables.containsKey("workspaceTypeName")
                && pathVariables.containsKey("workspaceID");
        if (shouldFilter) {
            Workspace workspace = this.workspaceService.getWorkspace(pathVariables.get("workspaceID"));
            if (!workspace.getType().getName().equals(pathVariables.get("workspaceTypeName")))
                throw new RuntimeException("Workspace with this id does not belong to this workspace type");
        }
        return true;
    }
}
