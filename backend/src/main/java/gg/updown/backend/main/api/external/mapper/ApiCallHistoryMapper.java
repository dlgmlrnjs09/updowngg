package gg.updown.backend.main.api.external.mapper;

import gg.updown.backend.main.api.external.model.ApiCallHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiCallHistoryMapper {
    void insertApiCallHistory(ApiCallHistoryEntity entity);
}