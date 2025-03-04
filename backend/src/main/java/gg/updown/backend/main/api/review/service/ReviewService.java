package gg.updown.backend.main.api.review.service;

import gg.updown.backend.common.util.CalculateUtil;
import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.external.riot.enums.MatchGameMode;
import gg.updown.backend.main.api.auth.model.SiteAccountEntity;
import gg.updown.backend.main.api.auth.service.AuthService;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.notification.model.NotificationDto;
import gg.updown.backend.main.api.notification.service.NotificationService;
import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagCategoryEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagSuggestEntity;
import gg.updown.backend.main.enums.SiteNotificationType;
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorDevMessage;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ReviewService {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    @Value("${match.basic.start-time}")
    private String basicStartTime;

    private final ReviewMapper reviewMapper;
    private final ReviewTransactionService transactionService;
    private final LolSummonerService summonerService;
    private final LolMatchService matchService;
    private final NotificationService notificationService;
    private final AuthService authService;

    public ReviewService(
            ReviewMapper reviewMapper,
            ReviewTransactionService transactionService,
            LolSummonerService summonerService,
            @Lazy LolMatchService matchService, NotificationService notificationService, AuthService authService,
            DdragonService ddragonService) {
        this.reviewMapper = reviewMapper;
        this.transactionService = transactionService;
        this.summonerService = summonerService;
        this.matchService = matchService;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    public List<ReviewTagEntity> getReviewTagList() {
        return reviewMapper.getReviewTagList();
    }

    public List<ReviewTagCategoryEntity> getReviewTagCategoryList() {
        return reviewMapper.getReviewTagCategoryList();
    }

    public List<ReviewDto> getWroteReviewList(String reviewerPuuid) {
        return reviewMapper.getWroteReviewList(reviewerPuuid);
    }

    public List<ReviewDto> getWroteReviewListPaging(long reviewerSiteCode, int page, int itemsPerPage) {
        int offset = (page - 1) * itemsPerPage;
        return reviewMapper.getWroteReviewListPaging(reviewerSiteCode, itemsPerPage, offset);
    }

    public List<ReviewDto> getReceivedReviewListPaging(String targetPuuid, int page, int itemsPerPage) {
        int offset = (page - 1) * itemsPerPage;
        return reviewMapper.getReceivedReviewListPaging(targetPuuid, itemsPerPage, offset);
    }

    public int getWroteReviewCount(long reviewerSiteCode) {
        return reviewMapper.getWroteReviewCount(reviewerSiteCode);
    }

    public int getReceivedReviewCount(String targetPuuid) {
        return reviewMapper.getReceivedReviewCount(targetPuuid);
    }

    public void submitReview(ReviewSubmitReqDto reqDto) {
        transactionService.insertSummonerReview(reqDto);
        // 리뷰작성과 무관하므로 log만 출력
        try {
            // 리뷰남긴 사용자 정보, 경기목록 가져오기
            summonerService.conflictSummonerInfo(reqDto.getTargetPuuid());
            Long startDate = DateUtil.yyyyMMddToMilliseconds(basicStartTime);
            Long endDate = DateUtil.getCurrentTimeMillis();
            matchService.getAndInsertMatchIdList(reqDto.getTargetPuuid(), startDate, endDate);

            // 리뷰남긴 사용자 계정으로 알림 전송
            SiteAccountEntity accountEntity = authService.getSiteAccountByPuuid(reqDto.getTargetPuuid());
            if (accountEntity == null) {
                return;
            }

            Map<String, Object> matchInfo = reviewMapper.getReviewNotificationElement(reqDto.getMatchId(), reqDto.getTargetPuuid());
            LocalDateTime gameStartDt = ((Timestamp) matchInfo.get("game_create_dt")).toLocalDateTime();
            String gameModeName = MatchGameMode.getQueueName((Integer) matchInfo.get("queue_id"));
            notificationService.notify(NotificationDto.builder()
                    .notificationType(SiteNotificationType.SITE_REVIEW.getCode())
                    .notificationId(UUID.randomUUID().toString())
                    .subSeq(reqDto.getSummonerReviewSeq())
                    .targetSiteCode(accountEntity.getMemberSiteCode())
                    .targetPuuid(accountEntity.getPuuid())
                    .content("**" + gameStartDt + "** 에 플레이한 " + "**" +  gameModeName + "** 게임의 평가가 도착했어요!")
                    .iconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, (String) matchInfo.get("champ_name")))
                    .actionDt(gameStartDt)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReview(ReviewUpdateReqDto reqDto) {
        transactionService.updateSummonerReview(reqDto);
    }

    public boolean deleteReview(long reviewSeq, long reviewerSiteCode) {
        if (reviewMapper.deleteReviewBySiteCode(reviewSeq, reviewerSiteCode) == 0) {
            throw new SiteCommonException(HttpStatus.BAD_REQUEST, SiteErrorDevMessage.NOT_REVIEWER.getMessage());
        }

        return true;
    }

    public ReviewStatsDto getReviewStats(String targetPuuid) {
        ReviewStatsDto reviewStatsDto = reviewMapper.getReviewStatus(targetPuuid);
        if (reviewStatsDto == null) {
            return null;
        }

        reviewStatsDto.setScore(CalculateUtil.calculateWilsonScore((int) reviewStatsDto.getUpCount(), (int) reviewStatsDto.getDownCount()));
        return reviewStatsDto;
    }

    public ReviewStatsDto getReviewAvgRating(String targetPuuid) {
        return reviewMapper.getReviewAvgRating(targetPuuid);
    }

    public List<ReviewTagDto> getFrequentTagCount(String targetPuuid, int limit) {
        return reviewMapper.getFrequentTagCount(targetPuuid, limit);
    }

    public List<ReviewDto> getRecentReviewList(String targetPuuid, int limit) {
        List<ReviewDto> reviewList = reviewMapper.getRecentReviewList(targetPuuid, limit);
        for (ReviewDto reviewDto : reviewList) {
            reviewDto.setTargetProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, reviewDto.getTargetProfileIconId()));
        }
        return reviewList;
    }

    public List<ReviewRatingByChampDto> getAvgRatingByChamp(String targetPuuid) {
        List<ReviewRatingByChampDto> dtoList = reviewMapper.getAvgRatingByChamp(targetPuuid);
        for (ReviewRatingByChampDto dto : dtoList) {
            dto.setChampIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, dto.getChampName()));
        }
        return dtoList;
    }

    public List<ReviewRatingByPositionDto> getAvgRatingByPosition(String targetPuuid) {
        return reviewMapper.getAvgRatingByPosition(targetPuuid);
    }

    public void insertSuggestTagList(List<ReviewTagSuggestReqDto> reqDto) {
        if (reqDto.isEmpty()) {
            return;
        }

        List<ReviewTagSuggestEntity> entityList = new ArrayList<>();
        for (ReviewTagSuggestReqDto dto : reqDto) {
            ReviewTagSuggestEntity entity = new ReviewTagSuggestEntity();
            BeanUtils.copyProperties(dto, entity);
            entityList.add(entity);
        }
        reviewMapper.insertSuggestTagList(entityList);
    }

    public int findReviewPage(Long reviewSeq, int itemsPerPage) {
        int reviewIndex = reviewMapper.findReviewIndex(reviewSeq);
        return (reviewIndex / itemsPerPage) + 1;
    }

    public boolean checkExist(String reviewerPuuid, String targetPuuid) {
        return reviewMapper.checkExist(reviewerPuuid, targetPuuid) > 0;
    }

    public ReviewDto getWrittenToTarget(String reviewerPuuid, String targetPuuid) {
        return reviewMapper.getWrittenToTarget(reviewerPuuid, targetPuuid);
    }

    public ReviewByMatchSummaryDto getReviewerAndTagsByMatch(String targetPuuid, String matchId) {
        ReviewByMatchSummaryDto dto = reviewMapper.getReviewerAndTagsByMatch(targetPuuid, matchId);
        if (dto == null) {
            return null;
        }

        dto.getReviewerInfoList().forEach(reviewerInfo -> {
            if (reviewerInfo.getIsAnonymous() != null && reviewerInfo.getIsAnonymous()) {
                reviewerInfo.anonymizeReviewerInfo();
                return;
            }

            String champIconUrl = RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, String.valueOf(reviewerInfo.getReviewerChampName()));
            reviewerInfo.setReviewerChampIconUrl(champIconUrl);
        });
        return dto;
    }
}
