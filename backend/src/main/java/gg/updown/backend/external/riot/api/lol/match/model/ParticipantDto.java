package gg.updown.backend.external.riot.api.lol.match.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 참가자 정보 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {
    /** 노란색 교차된 검 핑 */
    private int allInPings;

    /** 녹색 깃발 핑 */
    private int assistMePings;

    /** 어시스트 횟수 */
    private int assists;

    /** 바론 킬 횟수 */
    private int baronKills;

    /** 현상금 레벨 */
    private int bountyLevel;

    /** 챔피언 경험치 */
    private int champExperience;

    /** 챔피언 레벨 */
    private int champLevel;

    /** 챔피언 ID */
    private int championId;

    /** 챔피언 이름 */
    private String championName;

    /** 블루 일반 핑 (ALT+클릭) */
    private int commandPings;

    /** 챔피언 변형 (케인 전용: 0-없음, 1-슬레이어, 2-어쌔신) */
    private int championTransform;

    /** 소모품 구매 횟수 */
    private int consumablesPurchased;

    /** 도전 과제 정보 */
    private ChallengesDto challenges;

    /** 건물에 가한 피해량 */
    private int damageDealtToBuildings;

    /** 목표물에 가한 피해량 */
    private int damageDealtToObjectives;

    /** 포탑에 가한 피해량 */
    private int damageDealtToTurrets;

    /** 자신이 감소시킨 피해량 */
    private int damageSelfMitigated;

    /** 죽은 횟수 */
    private int deaths;

    /** 설치한 제어 와드 수 */
    private int detectorWardsPlaced;

    /** 더블킬 횟수 */
    private int doubleKills;

    /** 용 처치 횟수 */
    private int dragonKills;

    /** 진행 자격 여부 */
    private boolean eligibleForProgression;

    /** 노란색 물음표 핑 (적 미싱) */
    private int enemyMissingPings;

    /** 적색 눈알 핑 (적 시야) */
    private int enemyVisionPings;

    /** 퍼스트 블러드 어시스트 여부 */
    private boolean firstBloodAssist;

    /** 퍼스트 블러드 킬 여부 */
    private boolean firstBloodKill;

    /** 첫 포탑 어시스트 여부 */
    private boolean firstTowerAssist;

    /** 첫 포탑 파괴 여부 */
    private boolean firstTowerKill;

    /** 조기 항복으로 게임 종료 여부 */
    private boolean gameEndedInEarlySurrender;

    /** 항복으로 게임 종료 여부 */
    private boolean gameEndedInSurrender;

    /** 홀드 핑 */
    private int holdPings;

    /** 노란색 원+가로선 핑 (물러나라) */
    private int getBackPings;

    /** 획득한 골드 */
    private int goldEarned;

    /** 사용한 골드 */
    private int goldSpent;

    /** 개인 포지션 */
    private String individualPosition;

    /** 억제기 파괴 횟수 */
    private int inhibitorKills;

    /** 억제기 파괴 관여 횟수 */
    private int inhibitorTakedowns;

    /** 잃은 억제기 수 */
    private int inhibitorsLost;

    /** 아이템 슬롯 0 */
    private int item0;

    /** 아이템 슬롯 1 */
    private int item1;

    /** 아이템 슬롯 2 */
    private int item2;

    /** 아이템 슬롯 3 */
    private int item3;

    /** 아이템 슬롯 4 */
    private int item4;

    /** 아이템 슬롯 5 */
    private int item5;

    /** 아이템 슬롯 6 (장신구) */
    private int item6;

    /** 구매한 아이템 수 */
    private int itemsPurchased;

    /** 연속 킬 횟수 */
    private int killingSprees;

    /** 킬 횟수 */
    private int kills;

    /** 라인 */
    private String lane;

    /** 가장 큰 치명타 피해량 */
    private int largestCriticalStrike;

    /** 가장 긴 연속 킬 */
    private int largestKillingSpree;

    /** 가장 많은 멀티킬 */
    private int largestMultiKill;

    /** 가장 오래 생존한 시간 */
    private int longestTimeSpentLiving;

    /** 마법 피해량 */
    private int magicDamageDealt;

    /** 챔피언에게 가한 마법 피해량 */
    private int magicDamageDealtToChampions;

    /** 받은 마법 피해량 */
    private int magicDamageTaken;

    /** 미션 정보 */
    private MissionsDto missions;

    /** 처치한 중립 미니언 수 */
    private int neutralMinionsKilled;

    /** 녹색 와드 핑 (시야 필요) */
    private int needVisionPings;

    /** 넥서스 파괴 횟수 */
    private int nexusKills;

    /** 넥서스 파괴 관여 횟수 */
    private int nexusTakedowns;

    /** 잃은 넥서스 수 */
    private int nexusLost;

    /** 훔친 목표물 수 */
    private int objectivesStolen;

    /** 목표물 스틸 어시스트 횟수 */
    private int objectivesStolenAssists;

    /** 파란색 화살표 핑 (가는 중) */
    private int onMyWayPings;

    /** 참가자 ID */
    private int participantId;

    /** 펜타킬 횟수 */
    private int pentaKills;

    /** 룬 정보 */
    private PerksDto perks;

    /** 물리 피해량 */
    private int physicalDamageDealt;

    /** 챔피언에게 가한 물리 피해량 */
    private int physicalDamageDealtToChampions;

    /** 받은 물리 피해량 */
    private int physicalDamageTaken;

    /** 배치 */
    private int placement;

    /** 플레이어 증강 1 */
    private int playerAugment1;

    /** 플레이어 증강 2 */
    private int playerAugment2;

    /** 플레이어 증강 3 */
    private int playerAugment3;

    /** 플레이어 증강 4 */
    private int playerAugment4;

    /** 플레이어 서브팀 ID */
    private int playerSubteamId;

    /** 녹색 미니언 핑 (밀어) */
    private int pushPings;

    /** 프로필 아이콘 */
    private int profileIcon;

    /** PUUID */
    private String puuid;

    /** 쿼드라킬 횟수 */
    private int quadraKills;

    /** 라이엇 ID 게임 이름 */
    private String riotIdGameName;

    /** 라이엇 ID 태그라인 */
    private String riotIdTagline;

    /** 역할 */
    private String role;

    /** 구매한 시야 와드 수 */
    private int sightWardsBoughtInGame;

    /** 1번 스펠 사용 횟수 */
    private int spell1Casts;

    /** 2번 스펠 사용 횟수 */
    private int spell2Casts;

    /** 3번 스펠 사용 횟수 */
    private int spell3Casts;

    /** 4번 스펠 사용 횟수 */
    private int spell4Casts;

    /** 서브팀 배치 */
    private int subteamPlacement;

    /** 1번 소환사 주문 사용 횟수 */
    private int summoner1Casts;

    /** 1번 소환사 주문 ID */
    private int summoner1Id;

    /** 2번 소환사 주문 사용 횟수 */
    private int summoner2Casts;

    /** 2번 소환사 주문 ID */
    private int summoner2Id;

    /** 소환사 ID */
    private String summonerId;

    /** 소환사 레벨 */
    private int summonerLevel;

    /** 소환사 이름 */
    private String summonerName;

    /** 팀 조기 항복 여부 */
    private boolean teamEarlySurrendered;

    /** 팀 ID */
    private int teamId;

    /** 팀 포지션 */
    private String teamPosition;

    /** 다른 사람에게 CC기 적용한 시간 */
    private int timeCCingOthers;

    /** 플레이 시간 */
    private int timePlayed;

    /** 처치한 아군 정글 미니언 수 */
    private int totalAllyJungleMinionsKilled;

    /** 총 피해량 */
    private int totalDamageDealt;

    /** 챔피언에게 가한 총 피해량 */
    private int totalDamageDealtToChampions;

    /** 팀원에게 씌운 총 방어막 */
    private int totalDamageShieldedOnTeammates;

    /** 받은 총 피해량 */
    private int totalDamageTaken;

    /** 처치한 적 정글 미니언 수 */
    private int totalEnemyJungleMinionsKilled;

    /** 총 회복량 */
    private int totalHeal;

    /** 팀원에게 적용한 총 회복량 */
    private int totalHealsOnTeammates;

    /** 처치한 총 미니언 수 */
    private int totalMinionsKilled;

    /** 총 CC기 적용 시간 */
    private int totalTimeCCDealt;

    /** 총 사망 시간 */
    private int totalTimeSpentDead;

    /** 총 회복 유닛 수 */
    private int totalUnitsHealed;

    /** 트리플킬 횟수 */
    private int tripleKills;

    /** 고정 피해량 */
    private int trueDamageDealt;

    /** 챔피언에게 가한 고정 피해량 */
    private int trueDamageDealtToChampions;

    /** 받은 고정 피해량 */
    private int trueDamageTaken;

    /** 포탑 파괴 횟수 */
    private int turretKills;

    /** 포탑 파괴 관여 횟수 */
    private int turretTakedowns;

    /** 잃은 포탑 수 */
    private int turretsLost;

    /** 언리얼 킬 횟수 */
    private int unrealKills;

    /** 시야 점수 */
    private int visionScore;

    /** 시야 제거 핑 */
    private int visionClearedPings;

    /** 구매한 제어 와드 수 */
    private int visionWardsBoughtInGame;

    /** 파괴한 와드 수 */
    private int wardsKilled;

    /** 설치한 와드 수 */
    private int wardsPlaced;

    /** 승리 여부 */
    private boolean win;
}