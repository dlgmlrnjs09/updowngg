package gg.updown.backend.main.api.stats.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.main.api.stats.mapper.StatsMapper;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    public List<ChampionStatsDto> getChampions(SortTypeReqDto reqDto) {
        // 기본 통계
        List<ChampionStatsDto> statsList = statsMapper.getStatsByChampions(reqDto);
        // 많이받은 태그 top 3
        Map<String, String> topTags = statsMapper.getTopTagsByChampion(reqDto)
                .stream()
                .collect(Collectors.toMap(
                        map -> map.get("name_us"),
                        map -> map.get("top_tags"),
                        (existing, replacement) -> existing
                ));

        for (ChampionStatsDto champion : statsList) {
            champion.setIconUrl(RiotApiBasePath.DDRAGON.getUrl() + "/cdn/14.24.1/img/champion/" + champion.getNameUs() + ".png");
            champion.setUpRatio();
            champion.setDownRatio();
            champion.setTopTags(topTags.get(champion.getNameUs()));
            champion.setTopTagList();
        }

        return statsList;
    }
}
