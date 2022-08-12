package com.honeypot.domain.search;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SearchHistoryMapper {

    @Mapping(target = "id", source = "searchHistoryId")
    @Mapping(target = "keyword", source = "keyword")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "searcher.id", source = "searcherId")
    SearchHistory toEntity(SearchHistoryDto dto);

    @InheritInverseConfiguration
    SearchHistoryDto toDto(SearchHistory entity);

}
