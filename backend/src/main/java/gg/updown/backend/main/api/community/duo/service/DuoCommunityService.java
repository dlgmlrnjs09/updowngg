package gg.updown.backend.main.api.community.duo.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.mapper.DuoCommunityMapper;
import gg.updown.backend.main.api.community.duo.model.*;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.ranking.service.SiteRankingService;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.service.ReviewService;
import gg.updown.backend.main.enums.SiteLeagueTier;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import gg.updown.backend.main.enums.SiteMatchPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public <T extends CommunityPostDto> List<T> getPostList(String communityCode, Map<String, String> searchParamMap) {
        DuoCommunitySearchFilter searchFilter = this.convertSearchMapToFilter(searchParamMap);
        List<DuoCommunityEntity> postList = duoCommunityMapper.getDuoPostList(searchFilter);

        if (postList.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 작성자 sitecode 목록 추출
        Set<Long> writerSiteCodes = postList.stream()
                .map(DuoCommunityEntity::getWriterSiteCode)
                .collect(Collectors.toSet());

        // 2. 작성자 정보 조회하여 Map에 저장
        Map<Long, DuoSummonerInfoDto> summonerInfoMap = writerSiteCodes.stream()
                .collect(Collectors.toMap(
                        siteCode -> siteCode,
                        this::getSummonerInfo,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        // 3. 게시글 + 작성자 매핑
        return postList.stream()
                .map(post -> (T) createPostCardDto(post, summonerInfoMap.get(post.getWriterSiteCode())))
                .collect(Collectors.toList());
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

        searchFilter.setOffset(Integer.parseInt(searchParamMap.getOrDefault("offset", "0")));
        searchFilter.setLimit(Integer.parseInt(searchParamMap.getOrDefault("limit", "15")));

        return searchFilter;
    }

    private DuoSummonerInfoDto getSummonerInfo(Long siteCode) {
        SummonerBasicInfoDto summonerDto = lolSummonerService.getSummonerBasicInfoBySiteCode(siteCode);
        summonerDto.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerDto.getProfileIconId()));

        DuoSummonerInfoDto summonerInfoDto = new DuoSummonerInfoDto();
        summonerInfoDto.setSummonerBasicInfoDto(summonerDto);
        summonerInfoDto.setFrequentTagDtoList(reviewService.getFrequentTagCount(summonerDto.getPuuid(), 3));
        summonerInfoDto.setReviewStatsDto(reviewService.getReviewStats(summonerDto.getPuuid()));
        summonerInfoDto.setMostChampionDto(lolSummonerService.getSummonerMostChampions(summonerDto.getPuuid(), 3));

        if (summonerInfoDto.getReviewStatsDto() == null) {
            summonerInfoDto.setReviewStatsDto(new ReviewStatsDto());
        }

        return summonerInfoDto;
    }

    private DuoPostCardDto createPostCardDto(DuoCommunityEntity post, DuoSummonerInfoDto summonerInfo) {
        DuoPostCardDto postCardDto = new DuoPostCardDto();
        DuoCommunityPostDto innerDto = new DuoCommunityPostDto();
        postCardDto.setPostDto(innerDto);

        BeanUtils.copyProperties(post, postCardDto);
        BeanUtils.copyProperties(post, postCardDto.getPostDto());
        postCardDto.setDuoSummonerInfoDto(summonerInfo);

        return postCardDto;
    }
}
