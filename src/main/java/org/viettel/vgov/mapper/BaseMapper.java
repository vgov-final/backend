package org.viettel.vgov.mapper;

public interface BaseMapper<E, ReqDto, ResDto> {
    
    E toEntity(ReqDto requestDto);
    
    ResDto toResponseDto(E entity);
}
