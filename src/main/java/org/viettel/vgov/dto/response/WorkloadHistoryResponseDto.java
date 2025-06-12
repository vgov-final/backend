package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadHistoryResponseDto {
    private Long id;
    private Long projectMemberId;
    private BigDecimal oldWorkloadPercentage;
    private BigDecimal newWorkloadPercentage;
    private String reason;
    private String changedBy;
    private LocalDateTime changeTimestamp;
}