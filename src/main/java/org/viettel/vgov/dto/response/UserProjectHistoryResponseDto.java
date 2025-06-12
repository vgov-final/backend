package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.viettel.vgov.model.Project;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectHistoryResponseDto {
    private Long projectId;
    private String projectName;
    private BigDecimal workloadPercentage;
    private LocalDate joinedDate;
    private LocalDate leftDate;
    private Project.Status projectStatus;
}