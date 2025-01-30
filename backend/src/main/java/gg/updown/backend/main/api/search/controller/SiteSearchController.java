package gg.updown.backend.main.api.search.controller;

import gg.updown.backend.main.api.search.model.SiteSearchSummonerDto;
import gg.updown.backend.main.api.search.service.SiteSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "사이트 내 검색관련 API")
public class SiteSearchController {

    private final SiteSearchService siteSearchService;

    @GetMapping("/summoner")
    public List<SiteSearchSummonerDto> searchSummoner(String keyword) {
        return siteSearchService.searchSummoners(keyword);
    }
}
