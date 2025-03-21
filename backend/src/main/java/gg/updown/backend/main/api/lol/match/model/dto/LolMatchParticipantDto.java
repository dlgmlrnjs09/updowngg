package gg.updown.backend.main.api.lol.match.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 참여자/리뷰정보 DTO")
public class LolMatchParticipantDto {
    @Schema(description = "LOL 경기 고유ID")
    private String matchId;
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "라이엇 닉네임")
    private String riotIdGameName;
    @Schema(description = "라이엇 배틀태그")
    private String riotIdTagline;
    @Schema(description = "포지션")
    private String position;
    @Schema(description = "100: 블루팀, 200: 레드팀")
    private int teamId;
    @Schema(description = "팀명(블루팀, 레드팀)")
    private String teamName;
    @Schema(description = "처치 수")
    private int kills;
    @Schema(description = "어시스트 수")
    private int assists;
    @Schema(description = "죽음 수")
    private int deaths;
    @Schema(description = "챔피언 레벨")
    private int champLevel;
    @Schema(description = "챔피언 고유ID")
    private int champId;
    @Schema(description = "챔피언 명(영문)")
    private String champName;
    @Schema(description = "챔피언 아이콘 URL")
    private String champProfileIconUrl;
    @Schema(description = "챔피언에게 가한 피해량")
    private int totalDamageToChampion;
    @Schema(description = "챔피언에게 받은 피해량")
    private int totalDamageTaken;
    @Schema(description = "승리 여부")
    private Boolean isWin;

    @Schema(description = "노란색 교차된 검 핑")
    private int allInPings;

    @Schema(description = "녹색 깃발 핑")
    private int assistMePings;

    @Schema(description = "바론 킬 횟수")
    private int baronKills;

    @Schema(description = "현상금 레벨")
    private int bountyLevel;

    @Schema(description = "챔피언 경험치")
    private int champExperience;

    @Schema(description = "블루 일반 핑 (ALT+클릭)")
    private int commandPings;

    @Schema(description = "챔피언 변형 (케인 전용: 0-없음, 1-슬레이어, 2-어쌔신)")
    private int championTransform;

    @Schema(description = "소모품 구매 횟수")
    private int consumablesPurchased;

    @Schema(description = "건물에 가한 피해량")
    private int damageDealtToBuildings;

    @Schema(description = "목표물에 가한 피해량")
    private int damageDealtToObjectives;

    @Schema(description = "포탑에 가한 피해량")
    private int damageDealtToTurrets;

    @Schema(description = "자신이 감소시킨 피해량")
    private int damageSelfMitigated;

    @Schema(description = "설치한 제어 와드 수")
    private int detectorWardsPlaced;

    @Schema(description = "더블킬 횟수")
    private int doubleKills;

    @Schema(description = "용 처치 횟수")
    private int dragonKills;

    @Schema(description = "진행 자격 여부")
    private boolean eligibleForProgression;

    @Schema(description = "노란색 물음표 핑 (적 미싱)")
    private int enemyMissingPings;

    @Schema(description = "적색 눈알 핑 (적 시야)")
    private int enemyVisionPings;

    @Schema(description = "퍼스트 블러드 어시스트 여부")
    private boolean firstBloodAssist;

    @Schema(description = "퍼스트 블러드 킬 여부")
    private boolean firstBloodKill;

    @Schema(description = "첫 포탑 어시스트 여부")
    private boolean firstTowerAssist;

    @Schema(description = "첫 포탑 파괴 여부")
    private boolean firstTowerKill;

    @Schema(description = "조기 항복으로 게임 종료 여부")
    private boolean gameEndedInEarlySurrender;

    @Schema(description = "항복으로 게임 종료 여부")
    private boolean gameEndedInSurrender;

    @Schema(description = "홀드 핑")
    private int holdPings;

    @Schema(description = "노란색 원+가로선 핑 (물러나라)")
    private int getBackPings;

    @Schema(description = "획득한 골드")
    private int goldEarned;

    @Schema(description = "사용한 골드")
    private int goldSpent;

    @Schema(description = "개인 포지션")
    private String individualPosition;

    @Schema(description = "억제기 파괴 횟수")
    private int inhibitorKills;

    @Schema(description = "억제기 파괴 관여 횟수")
    private int inhibitorTakedowns;

    @Schema(description = "잃은 억제기 수")
    private int inhibitorsLost;

    @Schema(description = "아이템 슬롯 0")
    private int item0;

    @Schema(description = "아이템 슬롯 1")
    private int item1;

    @Schema(description = "아이템 슬롯 2")
    private int item2;

    @Schema(description = "아이템 슬롯 3")
    private int item3;

    @Schema(description = "아이템 슬롯 4")
    private int item4;

    @Schema(description = "아이템 슬롯 5")
    private int item5;

    @Schema(description = "아이템 슬롯 6 (장신구)")
    private int item6;

    @Schema(description = "구매한 아이템 수")
    private int itemsPurchased;

    @Schema(description = "연속 킬 횟수")
    private int killingSprees;

    @Schema(description = "라인")
    private String lane;

    @Schema(description = "가장 큰 치명타 피해량")
    private int largestCriticalStrike;

    @Schema(description = "가장 긴 연속 킬")
    private int largestKillingSpree;

    @Schema(description = "가장 많은 멀티킬")
    private int largestMultiKill;

    @Schema(description = "가장 오래 생존한 시간")
    private int longestTimeSpentLiving;

    @Schema(description = "마법 피해량")
    private int magicDamageDealt;

    @Schema(description = "챔피언에게 가한 마법 피해량")
    private int magicDamageDealtToChampions;

    @Schema(description = "받은 마법 피해량")
    private int magicDamageTaken;

    @Schema(description = "처치한 중립 미니언 수")
    private int neutralMinionsKilled;

    @Schema(description = "녹색 와드 핑 (시야 필요)")
    private int needVisionPings;

    @Schema(description = "넥서스 파괴 횟수")
    private int nexusKills;

    @Schema(description = "넥서스 파괴 관여 횟수")
    private int nexusTakedowns;

    @Schema(description = "잃은 넥서스 수")
    private int nexusLost;

    @Schema(description = "훔친 목표물 수")
    private int objectivesStolen;

    @Schema(description = "목표물 스틸 어시스트 횟수")
    private int objectivesStolenAssists;

    @Schema(description = "파란색 화살표 핑 (가는 중)")
    private int onMyWayPings;

    @Schema(description = "펜타킬 횟수")
    private int pentaKills;

    @Schema(description = "물리 피해량")
    private int physicalDamageDealt;

    @Schema(description = "챔피언에게 가한 물리 피해량")
    private int physicalDamageDealtToChampions;

    @Schema(description = "받은 물리 피해량")
    private int physicalDamageTaken;

    @Schema(description = "배치")
    private int placement;

    @Schema(description = "플레이어 증강 ID")
    private int playerAugment1;

    @Schema(description = "플레이어 증강 ID")
    private int playerAugment2;

    @Schema(description = "플레이어 증강 ID")
    private int playerAugment3;

    @Schema(description = "플레이어 증강 ID")
    private int playerAugment4;

    @Schema(description = "플레이어 증강 1")
    private ArenaAugmentEntity playerAugmentEntity1;

    @Schema(description = "플레이어 증강 2")
    private ArenaAugmentEntity playerAugmentEntity2;

    @Schema(description = "플레이어 증강 3")
    private ArenaAugmentEntity playerAugmentEntity3;

    @Schema(description = "플레이어 증강 4")
    private ArenaAugmentEntity playerAugmentEntity4;

    @Schema(description = "플레이어 서브팀 ID")
    private int playerSubteamId;

    @Schema(description = "녹색 미니언 핑 (밀어)")
    private int pushPings;

    @Schema(description = "프로필 아이콘")
    private int profileIcon;

    @Schema(description = "쿼드라킬 횟수")
    private int quadraKills;

    @Schema(description = "역할")
    private String role;

    @Schema(description = "구매한 시야 와드 수")
    private int sightWardsBoughtInGame;

    @Schema(description = "1번 스펠 사용 횟수")
    private int spell1Casts;

    @Schema(description = "2번 스펠 사용 횟수")
    private int spell2Casts;

    @Schema(description = "3번 스펠 사용 횟수")
    private int spell3Casts;

    @Schema(description = "4번 스펠 사용 횟수")
    private int spell4Casts;

    @Schema(description = "서브팀 배치")
    private int subteamPlacement;

    @Schema(description = "1번 소환사 주문 사용 횟수")
    private int summoner1Casts;

    @Schema(description = "1번 소환사 주문 ID")
    private int summoner1Id;

    @Schema(description = "2번 소환사 주문 사용 횟수")
    private int summoner2Casts;

    @Schema(description = "2번 소환사 주문 ID")
    private int summoner2Id;

    @Schema(description = "팀 조기 항복 여부")
    private boolean teamEarlySurrendered;

    @Schema(description = "팀 포지션")
    private String teamPosition;

    @Schema(description = "다른 사람에게 CC기 적용한 시간")
    private int timeCCingOthers;

    @Schema(description = "처치한 아군 정글 미니언 수")
    private int totalAllyJungleMinionsKilled;

    @Schema(description = "총 피해량")
    private int totalDamageDealt;

    @Schema(description = "팀원에게 씌운 총 방어막")
    private int totalDamageShieldedOnTeammates;

    @Schema(description = "처치한 적 정글 미니언 수")
    private int totalEnemyJungleMinionsKilled;

    @Schema(description = "총 회복량")
    private int totalHeal;

    @Schema(description = "팀원에게 적용한 총 회복량")
    private int totalHealsOnTeammates;

    @Schema(description = "처치한 총 미니언 수")
    private int totalMinionsKilled;

    @Schema(description = "총 CC기 적용 시간")
    private int totalTimeCCDealt;

    @Schema(description = "총 사망 시간")
    private int totalTimeSpentDead;

    @Schema(description = "총 회복 유닛 수")
    private int totalUnitsHealed;

    @Schema(description = "트리플킬 횟수")
    private int tripleKills;

    @Schema(description = "고정 피해량")
    private int trueDamageDealt;

    @Schema(description = "챔피언에게 가한 고정 피해량")
    private int trueDamageDealtToChampions;

    @Schema(description = "받은 고정 피해량")
    private int trueDamageTaken;

    @Schema(description = "포탑 파괴 횟수")
    private int turretKills;

    @Schema(description = "포탑 파괴 관여 횟수")
    private int turretTakedowns;

    @Schema(description = "잃은 포탑 수")
    private int turretsLost;

    @Schema(description = "언리얼 킬 횟수")
    private int unrealKills;

    @Schema(description = "시야 점수")
    private int visionScore;

    @Schema(description = "시야 제거 핑")
    private int visionClearedPings;

    @Schema(description = "구매한 제어 와드 수")
    private int visionWardsBoughtInGame;

    @Schema(description = "파괴한 와드 수")
    private int wardsKilled;

    @Schema(description = "설치한 와드 수")
    private int wardsPlaced;

    // 리뷰정보 객체
    private ReviewDto reviewDto;
    // 리뷰평점 객체
    private ReviewStatsDto reviewStatsDto;

    public void setChampProfileIconUrl(String iconUrl) {
        iconUrl = iconUrl.replaceAll("FiddleSticks", "Fiddlesticks");
        this.champProfileIconUrl = iconUrl;
    }
}
