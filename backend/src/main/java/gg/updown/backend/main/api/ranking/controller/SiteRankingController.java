package gg.updown.backend.main.api.ranking.controller;

import gg.updown.backend.main.api.ranking.model.SiteRankingCardReqDto;
import gg.updown.backend.main.api.ranking.model.SiteRankingCardResDto;
import gg.updown.backend.main.api.ranking.model.SiteRankingReqDto;
import gg.updown.backend.main.api.ranking.model.SiteRankingResDto;
import gg.updown.backend.main.api.ranking.service.SiteRankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Rank", description = "사이트 랭킹관련 API")
@RequestMapping("/api/v1/rank")
public class SiteRankingController {
    private final SiteRankingService service;

    @Operation(summary = "랭킹목록 조회", description = "사이트 랭킹목록 조회")
    @GetMapping("/list")
    public ResponseEntity<List<SiteRankingResDto>> getSiteRanking(@Valid SiteRankingReqDto reqDto) {
        List<SiteRankingResDto> resultList = service.selectTopRankers(reqDto);
        return ResponseEntity.ok(resultList);
    }
}
