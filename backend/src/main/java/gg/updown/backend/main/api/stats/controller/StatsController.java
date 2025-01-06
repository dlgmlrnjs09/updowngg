package gg.updown.backend.main.api.stats.controller;

import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import gg.updown.backend.main.api.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
@Tag(name = "Stats", description = "사이트 통계 관련 API")
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "챔피언별 통계", description = "전체 챔피언별 통계")
    @GetMapping("champions")
    public ResponseEntity<List<ChampionStatsDto>> getChampionList(SortTypeReqDto reqDto) {
        return ResponseEntity.ok(statsService.getChampions(reqDto));
    }
}
