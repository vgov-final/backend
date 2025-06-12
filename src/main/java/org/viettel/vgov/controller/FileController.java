package org.viettel.vgov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.viettel.vgov.dto.response.FileUploadResponseDto;
import org.viettel.vgov.service.FileService;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "File upload, download and management via MinIO")
@SecurityRequirement(name = "bearerAuth")
public class FileController {
    
    private final FileService fileService;
    
    @Operation(summary = "Upload file", description = "Upload file to MinIO storage")
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadResponseDto response = fileService.uploadFile(file);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Download file", description = "Download file by filename")
    @GetMapping("/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource resource = fileService.getFile(filename);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
    
    @Operation(summary = "Delete file", description = "Delete file from MinIO storage")
    @DeleteMapping("/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String filename) {
        boolean deleted = fileService.deleteFile(filename);
        
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get file URL", description = "Get public URL for file access")
    @GetMapping("/url/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getFileUrl(@PathVariable String filename) {
        String url = fileService.getFileUrl(filename);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
