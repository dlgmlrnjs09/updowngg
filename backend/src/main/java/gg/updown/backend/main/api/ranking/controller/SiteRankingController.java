package gg.updown.backend.main.api.ranking.controller;

import gg.updown.backend.main.api.ranking.model.SiteRankingCardReqDto;
import gg.updown.backend.main.api.ranking.model.SiteRankingCardResDto;
import gg.updown.backend.main.api.ranking.service.SiteRankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class SiteRankingController {
    private final SiteRankingService service;

    @GetMapping("/list")
    public ResponseEntity<List<SiteRankingCardResDto>> getSiteRanking(@Valid SiteRankingCardReqDto reqDto) {
        List<SiteRankingCardResDto> resultList = service.selectTopRankers(reqDto.getOffset(), reqDto.getLimit());
        return ResponseEntity.ok(resultList);
    }
}
