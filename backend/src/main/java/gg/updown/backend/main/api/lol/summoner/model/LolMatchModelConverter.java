package gg.updown.backend.main.api.lol.summoner.model;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.api.lol.match.model.InfoDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.model.ParticipantDto;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchParticipantEntity;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentEntity;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LolMatchModelConverter {

    private final DdragonService ddragonService;

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
            List<Map<String, Integer>> augmentList = new ArrayList<>();
            int augmentSeq = 1;
            if (dto.getPlayerAugment1() != 0) {
                Map<String, Integer> augmentMap = new HashMap<>();
                augmentMap.put("seq", augmentSeq++);
                augmentMap.put("id", dto.getPlayerAugment1());
                augmentList.add(augmentMap);
            }
            if (dto.getPlayerAugment2() != 0) {
                Map<String, Integer> augmentMap = new HashMap<>();
                augmentMap.put("seq", augmentSeq++);
                augmentMap.put("id", dto.getPlayerAugment2());
                augmentList.add(augmentMap);
            }
            if (dto.getPlayerAugment3() != 0) {
                Map<String, Integer> augmentMap = new HashMap<>();
                augmentMap.put("seq", augmentSeq++);
                augmentMap.put("id", dto.getPlayerAugment3());
                augmentList.add(augmentMap);
            }
            if (dto.getPlayerAugment4() != 0) {
                Map<String, Integer> augmentMap = new HashMap<>();
                augmentMap.put("seq", augmentSeq);
                augmentMap.put("id", dto.getPlayerAugment4());
                augmentList.add(augmentMap);
            }

            // ID만 추출한 리스트 생성
            List<Integer> augmentIds = augmentList.stream()
                    .map(map -> map.get("id"))
                    .collect(Collectors.toList());

            // ID를 기준으로 데이터 조회
            List<ArenaAugmentEntity> augments = ddragonService.findAugmentListById(augmentIds);

            // ID와 seq 정보를 매핑하는 맵 생성
            Map<Integer, Integer> idToSeqMap = augmentList.stream()
                    .collect(Collectors.toMap(
                            map -> map.get("id"),
                            map -> map.get("seq")
                    ));

            // seq 값으로 정렬
            augments.sort(Comparator.comparing(augment -> idToSeqMap.getOrDefault(augment.getId(), Integer.MAX_VALUE)));

            // ID와 증강 객체를 매핑하는 맵 생성
            Map<Integer, ArenaAugmentEntity> augmentMap = augments.stream()
                    .collect(Collectors.toMap(ArenaAugmentEntity::getId, entity -> entity));

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
                    .isWin(dto.isWin())
                    .allInPings(dto.getAllInPings())
                    .assistMePings(dto.getAssistMePings())
                    .baronKills(dto.getBaronKills())
                    .bountyLevel(dto.getBountyLevel())
                    .champExperience(dto.getChampExperience())
                    .commandPings(dto.getCommandPings())
                    .championTransform(dto.getChampionTransform())
                    .consumablesPurchased(dto.getConsumablesPurchased())
                    .damageDealtToBuildings(dto.getDamageDealtToBuildings())
                    .damageDealtToObjectives(dto.getDamageDealtToObjectives())
                    .damageDealtToTurrets(dto.getDamageDealtToTurrets())
                    .damageSelfMitigated(dto.getDamageSelfMitigated())
                    .detectorWardsPlaced(dto.getDetectorWardsPlaced())
                    .doubleKills(dto.getDoubleKills())
                    .dragonKills(dto.getDragonKills())
                    .eligibleForProgression(dto.isEligibleForProgression())
                    .enemyMissingPings(dto.getEnemyMissingPings())
                    .enemyVisionPings(dto.getEnemyVisionPings())
                    .firstBloodAssist(dto.isFirstBloodAssist())
                    .firstBloodKill(dto.isFirstBloodKill())
                    .firstTowerAssist(dto.isFirstTowerAssist())
                    .firstTowerKill(dto.isFirstTowerKill())
                    .gameEndedInEarlySurrender(dto.isGameEndedInEarlySurrender())
                    .gameEndedInSurrender(dto.isGameEndedInSurrender())
                    .holdPings(dto.getHoldPings())
                    .getBackPings(dto.getGetBackPings())
                    .goldEarned(dto.getGoldEarned())
                    .goldSpent(dto.getGoldSpent())
                    .individualPosition(dto.getIndividualPosition())
                    .inhibitorKills(dto.getInhibitorKills())
                    .inhibitorTakedowns(dto.getInhibitorTakedowns())
                    .inhibitorsLost(dto.getInhibitorsLost())
                    .item0(dto.getItem0())
                    .item1(dto.getItem1())
                    .item2(dto.getItem2())
                    .item3(dto.getItem3())
                    .item4(dto.getItem4())
                    .item5(dto.getItem5())
                    .item6(dto.getItem6())
                    .itemsPurchased(dto.getItemsPurchased())
                    .killingSprees(dto.getKillingSprees())
                    .lane(dto.getLane())
                    .largestCriticalStrike(dto.getLargestCriticalStrike())
                    .largestKillingSpree(dto.getLargestKillingSpree())
                    .largestMultiKill(dto.getLargestMultiKill())
                    .longestTimeSpentLiving(dto.getLongestTimeSpentLiving())
                    .magicDamageDealt(dto.getMagicDamageDealt())
                    .magicDamageDealtToChampions(dto.getMagicDamageDealtToChampions())
                    .magicDamageTaken(dto.getMagicDamageTaken())
                    .neutralMinionsKilled(dto.getNeutralMinionsKilled())
                    .needVisionPings(dto.getNeedVisionPings())
                    .nexusKills(dto.getNexusKills())
                    .nexusTakedowns(dto.getNexusTakedowns())
                    .nexusLost(dto.getNexusLost())
                    .objectivesStolen(dto.getObjectivesStolen())
                    .objectivesStolenAssists(dto.getObjectivesStolenAssists())
                    .onMyWayPings(dto.getOnMyWayPings())
                    .pentaKills(dto.getPentaKills())
                    .physicalDamageDealt(dto.getPhysicalDamageDealt())
                    .physicalDamageDealtToChampions(dto.getPhysicalDamageDealtToChampions())
                    .physicalDamageTaken(dto.getPhysicalDamageTaken())
                    .placement(dto.getPlacement())
                    .playerAugment1(dto.getPlayerAugment1())
                    .playerAugment2(dto.getPlayerAugment2())
                    .playerAugment3(dto.getPlayerAugment3())
                    .playerAugment4(dto.getPlayerAugment4())
                    .playerAugmentEntity1(dto.getPlayerAugment1() != 0 ? augmentMap.get(dto.getPlayerAugment1()) : null)
                    .playerAugmentEntity2(dto.getPlayerAugment2() != 0 ? augmentMap.get(dto.getPlayerAugment2()) : null)
                    .playerAugmentEntity3(dto.getPlayerAugment3() != 0 ? augmentMap.get(dto.getPlayerAugment3()) : null)
                    .playerAugmentEntity4(dto.getPlayerAugment4() != 0 ? augmentMap.get(dto.getPlayerAugment4()) : null)
                    .playerSubteamId(dto.getPlayerSubteamId())
                    .pushPings(dto.getPushPings())
                    .profileIcon(dto.getProfileIcon())
                    .quadraKills(dto.getQuadraKills())
                    .role(dto.getRole())
                    .sightWardsBoughtInGame(dto.getSightWardsBoughtInGame())
                    .spell1Casts(dto.getSpell1Casts())
                    .spell2Casts(dto.getSpell2Casts())
                    .spell3Casts(dto.getSpell3Casts())
                    .spell4Casts(dto.getSpell4Casts())
                    .subteamPlacement(dto.getSubteamPlacement())
                    .summoner1Casts(dto.getSummoner1Casts())
                    .summoner1Id(dto.getSummoner1Id())
                    .summoner2Casts(dto.getSummoner2Casts())
                    .summoner2Id(dto.getSummoner2Id())
                    .teamEarlySurrendered(dto.isTeamEarlySurrendered())
                    .teamPosition(dto.getTeamPosition())
                    .timeCCingOthers(dto.getTimeCCingOthers())
                    .totalAllyJungleMinionsKilled(dto.getTotalAllyJungleMinionsKilled())
                    .totalDamageDealt(dto.getTotalDamageDealt())
                    .totalDamageShieldedOnTeammates(dto.getTotalDamageShieldedOnTeammates())
                    .totalEnemyJungleMinionsKilled(dto.getTotalEnemyJungleMinionsKilled())
                    .totalHeal(dto.getTotalHeal())
                    .totalHealsOnTeammates(dto.getTotalHealsOnTeammates())
                    .totalMinionsKilled(dto.getTotalMinionsKilled())
                    .totalTimeCCDealt(dto.getTotalTimeCCDealt())
                    .totalTimeSpentDead(dto.getTotalTimeSpentDead())
                    .totalUnitsHealed(dto.getTotalUnitsHealed())
                    .tripleKills(dto.getTripleKills())
                    .trueDamageDealt(dto.getTrueDamageDealt())
                    .trueDamageDealtToChampions(dto.getTrueDamageDealtToChampions())
                    .trueDamageTaken(dto.getTrueDamageTaken())
                    .turretKills(dto.getTurretKills())
                    .turretTakedowns(dto.getTurretTakedowns())
                    .turretsLost(dto.getTurretsLost())
                    .unrealKills(dto.getUnrealKills())
                    .visionScore(dto.getVisionScore())
                    .visionClearedPings(dto.getVisionClearedPings())
                    .visionWardsBoughtInGame(dto.getVisionWardsBoughtInGame())
                    .wardsKilled(dto.getWardsKilled())
                    .wardsPlaced(dto.getWardsPlaced())
                    .build());
        }

        return resultParticipantDtoList;
    }
}
