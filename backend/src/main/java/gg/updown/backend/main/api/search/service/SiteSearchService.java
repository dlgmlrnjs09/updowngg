package gg.updown.backend.main.api.search.service;

import gg.updown.backend.main.api.search.mapper.SiteSearchMapper;
import gg.updown.backend.main.api.search.model.SiteSearchSummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteSearchService {

    private final SiteSearchMapper siteSearchMapper;

    public List<SiteSearchSummonerDto> searchSummoners(String keyword) {
        return siteSearchMapper.searchSummoners(keyword);
    }
}
