package gg.updown.backend.main.api.lol.summoner.service;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.external.riot.api.lol.league.model.LeagueInfoBySummonerDto;
import gg.updown.backend.external.riot.api.lol.league.service.LeagueApiService;
import gg.updown.backend.external.riot.api.lol.summoner.model.SummonerDto;
import gg.updown.backend.external.riot.api.lol.summoner.service.SummonerApiService;
import gg.updown.backend.main.api.lol.summoner.mapper.LolSummonerMapper;
import gg.updown.backend.main.api.lol.summoner.model.dto.*;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerEntity;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerLeagueEntity;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import gg.updown.backend.main.riot.account.mapper.RiotAccountMapper;
import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LolSummonerService {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final LolSummonerMapper lolSummonerMapper;
    private final AccountApiService accountApiService;
    private final SummonerApiService summonerApiService;
    private final RiotAccountMapper riotAccountMapper;
    private final LeagueApiService leagueApiService;

    public LolSummonerProfileResDto conflictSummonerInfo(String gameName, String tagLine) {
        LolSummonerProfileResDto resultDto = new LolSummonerProfileResDto();

        // 라이엇 계정정보 저장 및 조회
        RiotAccountInfoEntity accountEntity = this.getAndConflictAccountInfo(gameName, tagLine);
        // 롤 소환사 정보 저장 및 조회
        LolSummonerDto summonerEntity = this.getAndConflictSummonerInfo(accountEntity.getPuuid());
        // 롤 소환사 랭크정보 저장 및 조회
        List<LolSummonerLeagueDto> summonerLeagueInfoEntity = this.getAndConflictSummonerLeagueInfo(summonerEntity.getSummonerId());

        resultDto.setRiotAccountInfoEntity(accountEntity);
        resultDto.setLolSummonerDto(summonerEntity);
        resultDto.setLeagueEntityList(summonerLeagueInfoEntity);

        return resultDto;
    }

    public LolSummonerProfileResDto conflictSummonerInfo(String puuid) {
        LolSummonerProfileResDto resultDto = new LolSummonerProfileResDto();

        // 라이엇 계정정보 저장 및 조회
        RiotAccountInfoEntity accountEntity = this.getAndConflictAccountInfo(puuid);
        // 롤 소환사 정보 저장 및 조회
        LolSummonerDto summonerEntity = this.getAndConflictSummonerInfo(accountEntity.getPuuid());
        // 롤 소환사 랭크정보 저장 및 조회
        List<LolSummonerLeagueDto> summonerLeagueInfoEntity = this.getAndConflictSummonerLeagueInfo(summonerEntity.getSummonerId());

        resultDto.setRiotAccountInfoEntity(accountEntity);
        resultDto.setLolSummonerDto(summonerEntity);
        resultDto.setLeagueEntityList(summonerLeagueInfoEntity);

        return resultDto;
    }

    public Map<Integer, LolSummonerLeagueReviewDto> getSummonerLeagueReviewInfo(String puuid) {
        Map<Integer, LolSummonerLeagueReviewDto> resultMap = new HashMap<>();
        List<Map<String, Object>> leagueReviewRatings = lolSummonerMapper.getLeagueReviewRatingByPuuid(puuid);
        leagueReviewRatings.forEach(
                m -> resultMap.put((Integer) m.get("queue_id"), LolSummonerLeagueReviewDto.builder()
                                .upCount(((BigDecimal) m.get("up_count")).intValue())
                                .downCount(((BigDecimal) m.get("down_count")).intValue())
                        .build()));

        return resultMap;
    }

    /**
     * conflictSummonerInfo + getSummonerLeagueReviewInfo
     * 라이엇 계정정보, 롤 소환사/랭크정보, 랭크별 평가정보
     * @return
     */
    public LolSummonerProfileResDto getSummonerProfile(String gameName, String tagLine) {
        LolSummonerProfileResDto summonerInfo = this.conflictSummonerInfo(gameName, tagLine);
        Map<Integer, LolSummonerLeagueReviewDto> reviewMap = this.getSummonerLeagueReviewInfo(summonerInfo.getRiotAccountInfoEntity().getPuuid());

        summonerInfo.getLeagueEntityList().forEach(l -> {
            if (l.getQueueType().equals(SiteMatchGameMode.SOLO_RANK.getLeagueName()) && reviewMap.containsKey(SiteMatchGameMode.SOLO_RANK.getQueueId())) {
                l.setReviewDto(reviewMap.get(SiteMatchGameMode.SOLO_RANK.getQueueId()));
            } else if (l.getQueueType().equals(SiteMatchGameMode.FLEX_RANK.getLeagueName()) && reviewMap.containsKey(SiteMatchGameMode.FLEX_RANK.getQueueId())) {
                l.setReviewDto(reviewMap.get(SiteMatchGameMode.FLEX_RANK.getQueueId()));
            }
        });

        return summonerInfo;
    }

    /**
     * 라이엇 계정정보 DB저장 및 리턴
     * @param gameName
     * @param tagLine
     * @return
     */
    private RiotAccountInfoEntity getAndConflictAccountInfo(String gameName, String tagLine) {
        AccountInfoResDto resDto = accountApiService.getAccountInfoByRiotId(gameName, tagLine);

        // 라이엇 계정정보 저장
        RiotAccountInfoEntity paramAccountInfoEntity = RiotAccountInfoEntity.builder()
                .puuid(resDto.getPuuid())
                .gameName(resDto.getGameName())
                .tagLine(resDto.getTagLine())
                .build();
        riotAccountMapper.conflictAccountBasicInfo(paramAccountInfoEntity);

        return paramAccountInfoEntity;
    }

    private RiotAccountInfoEntity getAndConflictAccountInfo(String puuid) {
        AccountInfoResDto resDto = accountApiService.getAccountInfoByPuuid(puuid);

        // 라이엇 계정정보 저장
        RiotAccountInfoEntity paramAccountInfoEntity = RiotAccountInfoEntity.builder()
                .puuid(resDto.getPuuid())
                .gameName(resDto.getGameName())
                .tagLine(resDto.getTagLine())
                .build();
        riotAccountMapper.conflictAccountBasicInfo(paramAccountInfoEntity);

        return paramAccountInfoEntity;
    }

    private RiotAccountInfoEntity getAndConflictAccountInfo(String puuid, String gamename, String tagLine) {

        // 라이엇 계정정보 저장
        RiotAccountInfoEntity paramAccountInfoEntity = RiotAccountInfoEntity.builder()
                .puuid(puuid)
                .gameName(gamename)
                .tagLine(tagLine)
                .build();
        riotAccountMapper.conflictAccountBasicInfo(paramAccountInfoEntity);

        return paramAccountInfoEntity;
    }

    /**
     * 롤 계정정보 DB저장 및 리턴
     * @param puuid
     * @return
     */
    private LolSummonerDto getAndConflictSummonerInfo(String puuid) {
        LolSummonerDto returnDto = new LolSummonerDto();
        LolSummonerEntity summonerEntity = this.getSummonerInfoByPuuid(puuid);

        // 롤 계정정보 저장
        SummonerDto summonerDto = summonerApiService.getSummonerInfoByPuuid(puuid);
        LolSummonerEntity paramSummonerEntity = LolSummonerEntity.builder()
                .puuid(summonerDto.getPuuid())
                .summonerId(summonerDto.getId())
                .accountId(summonerDto.getAccountId())
                .summonerLevel(summonerDto.getSummonerLevel())
                .externalModDt(DateUtil.msToLocalDateTime(summonerDto.getRevisionDate()))
                .profileIconId(summonerDto.getProfileIconId())
                .build();

        if (summonerEntity == null) {
            // 새로운 계정 검색한 경우
            lolSummonerMapper.insertSummonerInfo(paramSummonerEntity);
        } else {
            if (!summonerEntity.getExternalModDt().isEqual(DateUtil.msToLocalDateTime(summonerDto.getRevisionDate()))) {
                // DB에 저장되어있는 수정일과 API에서 받아온 수정일이 다른경우(업데이트 발생)
                lolSummonerMapper.updateSummonerInfo(paramSummonerEntity);
            }
        }

        BeanUtils.copyProperties(paramSummonerEntity, returnDto);
        if (summonerEntity != null) {
            returnDto.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, summonerEntity.getProfileIconId()));
        }

        return returnDto;
    }

    public LolSummonerEntity getSummonerInfoByPuuid(String puuid) {
        return lolSummonerMapper.getSummonerInfoByPuuid(puuid);
    }

    /**
     * 롤 소환사 랭크정보 DB저장 및 리턴
     */
    public List<LolSummonerLeagueDto> getAndConflictSummonerLeagueInfo(String summonerId) {
        List<LolSummonerLeagueDto> resultList = new ArrayList<>();
        List<LeagueInfoBySummonerDto> leagueInfoList = leagueApiService.getSummonerLeagueInfoBySummonerId(summonerId);
        if (leagueInfoList.isEmpty()) {
            return resultList;
        }

        for (LeagueInfoBySummonerDto dto : leagueInfoList) {
            LolSummonerLeagueEntity paramEntity = new LolSummonerLeagueEntity();
            BeanUtils.copyProperties(dto, paramEntity);

            lolSummonerMapper.conflictSummonerLeagueInfo(paramEntity);
            LolSummonerLeagueDto leagueDto = new LolSummonerLeagueDto();
            BeanUtils.copyProperties(dto, leagueDto);
            resultList.add(leagueDto);
        }

        return resultList;
    }

    public LolSummonerEntity getSummonerInfoBySiteCode(long siteCode) {
        return lolSummonerMapper.getSummonerInfoBySiteCode(siteCode);
    }

    public SummonerBasicInfoDto getSummonerBasicInfoBySiteCode(long siteCode) {
        return lolSummonerMapper.getSummonerBasicInfo(siteCode);
    }

    public List<LolSummonerMostChampionDto> getSummonerMostChampions(String puuid, int limit) {
        List<LolSummonerMostChampionDto> list = lolSummonerMapper.getSummonerMostChampions(puuid, limit);
        list.forEach(m ->
                m.setIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, m.getNameUs()))
        );
        return list;
    }

    public LolSummonerLeagueEntity getSummonerLeagueInfo(String summonerId, String queueType) {
        return lolSummonerMapper.getSummonerLeagueInfo(summonerId, queueType);
    }
}
