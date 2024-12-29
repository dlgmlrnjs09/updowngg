package gg.updown.backend.main.api.stats.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import gg.updown.backend.main.api.stats.mapper.StatsMapper;
import gg.updown.backend.main.api.stats.model.dto.ChampionResDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import gg.updown.backend.main.riot.ddragon.mapper.DdragonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DdragonMapper ddragonMapper;
    private final StatsMapper statsMapper;

    public List<ChampionResDto> getChampions(SortTypeReqDto reqDto) {
        List<ChampionResDto> resultList = statsMapper.getPlayCountByChampions(reqDto);

        for (ChampionResDto champion : resultList) {
            champion.setIconUrl(RiotApiBasePath.DDRAGON.getUrl() + "/cdn/14.24.1/img/champion/" + champion.getNameUs() + ".png");
        }

        return resultList;
    }
}
