package gg.updown.backend.main.api.lol.match.service;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.api.lol.match.model.*;
import gg.updown.backend.external.riot.api.lol.match.service.MatchApiService;
import gg.updown.backend.main.api.lol.match.mapper.LolMatchMapper;
import gg.updown.backend.main.api.lol.match.model.*;
import gg.updown.backend.main.api.lol.summoner.model.LolMatchModelConverter;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LolMatchService {
    private final MatchApiService matchApiService;
    private final LolMatchMapper matchMapper;
    private final LolMatchTransactionService transactionService;
    private final LolSummonerService lolSummonerService;
    private final LolMatchModelConverter lolMatchModelConverter;

    /**
     * match 페이징 목록 DB에서 가져오기
     */
    public List<LolMatchInfoResDto> getMatchListFromDb(String puuid, int startIndex, int count) {
        return matchMapper.getMatchesByPuuid(puuid, startIndex, count);
    }

    /**
     * match 페이징 목록 가져오기 및 DB에 없는 match 정보 저장하기
     */
    public List<LolMatchInfoResDto> getAndInsertMatchList(String puuid, int startIndex, int count) {
        List<LolMatchInfoResDto> resultList = new ArrayList<>();

        List<String> matchIdList = matchApiService.getMatchIdListByPuuid(GetMatchIdListReqDto.builder()
                .puuid(puuid)
                .count(count)
                .start(startIndex)
                .build());

        for (String matchId : matchIdList) {
            resultList.add(this.getMatchResDtoAndInsertConditional(matchId));
        }

        return resultList;
    }


    /**
     * 이미 이미 DB에 존재하면 DB에서 get, 없으면 API에서 get 및 DB 저장
     * @param matchId
     * @return
     */
    private LolMatchInfoResDto getMatchResDtoAndInsertConditional(String matchId) {
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

        // 게임 참여자 10명 정보 없으면 Insert
        for (LolMatchParticipantEntity participantEntity : participantList) {
            lolSummonerService.conflictSummonerInfo(participantEntity.getPuuid(), participantEntity.getRiotIdGameName(), participantEntity.getRiotIdTagline());
        }

        LolMatchInfoDto infoDto = new LolMatchInfoDto();
        BeanUtils.copyProperties(matchInfoEntity, infoDto);
        List<LolMatchParticipantDto> participantDtoList = participantList.stream()
                .map(participant -> {
                    LolMatchParticipantDto entity = new LolMatchParticipantDto();
                    BeanUtils.copyProperties(participant, entity);
                    // 챔피언 초상화 URL Set
                    entity.setChampProfileIconUrl("https://ddragon.leagueoflegends.com/cdn/14.24.1/img/champion/" + participant.getChampName() + ".png");
                    return entity;
                })
                .toList();

        return LolMatchInfoResDto.builder()
            .matchInfo(infoDto)
            .participantList(participantDtoList)
        .build();
    }

    /**
     * DB에 저장되지 않은 match Id 전체 목록 가져오기
     * @param puuid
     * @return
     */
    private List<String> getNewMatchIdList(String puuid, Long startTime, Long endTime) {
        LinkedHashSet<String> newMatchIdList = new LinkedHashSet<>();
        String latestMatchId = matchMapper.getLatestMatchId(puuid);

        int startIndex = 0;
        int count = 100;
        while (true) {
            List<String> matchIdList = matchApiService.getMatchIdListByPuuid(GetMatchIdListReqDto.builder()
                .puuid(puuid)
                .count(count)
                .start(startIndex)
                .startTime(startTime)
                .endTime(endTime)
            .build());

            if (matchIdList.isEmpty()) {
                break;
            }

            int foundIndex = matchIdList.indexOf(latestMatchId);
            if (foundIndex == -1 || latestMatchId.isEmpty()) {
                newMatchIdList.addAll(matchIdList);
            } else {
                int beforeIndex = foundIndex - 1;
                newMatchIdList.addAll(matchIdList.subList(0, beforeIndex));
                break;
            }

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startIndex += count;
        }

        // 날짜별 시퀀스 부여를 위해 날짜 오름차순으로 정렬
        List<String> returnList = new ArrayList<>(newMatchIdList);
        Collections.reverse(returnList);

        return returnList;
    }

    private List<MatchDto> getMatchDetailList(List<String> matchIdList) {
        List<MatchDto> matchDtoList = new ArrayList<>();
        for (String matchId : matchIdList) {
            MatchDto matchDto = matchApiService.getMatchDetailByMatchId(matchId);
            matchDtoList.add(matchDto);

            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return matchDtoList;
    }
}
