package gg.updown.backend.main.api.community.party.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import gg.updown.backend.main.api.community.duo.model.DuoSummonerInfoDto;
import gg.updown.backend.main.api.community.party.mapper.PartyCommunityMapper;
import gg.updown.backend.main.api.community.party.model.*;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyCommunityService implements CommunityInterface {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final LolSummonerService lolSummonerService;
    private final ReviewService reviewService;

    private final PartyCommunityTransactionService transactionService;
    private final PartyCommunityMapper partyCommunityMapper;

    @Override
    public <T extends CommunityPostDto> List<T> getPostList(String communityCode, Map<String, String> searchParamMap) {
        DuoCommunitySearchFilter searchFilter = this.convertSearchMapToFilter(searchParamMap);
        List<PartyPostCardDto> postList = partyCommunityMapper.getPartyPostList(searchFilter);
        if (postList.isEmpty()) {
            return Collections.emptyList();
        }

        for (PartyPostCardDto postCard : postList) {
            List<PartyCommunityParticipantDto> participantDtoList = new ArrayList<>();
            PartyCommunityParticipantDto topDto = this.createParticipantDto(SiteMatchPosition.TOP, postCard.getTopPuuid(), postCard.getIsOpenTop());
            PartyCommunityParticipantDto jugleDto = this.createParticipantDto(SiteMatchPosition.JUNGLE, postCard.getJunglePuuid(), postCard.getIsOpenJungle());
            PartyCommunityParticipantDto midDto = this.createParticipantDto(SiteMatchPosition.MIDDLE, postCard.getMidPuuid(), postCard.getIsOpenMid());
            PartyCommunityParticipantDto adDto = this.createParticipantDto(SiteMatchPosition.BOTTOM, postCard.getAdPuuid(), postCard.getIsOpenAd());
            PartyCommunityParticipantDto supDto = this.createParticipantDto(SiteMatchPosition.UTILITY, postCard.getSupPuuid(), postCard.getIsOpenSup());

            participantDtoList.add(topDto);
            participantDtoList.add(jugleDto);
            participantDtoList.add(midDto);
            participantDtoList.add(adDto);
            participantDtoList.add(supDto);


            postCard.setParticipantDtoList(participantDtoList);
        }

            return (List<T>) postList;
    }

    @Override
    public void insertPost(String communityCode, CommunityPostSubmitReqDto post) {
        PartyCommunityEntity partyCommunityEntity = new PartyCommunityEntity();
        PartyCommunityParticipantEntity participantEntity = new PartyCommunityParticipantEntity();
        BeanUtils.copyProperties(post, partyCommunityEntity);
        BeanUtils.copyProperties(post.getPartyCommunityDto(), partyCommunityEntity);

        SiteMatchPosition position = SiteMatchPosition.findByCode(post.getPartyCommunityDto().getPositionSelf());
        switch(position) {
            case TOP:
                participantEntity.setTopPuuid(post.getWriterPuuid());
                break;
            case JUNGLE:
                participantEntity.setJunglePuuid(post.getWriterPuuid());
                break;
            case MIDDLE:
                participantEntity.setMidPuuid(post.getWriterPuuid());
                break;
            case BOTTOM:
                participantEntity.setAdPuuid(post.getWriterPuuid());
                break;
            case UTILITY:
                participantEntity.setSupPuuid(post.getWriterPuuid());
                break;
        }

        transactionService.insertPartyCommunityPost(communityCode, partyCommunityEntity, participantEntity);
    }

    @Override
    public void updatePost(String communityCode, CommunityPostEntity post) {

    }

    public boolean apply(String puuid, long postId, String position) {
        return transactionService.applyPartyCommunityPost(postId, position, puuid);
    }

    public List<PartyCommunityApplicantDto> getApplicantList(String puuid, List<Long> postIds) {
        List<PartyCommunityApplicantDto> resDtoList = new ArrayList<>();
        partyCommunityMapper.getApplicantList(puuid, postIds).forEach(dto -> {
            PartyCommunityApplicantDto p = new PartyCommunityApplicantDto();
            BeanUtils.copyProperties(dto, p);
            resDtoList.add(p);
        });

        return resDtoList;
    }




    private DuoSummonerInfoDto getSummonerInfo(String puuid) {
        SummonerBasicInfoDto summonerDto = lolSummonerService.getSummonerBasicInfoByPuuid(puuid);
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

    private PartyCommunityParticipantDto createParticipantDto(SiteMatchPosition position, String puuid, Boolean isOpen) {
        if (puuid == null) {
            return PartyCommunityParticipantDto.builder()
                    .position(position.getCode())
                    .isOpenPosition(isOpen)
                    .build();
        }

        return PartyCommunityParticipantDto.builder()
                .position(position.getCode())
                .isOpenPosition(isOpen)
                .summonerInfoDto(this.getSummonerInfo(puuid))
                .build();
    }
}
