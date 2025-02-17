package gg.updown.backend.main.api.notice.controller;

import gg.updown.backend.main.api.notice.model.dto.NoticeResDto;
import gg.updown.backend.main.api.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Tag(name = "notice", description = "사이트 공지사항관련 API")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public ResponseEntity<List<NoticeResDto>> getDisplayNoticeList() {
       return ResponseEntity.ok(noticeService.getDisplayNoticeList());
    }
}
