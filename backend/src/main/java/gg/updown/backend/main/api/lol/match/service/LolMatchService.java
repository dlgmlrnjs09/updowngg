package gg.updown.backend.main.api.lol.match.service;

import gg.updown.backend.external.riot.api.lol.match.model.GetMatchIdListReqDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.service.MatchApiService;
import gg.updown.backend.main.api.lol.match.mapper.LolMatchMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LolMatchService {
    private final MatchApiService matchApiService;
    private final LolMatchMapper matchMapper;

    public void insertNewMatchList(String puuid) {
        // 평가기간 7일까지인 관계로 7일이전 대전내역만 Get
        Long beforeOneWeekMillis = Instant.now().minus(7, ChronoUnit.DAYS).getEpochSecond();
        Long currentMillis = Instant.now().toEpochMilli();
        List<String> newMatchIdList = this.getNewMatchIdList(puuid, beforeOneWeekMillis, currentMillis);
        List<MatchDto> newMatchDetailList = this.getMatchDetailList(newMatchIdList);

        System.out.println("");
    }

    /**
     * DB에 저장되지 않은 match 전체 목록 가져오기
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
