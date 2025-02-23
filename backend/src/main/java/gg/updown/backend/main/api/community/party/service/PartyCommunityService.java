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
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

        if (partyCommunityMapper.checkAlreadyMyParty(post.getWriterPuuid())) {
            throw new SiteCommonException(
                    HttpStatus.CONFLICT,
                    SiteErrorMessage.ALREADY_MY_PARTY.getMessage(),
                    SiteErrorMessage.ALREADY_MY_PARTY.getMessage(),
                    SiteErrorMessage.ALREADY_MY_PARTY.getMessage()
            );
        }

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

    public void apply(String puuid, long postId, String position) {
        // 이미 참가한 포지션이 있는지 확인
        boolean isParticipateAnotherPosition = partyCommunityMapper.checkParticipateAnotherPosition(postId, puuid);
        if (isParticipateAnotherPosition) {
            throw new SiteCommonException(
                    HttpStatus.CONFLICT
                    , SiteErrorMessage.PARTICIPATED_ANOTHER_POSITION.getMessage()
                    , SiteErrorMessage.PARTICIPATED_ANOTHER_POSITION.getMessage()
                    , SiteErrorMessage.PARTICIPATED_ANOTHER_POSITION.getMessage()
            );
        }

        transactionService.applyPartyCommunityPost(postId, position, puuid);
    }

    public void updateApplicantStatus(PartyCommunityApproveReqDto reqDto, String puuid, boolean isApproval) {
        // 파티장 본인인지 확인
        boolean isPartyReader = partyCommunityMapper.checkPartyReader(reqDto.getPostId(), puuid);
        if (!isPartyReader) {
            throw new SiteCommonException(
                    HttpStatus.FORBIDDEN
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
            );
        }

        // 해당 포지션이 마감됐는지 확인
        boolean isAlreadyPosition = partyCommunityMapper.checkAlreadyByPosition(reqDto.getPostId(), reqDto.getPosition());
        if (isAlreadyPosition) {
            throw new SiteCommonException(
                    HttpStatus.CONFLICT
                    , SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage()
                    , SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage()
                    , SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage()
            );
        }

        transactionService.updateApplicantAndParticipant(reqDto.getPostId(), reqDto.getApplicantSeq(), isApproval);
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

    public MyPartyDto getMyActivePartyPost(String puuid) {
        MyPartyDto resDto = new MyPartyDto();

        PartyPostCardDto postDto = partyCommunityMapper.getPartyActivePost(puuid);
        if (postDto == null) {
            // 등록된 모집중인 글이 없음
            return null;
        }

        // 참여자 목록 조회
        List<PartyCommunityParticipantDto> participantDtoList = new ArrayList<>();
        participantDtoList.add(this.createParticipantDto(SiteMatchPosition.TOP, postDto.getTopPuuid(), postDto.getIsOpenTop()));
        participantDtoList.add(this.createParticipantDto(SiteMatchPosition.JUNGLE, postDto.getJunglePuuid(), postDto.getIsOpenJungle()));
        participantDtoList.add(this.createParticipantDto(SiteMatchPosition.MIDDLE, postDto.getMidPuuid(), postDto.getIsOpenMid()));
        participantDtoList.add(this.createParticipantDto(SiteMatchPosition.BOTTOM, postDto.getAdPuuid(), postDto.getIsOpenAd()));
        participantDtoList.add(this.createParticipantDto(SiteMatchPosition.UTILITY, postDto.getSupPuuid(), postDto.getIsOpenSup()));
        postDto.setParticipantDtoList(participantDtoList);

        // 신청자 목록 조회
        List<PartyCommunityApplicantEntity> applicantEntities = partyCommunityMapper.getWaitingApplicantList(postDto.getPostId());
        resDto.setPostCardDto(postDto);

        LinkedHashMap<String, List<PartyCommunityApplicantDetailDto>> innerMap = new LinkedHashMap<>();
        innerMap.put(SiteMatchPosition.TOP.getCode(), this.sortApplicantsByPosition(SiteMatchPosition.TOP, applicantEntities));
        innerMap.put(SiteMatchPosition.JUNGLE.getCode(), this.sortApplicantsByPosition(SiteMatchPosition.JUNGLE, applicantEntities));
        innerMap.put(SiteMatchPosition.MIDDLE.getCode(), this.sortApplicantsByPosition(SiteMatchPosition.MIDDLE, applicantEntities));
        innerMap.put(SiteMatchPosition.BOTTOM.getCode(), this.sortApplicantsByPosition(SiteMatchPosition.BOTTOM, applicantEntities));
        innerMap.put(SiteMatchPosition.UTILITY.getCode(), this.sortApplicantsByPosition(SiteMatchPosition.UTILITY, applicantEntities));
        resDto.setApplicantByPositionMap(innerMap);

        return resDto;
    }

    public void updatePartyStatus(long postId, String puuid, String status) {

        // 파티장 본인인지 확인
        boolean isPartyReader = partyCommunityMapper.checkPartyReader(postId, puuid);
        if (!isPartyReader) {
            throw new SiteCommonException(
                    HttpStatus.FORBIDDEN
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
                    , SiteErrorMessage.NOT_PARTY_READER.getMessage()
            );
        }

        partyCommunityMapper.updatePartyStatus(postId, status);
    }

    public List<PartyCommunityHistoryDto> getPartyHostedHistory(String puuid, int page, int limit) {
        List<PartyCommunityHistoryDto> hostedPartyList = partyCommunityMapper.getPartyHostedHistory(puuid, page, limit);
        this.setParticipantListByPartyList(hostedPartyList);
        return hostedPartyList;
    }

    public int getPartyHostedHistoryCount(String puuid) {
        return partyCommunityMapper.getPartyHostedHistoryCount(puuid);
    }

    public List<PartyCommunityHistoryDto> getPartyParticipatedHistory(String puuid, int page, int limit) {
        List<PartyCommunityHistoryDto> participatedList = partyCommunityMapper.getPartyParticipatedHistory(puuid, page, limit);
        this.setParticipantListByPartyList(participatedList);
        return participatedList;
    }

    public int getPartyParticipatedHistoryCount(String puuid) {
        return partyCommunityMapper.getPartyParticipatedHistoryCount(puuid);
    }

    public List<PartyCommunityAppliedHistoryDto> getPartyAppliedHistory(String puuid, int page, int limit) {
        List<PartyCommunityAppliedHistoryDto> appliedHistoryList = partyCommunityMapper.getPartyAppliedHistory(puuid, page, limit);
        this.setParticipantListByPartyList(appliedHistoryList);
        return appliedHistoryList;
    }

    public int getPartyAppliedHistoryCount(String puuid) {
        return partyCommunityMapper.getPartyAppliedHistoryCount(puuid);
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

    private PartyCommunityParticipantDto createParticipantDtoOnlyBasicInfo(SiteMatchPosition position, String puuid, Boolean isOpen) {
        if (puuid == null) {
            return PartyCommunityParticipantDto.builder()
                    .position(position.getCode())
                    .isOpenPosition(isOpen)
                    .build();
        }

        DuoSummonerInfoDto summonerInfoDto = new DuoSummonerInfoDto();
        SummonerBasicInfoDto summonerDto = lolSummonerService.getSummonerBasicInfoByPuuid(puuid);
        summonerDto.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerDto.getProfileIconId()));
        summonerInfoDto.setSummonerBasicInfoDto(summonerDto);

        return PartyCommunityParticipantDto.builder()
                .isOpenPosition(isOpen)
                .position(position.getCode())
                .summonerInfoDto(summonerInfoDto)
                .build();
    }

    private List<PartyCommunityApplicantDetailDto> sortApplicantsByPosition(
            SiteMatchPosition position,
            List<PartyCommunityApplicantEntity> applicantEntities
    ) {
        return applicantEntities.stream()
                .filter(applicantEntity -> applicantEntity.getPosition().equals(position.getCode()))
                .map(applicantEntity -> {
                    PartyCommunityApplicantDetailDto resultDto = new PartyCommunityApplicantDetailDto();
                    DuoSummonerInfoDto innerBasicInfoDto = new DuoSummonerInfoDto();

                    // 소환사 기본 정보 설정
                    innerBasicInfoDto = this.getSummonerInfo(applicantEntity.getApplicantPuuid());

                    resultDto.setPuuid(applicantEntity.getApplicantPuuid());
                    resultDto.setPosition(applicantEntity.getPosition());
                    resultDto.setPostId(applicantEntity.getPostId());
                    resultDto.setApplicantSeq(applicantEntity.getApplicantSeq());
                    resultDto.setSummonerInfoDto(innerBasicInfoDto);
                    resultDto.setIsApprove(applicantEntity.isApprove());

                    return resultDto;
                })
                .toList();
    }

    private int getRecruitCount(PartyCommunityHistoryBaseDto historyDto) {
        int count = 0;
        count += Boolean.TRUE.equals(historyDto.getIsOpenTop()) ? 1 : 0;
        count += Boolean.TRUE.equals(historyDto.getIsOpenJungle()) ? 1 : 0;
        count += Boolean.TRUE.equals(historyDto.getIsOpenMid()) ? 1 : 0;
        count += Boolean.TRUE.equals(historyDto.getIsOpenAd()) ? 1 : 0;
        count += Boolean.TRUE.equals(historyDto.getIsOpenSup()) ? 1 : 0;

        return count + 1; // 본인포함
    }

    private int getParticipantCount(PartyCommunityHistoryBaseDto historyDto) {
        int count = 0;
        count += historyDto.getTopPuuid() != null ? 1 : 0;
        count += historyDto.getJunglePuuid() != null ? 1 : 0;
        count += historyDto.getMidPuuid() != null ? 1 : 0;
        count += historyDto.getAdPuuid() != null ? 1 : 0;
        count += historyDto.getSupPuuid() != null ? 1 : 0;

        return count;
    }

    private <T extends PartyCommunityHistoryBaseDto> void setParticipantListByPartyList(List<T> partyList) {
        for (T party : partyList) {
            List<PartyCommunityParticipantDto> participantList = new ArrayList<>();

            PartyCommunityParticipantDto topDto = this.createParticipantDtoOnlyBasicInfo(SiteMatchPosition.TOP, party.getTopPuuid(), party.getIsOpenTop());
            PartyCommunityParticipantDto jungleDto = this.createParticipantDtoOnlyBasicInfo(SiteMatchPosition.JUNGLE, party.getJunglePuuid(), party.getIsOpenJungle());
            PartyCommunityParticipantDto midDto = this.createParticipantDtoOnlyBasicInfo(SiteMatchPosition.MIDDLE, party.getMidPuuid(), party.getIsOpenMid());
            PartyCommunityParticipantDto adDto = this.createParticipantDtoOnlyBasicInfo(SiteMatchPosition.BOTTOM, party.getAdPuuid(), party.getIsOpenAd());
            PartyCommunityParticipantDto supDto = this.createParticipantDtoOnlyBasicInfo(SiteMatchPosition.UTILITY, party.getSupPuuid(), party.getIsOpenSup());

            participantList.add(topDto);
            participantList.add(jungleDto);
            participantList.add(midDto);
            participantList.add(adDto);
            participantList.add(supDto);

            party.setRecruitCount(this.getRecruitCount(party));
            party.setParticipantCount(this.getParticipantCount(party));
            party.setParticipantList(participantList);
        }
    }
}
