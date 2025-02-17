package gg.updown.backend.main.api.notice.mapper;

import gg.updown.backend.main.api.notice.model.entity.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    List<NoticeEntity> getDisplayNoticeList();
}
