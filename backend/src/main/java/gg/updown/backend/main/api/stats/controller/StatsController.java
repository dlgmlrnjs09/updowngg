package gg.updown.backend.main.api.stats.controller;

import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import gg.updown.backend.main.api.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("champions")
    public ResponseEntity<List<ChampionStatsDto>> getChampionList(SortTypeReqDto reqDto) {
        return ResponseEntity.ok(statsService.getChampions(reqDto));
    }
}
