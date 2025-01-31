package gg.updown.backend.main.api.search.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.search.mapper.SiteSearchMapper;
import gg.updown.backend.main.api.search.model.SiteSearchSummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteSearchService {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final SiteSearchMapper siteSearchMapper;

    public List<SiteSearchSummonerDto> searchSummoners(String keyword) {
        List<SiteSearchSummonerDto> resultList = siteSearchMapper.searchSummoners(keyword);
        resultList.forEach(r -> {
            r.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, r.getProfileIconId()));
        });
        return resultList;
    }
}
