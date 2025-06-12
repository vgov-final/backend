package org.viettel.vgov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.viettel.vgov.dto.response.WorkloadHistoryResponseDto;
import org.viettel.vgov.model.WorkloadHistory;

@Mapper(componentModel = "spring")
public interface WorkloadHistoryMapper {

    WorkloadHistoryMapper INSTANCE = Mappers.getMapper(WorkloadHistoryMapper.class);

    @Mapping(source = "projectMember.id", target = "projectMemberId")
    @Mapping(source = "changedBy.fullName", target = "changedBy")
    WorkloadHistoryResponseDto toResponseDto(WorkloadHistory workloadHistory);
}