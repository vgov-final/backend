package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmInfoResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Integer activeProjectCount;
    private BigDecimal totalWorkload;
}
