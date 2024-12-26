package gg.updown.backend.main.api.stats.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import gg.updown.backend.main.api.stats.model.dto.ChampionResDto;
import gg.updown.backend.main.riot.ddragon.mapper.DdragonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DdragonMapper ddragonMapper;

    public List<ChampionResDto> getChampions() {
        List<ChampionResDto> resultList = new ArrayList<>();

        List<Champion> championList = ddragonMapper.getAllChampions();
        for (Champion champion : championList) {
            resultList.add(ChampionResDto.builder()
                            .nameUs(champion.getNameUs())
                            .nameKr(champion.getNameKr())
                            .iconUrl(RiotApiBasePath.DDRAGON.getUrl() + "/cdn/14.24.1/img/champion/" + champion.getNameUs() + ".png")
                    .build());
        }

        return resultList;
    }
}
