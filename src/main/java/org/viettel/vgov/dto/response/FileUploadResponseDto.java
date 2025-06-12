package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {
    private String filename;
    private String originalFilename;
    private String url;
    private Long size;
    private String contentType;
    private String message;
    
    public FileUploadResponseDto(String filename, String originalFilename, String url, Long size, String contentType) {
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.url = url;
        this.size = size;
        this.contentType = contentType;
        this.message = "File uploaded successfully";
    }
}
