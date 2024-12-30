package gg.updown.backend.main.api.stats.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.stats.mapper.StatsMapper;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsTagDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final StatsMapper statsMapper;

    public List<ChampionStatsDto> getChampions(SortTypeReqDto reqDto) {
        // 기본 통계
        List<ChampionStatsDto> statsList = statsMapper.getStatsByChampions(reqDto);
        // 많이받은 태그 top 3
        List<ChampionStatsTagDto> topTags = statsMapper.getTopTagsByChampion(reqDto);

        // topTags를 nameUs를 키로 하는 Map으로 변환
        Map<String, List<ReviewTagDto>> topTagsMap = topTags.stream()
                .collect(Collectors.toMap(
                        ChampionStatsTagDto::getNameUs,
                        ChampionStatsTagDto::getTagDtoList
                ));

        // 각 챔피언에 태그 리스트 매핑
        for (ChampionStatsDto champion : statsList) {
            champion.setIconUrl(RiotApiBasePath.DDRAGON.getUrl() + "/cdn/" + latestVersion + "/img/champion/" + champion.getNameUs() + ".png");
            champion.setUpRatio();
            champion.setDownRatio();

            // nameUs 기준으로 topTags를 매핑
            List<ReviewTagDto> topTagList = topTagsMap.getOrDefault(champion.getNameUs(), Collections.emptyList());
            champion.setTopTagList(topTagList);
        }


        return statsList;
    }
}
