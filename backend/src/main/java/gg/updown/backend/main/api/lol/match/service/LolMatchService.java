package gg.updown.backend.main.api.lol.match.service;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.external.riot.api.lol.match.model.GetMatchIdListReqDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.service.MatchApiService;
import gg.updown.backend.external.riot.api.lol.spectator.model.CurrentMatchInfoDto;
import gg.updown.backend.external.riot.api.lol.spectator.model.CurrentMatchParticipantDto;
import gg.updown.backend.external.riot.api.lol.spectator.service.SpectatorService;
import gg.updown.backend.external.riot.enums.MatchGameMode;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.lol.match.mapper.LolMatchMapper;
import gg.updown.backend.main.api.lol.match.model.dto.*;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchParticipantEntity;
import gg.updown.backend.main.api.lol.summoner.model.LolMatchModelConverter;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerProfileResDto;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerLeagueEntity;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.ranking.mapper.SiteRankingMapper;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.review.model.dto.ReviewDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.service.ReviewService;
import gg.updown.backend.main.enums.SiteLeagueTier;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LolMatchService {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final MatchApiService matchApiService;
    private final SpectatorService spectatorService;
    private final LolMatchMapper matchMapper;
    private final LolMatchTransactionService transactionService;
    private final LolSummonerService lolSummonerService;
    private final LolMatchModelConverter lolMatchModelConverter;
    private final ReviewService reviewService;
    private final LolMatchMapper lolMatchMapper;
    private final SiteRankingMapper siteRankingMapper;

    /**
     * 기간 내 저장되지 않은 match 전부 불러와 matchId, 갱신정보만 저장하기
     * 갱신버튼 클릭 시
     * @param puuid
     * @param startTime
     * @param endTime
     * @return
     */
    public List<String> getAndInsertMatchIdList(String puuid, Long startTime, Long endTime) {
        String latestMatchId = matchMapper.getLatestRequestMatchId(puuid);
        List<String> newMatchIdList = this.getNewMatchIdList(puuid, latestMatchId, startTime, endTime);
        if (!newMatchIdList.isEmpty()) {
            transactionService.saveMatchWithUpdateRequests(puuid, newMatchIdList);
        }

        return newMatchIdList;
    }

    /**
     * 갱신으로 인해 새로불러온 matchId 조회 후 상세정보 및 리뷰 불러오기(DB or API)
     * 초기 진입시, 더보기버튼 클릭시
     * @param puuid
     * @param startIndex
     * @param count
     * @return
     */
    public List<LolMatchInfoResDto> getAndInsertMatchList(String puuid, int startIndex, int count, UserDetailImpl userDetails) {
        List<LolMatchInfoResDto> resultList = new ArrayList<>();

        // 갱신을 통해 불러온 matchId 목록 조회
        List<String> requestedMatchList = matchMapper.selectMatchRequestList(puuid, startIndex, count);
        for (String matchId : requestedMatchList) {
            // 이미 DB에 존재하면 DB에서 get, 없으면 API에서 get 및 DB 저장
            // 1. 경기정보 저장
            LolMatchInfoResDto resDto = this.getMatchResDtoAndInsertConditional(matchId);

            // 2. 프로필 대상자의 해당 경기 받은 리뷰 작성자, 태그정보 조회
            resDto.setReviewByMatchSummaryDto(reviewService.getReviewerAndTagsByMatch(puuid, matchId));

            if (userDetails == null) {
                // 3. 로그인하지 않은 경우 모든 플레이어를 리뷰 불가능으로 처리
                resDto.getParticipantList().forEach(player -> {
                    player.setReviewDto(new ReviewDto());
                    player.getReviewDto().setReviewable(false);
                    player.setReviewStatsDto(reviewService.getReviewAvgRating(player.getPuuid()));
                });
            } else {
                // 3. 로그인한 경우 작성한 리뷰 목록 조회
                String loginUserPuuid = userDetails.getPuuid();

                // 로그인한 사용자 작성한 리뷰
                List<ReviewDto> wroteReviewList = reviewService.getWroteReviewList(loginUserPuuid);
                Map<String, ReviewDto> reviewMap = wroteReviewList.stream()
                        .collect(Collectors.toMap(ReviewDto::getTargetPuuid, review -> review));

                // 4. 리뷰 정보 설정
                for (LolMatchParticipantDto dto : resDto.getParticipantList()) {
                    // 자기 자신은 리뷰 불가능
                    if (loginUserPuuid.equals(dto.getPuuid())) {
                        dto.setReviewDto(new ReviewDto());
                        dto.getReviewDto().setReviewable(false);
                        continue;
                    }

                    // 이미 리뷰한 플레이어 처리
                    ReviewDto existingReview = reviewMap.get(dto.getPuuid());
                    if (existingReview != null) {
                        dto.setReviewDto(existingReview);
                        dto.getReviewDto().setReviewable(false);
                    } else {
                        // 리뷰하지 않은 플레이어는 리뷰 가능으로 설정
                        dto.setReviewDto(new ReviewDto());
                        dto.getReviewDto().setReviewable(true);
                    }

                    // 리뷰평점 객체 Set
                    dto.setReviewStatsDto(reviewService.getReviewAvgRating(dto.getPuuid()));
                }
            }

            resultList.add(resDto);
        }

        return resultList;
    }

    /**
     * matchId로 매치 상세 정보 및 리뷰 조회
     * @param matchId
     * @return
     */
    public LolMatchInfoResDto getMatchResDto(String matchId, String puuid) {
        LolMatchInfoResDto resDto = this.getMatchResDtoAndInsertConditional(matchId);

        resDto.getParticipantList().forEach(player -> {
            player.setReviewDto(new ReviewDto());
            player.setReviewStatsDto(reviewService.getReviewAvgRating(player.getPuuid()));
            player.setReviewDto(reviewService.getWrittenToTarget(puuid, player.getPuuid()));
        });

        return resDto;
    }


    /**
     * 이미 이미 DB에 존재하면 DB에서 get, 없으면 API에서 get 및 DB 저장
     * @param matchId
     * @return
     */
    public LolMatchInfoResDto getMatchResDtoAndInsertConditional(String matchId) {
        LolMatchEntity matchInfoEntity = null;
        List<LolMatchParticipantEntity> participantList = null;
        // 이미 DB에 존재하면 DB에서 get, 없으면 API에서 get 및 DB 저장
        if (matchMapper.countingMatchByMatchId(matchId) > 0) {
            matchInfoEntity = matchMapper.getMatchInfo(matchId);
            participantList = matchMapper.getMatchParticipantList(matchId);
        } else {
            MatchDto matchDto = matchApiService.getMatchDetailByMatchId(matchId);
            matchInfoEntity = lolMatchModelConverter.convertMatchDtoToLolMatchEntity(matchDto);
            participantList = lolMatchModelConverter.convertMatchDtoToLolMatchParticipantList(matchDto);
            transactionService.saveMatchWithParticipants(matchInfoEntity, participantList);
        }

        // 게임 참여자 10명 계정정보 없으면 Insert
        // 굳이 안해도 될듯.. 어차피 경기 목록에는 계정정보 안보여지니까
//        for (LolMatchParticipantEntity participantEntity : participantList) {
//            lolSummonerService.conflictSummonerInfo(participantEntity.getPuuid(), participantEntity.getRiotIdGameName(), participantEntity.getRiotIdTagline());
//        }

        // 게임 참여자 10명 랭크정보 Insert
        // API 호출횟수 많아서 제거함
//        for (LolMatchParticipantEntity participantEntity : participantList) {
//            lolSummonerService.getAndConflictSummonerLeagueInfo(participantEntity.getSummonerId());
//        }

        return this.makeMatchInfoResDto(matchInfoEntity, participantList);
    }

    public LolMatchInfoResDto getMatchInfoAndParticipantList(String matchId) {
        LolMatchEntity matchInfoEntity = this.getMatchInfo(matchId);
        List<LolMatchParticipantEntity> participantList = this.getMatchParticipant(matchId);

        return this.makeMatchInfoResDto(matchInfoEntity, participantList);
    }

    public LolMatchEntity getMatchInfo(String matchId) {
        return matchMapper.getMatchInfo(matchId);
    }

    public List<LolMatchParticipantEntity> getMatchParticipant(String matchId) {
        return matchMapper.getMatchParticipantList(matchId);
    }

    public boolean hasPlayedTogether(String loginPuuid, LolMatchTogetherReqDto reqDto) {
        LocalDateTime startDate = LocalDateTime.parse(
                reqDto.getStartDate() + "000000",
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );

        LocalDateTime endDate = LocalDateTime.parse(
                reqDto.getEndDate() + "235959",
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );

        return matchMapper.existsMatchedTogether(loginPuuid, reqDto.getTargetPuuid(), startDate, endDate);
    }

    public LolMatchInfoResDto getLatestMatchInfoTogether(String loginPuuid, LolMatchTogetherReqDto reqDto) {
        LocalDateTime startDate = LocalDateTime.parse(
                reqDto.getStartDate() + "000000",
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );

        LocalDateTime endDate = LocalDateTime.parse(
                reqDto.getEndDate() + "235959",
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );

        String matchId = lolMatchMapper.getLatestMatchIdTogether(loginPuuid, reqDto.getTargetPuuid(), startDate, endDate);
        return this.getMatchResDto(matchId, loginPuuid);
    }

    public LolCurrentMatchInfoDto getCurrentMatchInfo(String puuid) {
        LolCurrentMatchInfoDto currentMatchInfoDto = new LolCurrentMatchInfoDto();
        List<LolCurrentMatchParticipantDto> participantDtoList = new ArrayList<>();
        CurrentMatchInfoDto currentMatchInfo = spectatorService.getCurrentMatchInfoByPuuid(puuid);
        if (currentMatchInfo == null) {
            return currentMatchInfoDto;
        }

        currentMatchInfoDto.setMatchInfoDto(LolMatchInfoDto.builder()
                        .gameDuration(currentMatchInfo.getGameLength())
                        .gameMode(currentMatchInfo.getGameMode())
                        .gameModeName(MatchGameMode.getQueueName((int) currentMatchInfo.getGameQueueConfigId()))
                        .gameStartDt(DateUtil.msToLocalDateTime(currentMatchInfo.getGameStartTime()))
                        .gameType(currentMatchInfo.getGameType())
                        .queueId((int) currentMatchInfo.getGameQueueConfigId())
                .build());
        for (CurrentMatchParticipantDto summoner: currentMatchInfo.getParticipants()) {
            LolCurrentMatchParticipantDto participantDto = this.getCurrentMatchSummonerInfo(summoner, currentMatchInfo.getGameQueueConfigId());
            /*String championName = matchMapper.getChampionNameByKey(String.valueOf(summoner.getChampionId()));
            participantDto.setPlayerDto(LolCurrentMatchPlayerDto.builder()
                            .puuid(summoner.getPuuid())
                            .championId(summoner.getChampionId())
                            .championIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, championName))
                            .teamId(summoner.getTeamId())
                    .build());*/
            participantDtoList.add(participantDto);
        }

        currentMatchInfoDto.setParticipantDtoList(participantDtoList);

        return currentMatchInfoDto;
    }

    /**
     * DB에 저장되지 않은 match Id 전체 목록 가져오기
     * @param puuid
     * @return
     */
    private List<String> getNewMatchIdList(String puuid, String latestMatchId, Long startTime, Long endTime) {
        LinkedHashSet<String> newMatchIdList = new LinkedHashSet<>();
        latestMatchId = latestMatchId == null ? "" : latestMatchId;

        int startIndex = 0;
        int count = 100;
        final int MAX_LOOP_COUNT = 50;
        for (int i=0; i<=MAX_LOOP_COUNT; i++) {
            List<String> matchIdList = matchApiService.getMatchIdListByPuuid(GetMatchIdListReqDto.builder()
                .puuid(puuid)
                .count(count)
                .start(startIndex)
                .startTime(startTime / 1000)
                .endTime(endTime / 1000)
            .build());

            if (matchIdList.isEmpty()) {
                break;
            }

            int foundIndex = matchIdList.indexOf(latestMatchId);
            if (foundIndex == -1 || latestMatchId.isEmpty()) {
                newMatchIdList.addAll(matchIdList);
            } else {
                // foundIndex가 0보다 큰 경우에만 이전 매치들을 추가 (foundIndex가 0이면 새로운 match 없음)
                if (foundIndex > 0) {
                    newMatchIdList.addAll(matchIdList.subList(0, foundIndex));
                }
                break;
            }

//            try {
//                Thread.sleep(1300);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // 스레드의 interrupt 상태를 복원
//            }

            startIndex += count;
        }

        // 날짜별 시퀀스 부여를 위해 날짜 오름차순으로 정렬
        List<String> returnList = new ArrayList<>(newMatchIdList);
        Collections.reverse(returnList);

        return returnList;
    }

    private LolMatchInfoResDto makeMatchInfoResDto(LolMatchEntity matchEntity, List<LolMatchParticipantEntity> participantEntityList) {
        LolMatchInfoDto infoDto = new LolMatchInfoDto();
        BeanUtils.copyProperties(matchEntity, infoDto);
        infoDto.setGameModeName(MatchGameMode.getQueueName(infoDto.getQueueId()));

        List<LolMatchParticipantDto> participantDtoList = participantEntityList.stream()
                .map(participant -> {
                    LolMatchParticipantDto entity = new LolMatchParticipantDto();
                    BeanUtils.copyProperties(participant, entity);
                    entity.setChampProfileIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, participant.getChampName()));
                    return entity;
                })
                .toList();

        return LolMatchInfoResDto.builder()
                .matchInfo(infoDto)
                .participantList(participantDtoList)
                .build();
    }

    private LolCurrentMatchParticipantDto getCurrentMatchSummonerInfo(CurrentMatchParticipantDto summoner, long queueId) {
        Map<String, Object> summonerInfoMap = matchMapper.getSummonerBasicInfoByPuuid(summoner.getPuuid());
        LolCurrentMatchPlayerDto playerDto;
        SummonerBasicInfoDto summonerInfoDto = null;
        ReviewStatsDto reviewStatsDto = null;
        List<ReviewTagDto> frequentTagDtoList = null;

        SiteMatchGameMode currentGameMode = SiteMatchGameMode.findByQueueId((int) queueId);
        SiteLeagueTier tier = null;
        String rank = null;

        if (summonerInfoMap == null || Boolean.FALSE.equals(summonerInfoMap.get("has_riot_account"))) {
            // 라이엇 계정 테이블에 소환사 정보 존재하지않으면 저장 및 데이터 set
            LolSummonerProfileResDto summonerProfile = lolSummonerService.conflictSummonerInfo(summoner.getPuuid());
            summonerInfoDto = SummonerBasicInfoDto.builder()
                    .puuid(summonerProfile.getRiotAccountInfoEntity().getPuuid())
                    .gameName(summonerProfile.getRiotAccountInfoEntity().getGameName())
                    .tagLine(summonerProfile.getRiotAccountInfoEntity().getTagLine())
                    .profileIconId(summonerProfile.getLolSummonerDto().getProfileIconId())
                    .profileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerProfile.getLolSummonerDto().getProfileIconId()))
                    .build();

            if (currentGameMode.isRankGame()) {
                for (LolSummonerLeagueEntity leagueEntity : summonerProfile.getLeagueEntityList()) {
                    if (leagueEntity.getQueueType().equals(currentGameMode.getLeagueName())) {
                        tier = SiteLeagueTier.findByTierCode(leagueEntity.getTier());
                        rank = leagueEntity.getRank();
                        break;
                    }
                }
            }
        }

        if (summonerInfoMap != null) {
            if (Boolean.TRUE.equals(summonerInfoMap.get("has_riot_account"))) {
                // 계정정보 DB에 존재하는경우만 리뷰, 태그, 랭크정보 set
                reviewStatsDto = reviewService.getReviewStats(summoner.getPuuid());
                frequentTagDtoList = reviewService.getFrequentTagCount(summoner.getPuuid(), 3);

                summonerInfoDto = SummonerBasicInfoDto.builder()
                        .puuid(summonerInfoMap.get("puuid").toString())
                        .gameName(summonerInfoMap.get("game_name").toString())
                        .tagLine(summonerInfoMap.get("tag_line").toString())
                        .profileIconId(summonerInfoMap.get("profile_icon_id").toString())
                        .profileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerInfoMap.get("profile_icon_id").toString()))
                        .build();

                if (currentGameMode.isRankGame()) {
                    LolSummonerLeagueEntity leagueEntity = lolSummonerService.getSummonerLeagueInfo(summoner.getSummonerId(), currentGameMode.getLeagueName());
                    tier = SiteLeagueTier.findByTierCode(leagueEntity.getTier());
                    rank = leagueEntity.getRank();
                }
            }
        }

        String championName = matchMapper.getChampionNameByKey(String.valueOf(summoner.getChampionId()));
        playerDto = LolCurrentMatchPlayerDto.builder()
                .puuid(summoner.getPuuid())
                .championId(summoner.getChampionId())
                .championIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, championName))
                .teamId(summoner.getTeamId())
                .build();

        if (currentGameMode.isRankGame()) {
            playerDto.setLeagueDto(LolMatchPlayerLeagueDto.builder()
                    .tier(tier.getTierCode())
                    .acronymTier(tier.getAcronym())
                    .tierColor(tier.getColor())
                    .rank(rank)
                    .build());
        }

        return LolCurrentMatchParticipantDto.builder()
                .summonerInfoDto(summonerInfoDto)
                .reviewStatsDto(reviewStatsDto)
                .frequentTagDtoList(frequentTagDtoList)
                .playerDto(playerDto)
                .build();
    }
}
