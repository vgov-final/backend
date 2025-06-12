package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.request.ProjectMemberRequestDto;
import org.viettel.vgov.dto.request.ProjectRequestDto;
import org.viettel.vgov.dto.response.PagedResponse;
import org.viettel.vgov.dto.response.ProjectResponseDto;
import org.viettel.vgov.dto.response.UserProjectHistoryResponseDto;
import org.viettel.vgov.exception.ResourceNotFoundException;
import org.viettel.vgov.mapper.ProjectMapper;
import org.viettel.vgov.model.Project;
import org.viettel.vgov.model.ProjectMember;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.ProjectMemberRepository;
import org.viettel.vgov.repository.ProjectRepository;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.security.UserPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberService projectMemberService;
    private final ProjectMemberRepository projectMemberRepository;

    public PagedResponse<ProjectResponseDto> getAllProjects(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Project> projects;

        switch (currentUser.getRole()) {
            case admin:
                // Admin can see all projects
                projects = projectRepository.findAll(pageable);
                break;
            case pm:
                // PM can only see projects they manage
                projects = projectRepository.findByPmEmail(currentUser.getEmail(), pageable);
                break;
            case dev:
            case ba:
            case test:
                // Dev/BA/Test can only see assigned projects
                projects = projectRepository.findProjectsByUserId(currentUser.getId(), pageable);
                break;
            default:
                throw new AccessDeniedException("Access denied");
        }

        Page<ProjectResponseDto> projectDtos = projects.map(projectMapper::toResponseDto);
        return PagedResponse.of(projectDtos);
    }

    public PagedResponse<ProjectResponseDto> getAllProjects(Pageable pageable, String search, String projectStatus, String projectType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Convert string parameters to enums
        Project.Status statusEnum = null;
        if (projectStatus != null && !projectStatus.isEmpty()) {
            try {
                statusEnum = Project.Status.valueOf(projectStatus);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep as null
            }
        }

        Project.ProjectType typeEnum = null;
        if (projectType != null && !projectType.isEmpty()) {
            try {
                typeEnum = Project.ProjectType.valueOf(projectType);
            } catch (IllegalArgumentException e) {
                // Invalid type, keep as null
            }
        }

        Page<Project> projects;

        switch (currentUser.getRole()) {
            case admin:
                // Admin can see all projects with filters
                projects = projectRepository.findProjectsWithFilters(search, statusEnum, typeEnum, pageable);
                break;
            case pm:
                // PM can only see projects they manage with filters
                projects = projectRepository.findProjectsWithFiltersForPM(currentUser.getEmail(), search, statusEnum, typeEnum, pageable);
                break;
            case dev:
            case ba:
            case test:
                // Dev/BA/Test can only see assigned projects with filters
                projects = projectRepository.findProjectsWithFiltersForUser(currentUser.getId(), search, statusEnum, typeEnum, pageable);
                break;
            default:
                throw new AccessDeniedException("Access denied");
        }

        Page<ProjectResponseDto> projectDtos = projects.map(projectMapper::toResponseDto);
        return PagedResponse.of(projectDtos);
    }

    public List<ProjectResponseDto> getAllProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Project> projects;

        switch (currentUser.getRole()) {
            case admin:
                // Admin can see all projects
                projects = projectRepository.findAll();
                break;
            case pm:
                // PM can only see projects they manage
                projects = projectRepository.findByPmEmail(currentUser.getEmail());
                break;
            case dev:
            case ba:
            case test:
                // Dev/BA/Test can only see assigned projects
                projects = projectRepository.findProjectsByUserId(currentUser.getId());
                break;
            default:
                throw new AccessDeniedException("Access denied");
        }

        return projects.stream()
                .map(projectMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProjectResponseDto getProjectById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // Check access permission
        boolean hasAccess = false;
        switch (currentUser.getRole()) {
            case admin:
                hasAccess = true;
                break;
            case pm:
                hasAccess = project.getPmEmail().equals(currentUser.getEmail());
                break;
            case dev:
            case ba:
            case test:
                hasAccess = projectRepository.findProjectsByUserId(currentUser.getId())
                        .stream()
                        .anyMatch(p -> p.getId().equals(id));
                break;
        }

        if (!hasAccess) {
            throw new AccessDeniedException("Access denied to this project");
        }

        return projectMapper.toResponseDto(project);
    }

    public ProjectResponseDto createProject(ProjectRequestDto requestDto) {
        if (projectRepository.existsByProjectCode(requestDto.getProjectCode())) {
            throw new IllegalArgumentException("Project code already exists: " + requestDto.getProjectCode());
        }

        // Validate dates
        if (requestDto.getEndDate() != null && requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = projectMapper.toEntity(requestDto);
        project.setCreatedBy(currentUser);
        project.setUpdatedBy(currentUser);

        Project savedProject = projectRepository.save(project);

        // Automatically add the PM to the project with 0% workload
        if (savedProject.getPmEmail() != null && !savedProject.getPmEmail().isEmpty()) {
            userRepository.findByEmail(savedProject.getPmEmail()).ifPresent(pmUser -> {
                if (pmUser.getRole() == User.Role.pm) {
                    ProjectMemberRequestDto memberRequestDto = new ProjectMemberRequestDto();
                    memberRequestDto.setUserId(pmUser.getId());
                    memberRequestDto.setWorkloadPercentage(BigDecimal.ZERO);
                    projectMemberService.addMemberToProject(savedProject.getId(), memberRequestDto);
                }
            });
        }

        return projectMapper.toResponseDto(savedProject);
    }

    public ProjectResponseDto updateProject(Long id, ProjectRequestDto requestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (project.getStatus() == Project.Status.Closed) {
            throw new IllegalStateException("Cannot update a closed project.");
        }

        // Check if project code is being changed and if it already exists
        if (!project.getProjectCode().equals(requestDto.getProjectCode()) &&
            projectRepository.existsByProjectCode(requestDto.getProjectCode())) {
            throw new IllegalArgumentException("Project code already exists: " + requestDto.getProjectCode());
        }

        // Validate dates
        if (requestDto.getEndDate() != null && requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update project fields
        project.setProjectCode(requestDto.getProjectCode());
        project.setProjectName(requestDto.getProjectName());
        project.setPmEmail(requestDto.getPmEmail());
        project.setStartDate(requestDto.getStartDate());
        project.setEndDate(requestDto.getEndDate());
        project.setProjectType(requestDto.getProjectType());
        // Check if status is changing to Closed to set the actualClosedDate
        if (requestDto.getStatus() == Project.Status.Closed && project.getStatus() != Project.Status.Closed) {
            project.setActualClosedDate(LocalDate.now());
        }
        project.setStatus(requestDto.getStatus());
        project.setDescription(requestDto.getDescription());
        project.setUpdatedBy(currentUser);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        projectRepository.delete(project);
    }

    public ProjectResponseDto updateProjectStatus(Long id, Project.Status status) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (project.getStatus() == Project.Status.Closed) {
            throw new IllegalStateException("Cannot change the status of a closed project.");
        }

        // Set the actual closed date if the status is changing to "Closed"
        if (status == Project.Status.Closed) {
            project.setActualClosedDate(LocalDate.now());
        } else {
            // If for some reason the project is reopened, clear the date
            project.setActualClosedDate(null);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        project.setStatus(status);
        project.setUpdatedBy(currentUser);

        Project savedProject = projectRepository.save(project);

        // TODO: Send notification to all project members about status change

        return projectMapper.toResponseDto(savedProject);
    }

    public List<UserProjectHistoryResponseDto> getUserProjectHistory(Long userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUserIdWithProject(userId);

        return projectMembers.stream()
                .map(pm -> new UserProjectHistoryResponseDto(
                        pm.getProject().getId(),
                        pm.getProject().getProjectName(),
                        pm.getWorkloadPercentage(),
                        pm.getJoinedDate(),
                        pm.getLeftDate(),
                        pm.getProject().getStatus()
                ))
                .sorted(Comparator.comparing(dto -> dto.getProjectStatus() == Project.Status.Closed))
                .collect(Collectors.toList());
    }
}
