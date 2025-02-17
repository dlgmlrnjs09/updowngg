package gg.updown.backend.main.api.notice.service;

import gg.updown.backend.main.api.notice.mapper.NoticeMapper;
import gg.updown.backend.main.api.notice.model.dto.NoticeResDto;
import gg.updown.backend.main.api.notice.model.entity.NoticeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeMapper noticeMapper;

    public List<NoticeResDto> getDisplayNoticeList() {
        List<NoticeResDto> resultList = new ArrayList<>();
        List<NoticeEntity> noticeEntities = noticeMapper.getDisplayNoticeList();
        for (NoticeEntity noticeEntity : noticeEntities) {
            NoticeResDto noticeResDto = new NoticeResDto();
            BeanUtils.copyProperties(noticeEntity, noticeResDto);
            resultList.add(noticeResDto);
        }
        return resultList;
    }
}
