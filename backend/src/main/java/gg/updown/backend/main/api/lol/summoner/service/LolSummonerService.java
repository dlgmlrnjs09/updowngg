package gg.updown.backend.main.api.lol.summoner.service;

import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.external.riot.api.lol.summoner.model.SummonerDto;
import gg.updown.backend.external.riot.api.lol.summoner.service.SummonerApiService;
import gg.updown.backend.main.api.lol.summoner.mapper.LolSummonerMapper;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerDto;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerEntity;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerProfileDto;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerProfileResDto;
import gg.updown.backend.main.riot.account.mapper.RiotAccountMapper;
import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import gg.updown.backend.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LolSummonerService {

    @Value("${riot.ddragon.basepath}")
    private String ddragonBasepath;

    private final LolSummonerMapper lolSummonerMapper;
    private final AccountApiService accountApiService;
    private final SummonerApiService summonerApiService;
    private final RiotAccountMapper riotAccountMapper;

    public LolSummonerProfileResDto conflictSummonerInfo(String gameName, String tagLine) {
        LolSummonerProfileResDto resultDto = new LolSummonerProfileResDto();

        // 라이엇 계정정보 저장 및 조회
        RiotAccountInfoEntity accountEntity = this.getAndConflictAccountInfo(gameName, tagLine);
        // 롤 소환사 정보 저장 및 조회
        LolSummonerDto summonerEntity = this.getAndConflictSummonerInfo(accountEntity.getPuuid());

        resultDto.setRiotAccountInfoEntity(accountEntity);
        resultDto.setLolSummonerDto(summonerEntity);

        return resultDto;
    }

    public void conflictSummonerInfo(String puuid, String gameName, String tagLine) {
        // 라이엇 계정정보 저장 및 조회
        this.getAndConflictAccountInfo(puuid, gameName, tagLine);
        // 롤 소환사 정보 저장 및 조회
        this.getAndConflictSummonerInfo(puuid);
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
        LolSummonerEntity summonerEntity = lolSummonerMapper.getSummonerInfoByPuuid(puuid);

        // 롤 계정정보 저장
        SummonerDto summonerDto = summonerApiService.getSummonerInfoByPuuid(puuid);
        LolSummonerEntity paramSummonerEntity = LolSummonerEntity.builder()
                .puuid(summonerDto.getPuuid())
                .summonerId(summonerDto.getPuuid())
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
            returnDto.setProfileIconUrl(ddragonBasepath + "/cdn/14.4.1/img/profileicon/" + summonerEntity.getProfileIconId() + ".png");
        }

        return returnDto;
    }
}
