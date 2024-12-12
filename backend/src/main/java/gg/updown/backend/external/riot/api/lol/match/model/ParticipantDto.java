package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; /** 게임 참가자 정보 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {
    /** 획득한 어시스트 수 */
    private int assists;

    /** 바론 처치 수 */
    private int baronKills;

    /** 현상금 레벨 */
    private int bountyLevel;

    /** 획득한 총 챔피언 경험치 */
    private int champExperience;

    /** 최종 챔피언 레벨 */
    private int champLevel;

    /** 플레이한 챔피언의 ID */
    private int championId;

    /** 플레이한 챔피언의 이름 */
    private String championName;

    /** 챔피언 변신 상태 (카인 등) */
    private int championTransform;

    /** 구매한 소비 아이템 수 (물약, 와드 등) */
    private int consumablesPurchased;

    /** 건물에 가한 총 피해량 */
    private int damageDealtToBuildings;

    /** 중립 몬스터에 가한 총 피해량 */
    private int damageDealtToObjectives;

    /** 포탑에 가한 총 피해량 */
    private int damageDealtToTurrets;

    /** 자신이 감소시킨 피해량 (방어력/마법저항력으로 인한) */
    private int damageSelfMitigated;

    /** 죽은 횟수 */
    private int deaths;

    /** 설치한 제어 와드 수 */
    private int detectorWardsPlaced;

    /** 더블킬 횟수 */
    private int doubleKills;

    /** 드래곤 처치 횟수 */
    private int dragonKills;

    /** 첫 처치 어시스트 여부 */
    private boolean firstBloodAssist;

    /** 첫 처치 여부 */
    private boolean firstBloodKill;

    /** 첫 포탑 파괴 어시스트 여부 */
    private boolean firstTowerAssist;

    /** 첫 포탑 파괴 여부 */
    private boolean firstTowerKill;

    /** 조기 항복으로 게임 종료 여부 */
    private boolean gameEndedInEarlySurrender;

    /** 항복으로 게임 종료 여부 */
    private boolean gameEndedInSurrender;

    /** 획득한 총 골드량 */
    private int goldEarned;

    /** 사용한 총 골드량 */
    private int goldSpent;

    /** 개인 포지션 */
    private String individualPosition;

    /** 억제기 파괴 수 */
    private int inhibitorKills;

    /** 억제기 파괴 관여 수 */
    private int inhibitorTakedowns;

    /** 팀의 억제기 파괴당한 수 */
    private int inhibitorsLost;

    /** 아이템 슬롯 0번의 아이템 ID */
    private int item0;

    /** 아이템 슬롯 1번의 아이템 ID */
    private int item1;

    /** 아이템 슬롯 2번의 아이템 ID */
    private int item2;

    /** 아이템 슬롯 3번의 아이템 ID */
    private int item3;

    /** 아이템 슬롯 4번의 아이템 ID */
    private int item4;

    /** 아이템 슬롯 5번의 아이템 ID */
    private int item5;

    /** 아이템 슬롯 6번의 아이템 ID (장신구) */
    private int item6;

    /** 구매한 총 아이템 수 */
    private int itemsPurchased;

    /** 연속 처치 횟수 */
    private int killingSprees;

    /** 챔피언 처치 횟수 */
    private int kills;

    /** 게임에서의 라인 */
    private String lane;

    /** 가한 최대 치명타 피해량 */
    private int largestCriticalStrike;

    /** 최대 연속 처치 횟수 */
    private int largestKillingSpree;

    /** 최대 동시 처치 횟수 */
    private int largestMultiKill;

    /** 최대 생존 시간 (초) */
    private int longestTimeSpentLiving;

    /** 가한 마법 피해량 */
    private int magicDamageDealt;

    /** 챔피언에게 가한 마법 피해량 */
    private int magicDamageDealtToChampions;

    /** 받은 마법 피해량 */
    private int magicDamageTaken;

    /** 처치한 중립 미니언 수 */
    private int neutralMinionsKilled;

    /** 넥서스 파괴 횟수 */
    private int nexusKills;

    /** 넥서스 파괴 관여 횟수 */
    private int nexusTakedowns;

    /** 팀의 넥서스 파괴당한 횟수 */
    private int nexusLost;

    /** 스틸한 중립 몬스터 수 */
    private int objectivesStolen;

    /** 중립 몬스터 스틸 어시스트 횟수 */
    private int objectivesStolenAssists;

    /** 게임 내 참가자 ID */
    private int participantId;

    /** 펜타킬 횟수 */
    private int pentaKills;

    /** 룬 정보 */
    private PerksDto perks;

    /** 가한 물리 피해량 */
    private int physicalDamageDealt;

    /** 챔피언에게 가한 물리 피해량 */
    private int physicalDamageDealtToChampions;

    /** 받은 물리 피해량 */
    private int physicalDamageTaken;

    /** 프로필 아이콘 ID */
    private int profileIcon;

    /** 플레이어의 PUUID */
    private String puuid;

    /** 쿼드라킬 횟수 */
    private int quadraKills;

    /** 라이엇 계정 이름 */
    private String riotIdName;

    /** 라이엇 계정 태그라인 */
    private String riotIdTagline;

    /** 게임에서의 역할 */
    private String role;

    /** 구매한 제어 와드 수 */
    private int sightWardsBoughtInGame;

    /** 스킬 1(Q) 사용 횟수 */
    private int spell1Casts;

    /** 스킬 2(W) 사용 횟수 */
    private int spell2Casts;

    /** 스킬 3(E) 사용 횟수 */
    private int spell3Casts;

    /** 스킬 4(R) 사용 횟수 */
    private int spell4Casts;

    /** 소환사 주문 1 사용 횟수 */
    private int summoner1Casts;

    /** 소환사 주문 1 ID */
    private int summoner1Id;

    /** 소환사 주문 2 사용 횟수 */
    private int summoner2Casts;

    /** 소환사 주문 2 ID */
    private int summoner2Id;

    /** 소환사 ID */
    private String summonerId;

    /** 소환사 레벨 */
    private int summonerLevel;

    /** 소환사 이름 */
    private String summonerName;

    /** 팀의 조기 항복 여부 */
    private boolean teamEarlySurrendered;

    /** 팀 ID (100: 블루, 200: 레드) */
    private int teamId;

    /** 팀 내 포지션 */
    private String teamPosition;

    /** CC를 건 총 시간 */
    private int timeCCingOthers;

    /** 게임 플레이 시간 (초) */
    private int timePlayed;

    /** 가한 총 피해량 */
    private int totalDamageDealt;

    /** 챔피언에게 가한 총 피해량 */
    private int totalDamageDealtToChampions;

    /** 팀원에게 준 총 보호막 량 */
    private int totalDamageShieldedOnTeammates;

    /** 받은 총 피해량 */
    private int totalDamageTaken;

    /** 총 회복량 */
    private int totalHeal;

    /** 팀원 총 회복량 */
    private int totalHealsOnTeammates;

    /** 처치한 미니언 수 */
    private int totalMinionsKilled;

    /** CC를 건 총 시간 */
    private int totalTimeCCDealt;

    /** 총 사망 시간 */
    private int totalTimeSpentDead;

    /** 회복시킨 총 유닛 수 */
    private int totalUnitsHealed;

    /** 트리플킬 횟수 */
    private int tripleKills;

    /** 가한 고정 피해량 */
    private int trueDamageDealt;

    /** 챔피언에게 가한 고정 피해량 */
    private int trueDamageDealtToChampions;

    /** 받은 고정 피해량 */
    private int trueDamageTaken;

    /** 파괴한 포탑 수 */
    private int turretKills;

    /** 포탑 파괴 관여 수 */
    private int turretTakedowns;

    /** 팀의 파괴당한 포탑 수 */
    private int turretsLost;

    /** 언리얼킬 횟수 */
    private int unrealKills;

    /** 시야 점수 */
    private int visionScore;

    /** 구매한 시야 와드 수 */
    private int visionWardsBoughtInGame;

    /** 파괴한 와드 수 */
    private int wardsKilled;

    /** 설치한 와드 수 */
    private int wardsPlaced;

    /** 승리 여부 */
    private boolean win;
}
