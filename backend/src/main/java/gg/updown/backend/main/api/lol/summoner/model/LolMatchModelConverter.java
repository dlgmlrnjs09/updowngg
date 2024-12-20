package gg.updown.backend.main.api.lol.summoner.model;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.api.lol.match.model.InfoDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.model.ParticipantDto;
import gg.updown.backend.main.api.lol.match.model.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.LolMatchParticipantEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LolMatchModelConverter {
    public LolMatchEntity convertMatchDtoToLolMatchEntity(MatchDto matchDto) {
        InfoDto infoDto = matchDto.getInfo();
        return LolMatchEntity.builder()
                .matchId(matchDto.getMetadata().getMatchId())
                .gameCreateDt(DateUtil.msToLocalDateTime(infoDto.getGameCreation()))
                .gameDuration(infoDto.getGameDuration())
                .gameId(infoDto.getGameId())
                .gameMode(infoDto.getGameMode())
                .gameName(infoDto.getGameName())
                .gameStartDt(DateUtil.msToLocalDateTime(infoDto.getGameStartTimestamp()))
                .gameType(infoDto.getGameType())
                .gameVersion(infoDto.getGameVersion())
                .mapId(infoDto.getMapId())
                .queueId(infoDto.getQueueId())
                .build();
    }

    public List<LolMatchParticipantEntity> convertMatchDtoToLolMatchParticipantList(MatchDto matchDto) {
        List<LolMatchParticipantEntity> resultParticipantDtoList = new ArrayList<>();
        List<ParticipantDto> participantDtoList = matchDto.getInfo().getParticipants();
        for (ParticipantDto dto : participantDtoList) {
            resultParticipantDtoList.add(LolMatchParticipantEntity.builder()
                    .matchId(matchDto.getMetadata().getMatchId())
                    .puuid(dto.getPuuid())
                    .riotIdGameName(dto.getRiotIdGameName())
                    .riotIdTagline(dto.getRiotIdTagline())
                    .position(dto.getIndividualPosition())
                    .summonerId(dto.getSummonerId())
                    .summonerName(dto.getSummonerName())
                    .summonerLevel(dto.getSummonerLevel())
                    .teamId(dto.getTeamId())
                    .timePlayed(dto.getTimePlayed())
                    .kills(dto.getKills())
                    .assists(dto.getAssists())
                    .deaths(dto.getDeaths())
                    .champLevel(dto.getChampLevel())
                    .champId(dto.getChampionId())
                    .champName(dto.getChampionName())
                    .totalDamageToChampion(dto.getTotalDamageDealtToChampions())
                    .totalDamageTaken(dto.getTotalDamageTaken())
                    .build());
        }

        return resultParticipantDtoList;
    }
}
