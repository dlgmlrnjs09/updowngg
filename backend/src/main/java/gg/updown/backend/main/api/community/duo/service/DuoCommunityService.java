package gg.updown.backend.main.api.community.duo.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.mapper.DuoCommunityMapper;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import gg.updown.backend.main.api.community.duo.model.DuoPostCardDto;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerDto;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerMostChampionDto;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerEntity;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.ranking.service.SiteRankingService;
import gg.updown.backend.main.api.review.service.ReviewService;
import gg.updown.backend.main.enums.SiteLeagueTier;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import gg.updown.backend.main.enums.SiteMatchPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuoCommunityService implements CommunityInterface {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final DuoCommunityMapper duoCommunityMapper;
    private final DuoCommunityTransactionService transactionService;
    private final LolSummonerService lolSummonerService;
    private final ReviewService reviewService;
    private final SiteRankingService siteRankingService;

    @Override
    public List<? extends CommunityPostDto> getPostList(String communityCode, Map<String, String> searchParamMap) {
        DuoCommunitySearchFilter searchFilter = this.convertSearchMapToFilter(searchParamMap);

        List<DuoCommunityEntity> postList = duoCommunityMapper.getDuoPostList(searchFilter);
        List<DuoPostCardDto> resultList = new ArrayList<>();
        for (DuoCommunityEntity post : postList) {
            DuoPostCardDto postCardDto = new DuoPostCardDto();
            DuoCommunityPostDto innerDto = new DuoCommunityPostDto();
            postCardDto.setPostDto(innerDto);
            BeanUtils.copyProperties(post, postCardDto);
            BeanUtils.copyProperties(post, postCardDto.getPostDto());
            SummonerBasicInfoDto summonerDto = lolSummonerService.getSummonerBasicInfoBySiteCode(post.getWriterSiteCode());
            summonerDto.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerDto.getProfileIconId()));
            postCardDto.setSummonerBasicInfoDto(summonerDto);
            postCardDto.setFrequentTagDtoList(reviewService.getFrequentTagCount(summonerDto.getPuuid()));
            postCardDto.setReviewStatsDto(reviewService.getReviewStats(summonerDto.getPuuid()));
            postCardDto.setMostChampionDto(lolSummonerService.getSummonerMostChampions(summonerDto.getPuuid(), 3));
            resultList.add(postCardDto);
        }

        return resultList;
    }

    @Override
    public void insertPost(String communityCode, CommunityPostSubmitReqDto post) {
        DuoCommunityEntity sqlParamEntity = new DuoCommunityEntity();
        BeanUtils.copyProperties(post, sqlParamEntity);
        BeanUtils.copyProperties(post.getDuoCommunityEntity(), sqlParamEntity);
        transactionService.insertDuoCommunityPost(communityCode, sqlParamEntity);
    }

    @Override
    public void updatePost(String communityCode, CommunityPostEntity post) {

    }

    private DuoCommunitySearchFilter convertSearchMapToFilter(Map<String, String> searchParamMap) {
        DuoCommunitySearchFilter searchFilter = new DuoCommunitySearchFilter();

        Optional.ofNullable(searchParamMap.get("tier"))
                .filter(tier -> !tier.isEmpty())
                .ifPresent(tier -> searchFilter.setTier(SiteLeagueTier.valueOf(tier)));

        Optional.ofNullable(searchParamMap.get("positionSelf"))
                .filter(pos -> !pos.isEmpty())
                .ifPresent(pos -> searchFilter.setPositionSelf(SiteMatchPosition.findByCode(pos)));

        Optional.ofNullable(searchParamMap.get("gameMode"))
                .filter(mode -> !mode.isEmpty())
                .ifPresent(mode -> searchFilter.setGameMode(SiteMatchGameMode.valueOf(mode)));

        searchFilter.setOffset(Integer.valueOf(searchParamMap.getOrDefault("offset", "0")));
        searchFilter.setLimit(Integer.valueOf(searchParamMap.getOrDefault("limit", "10")));

        return searchFilter;
    }
}
