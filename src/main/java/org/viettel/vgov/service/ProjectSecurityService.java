package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.ProjectMemberRepository;
import org.viettel.vgov.repository.ProjectRepository;
import org.viettel.vgov.repository.UserRepository;

@Service("projectSecurityService")
@RequiredArgsConstructor
public class ProjectSecurityService {
    
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    
    public boolean canAccessProject(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return false;
        }
        
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return false;
        }
        
        // Check access based on role
        switch (user.getRole()) {
            case admin:
                return true;
            case pm:
                return project.getPmEmail().equals(user.getEmail());
            case dev:
            case ba:
            case test:
                return projectMemberRepository.existsByProjectIdAndUserIdAndIsActive(projectId, user.getId(), true);
            default:
                return false;
        }
    }
    
    public boolean canManageProject(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return false;
        }
        
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return false;
        }
        
        // Only admin can manage projects
        return user.getRole() == User.Role.admin;
    }
    
    public boolean canManageProjectMembers(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Only admin can manage project members
        return user.getRole() == User.Role.admin;
    }
}
