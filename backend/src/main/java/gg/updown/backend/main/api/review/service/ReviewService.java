package gg.updown.backend.main.api.review.service;

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
import gg.updown.backend.main.enums.SiteMatchGameMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
            @Lazy LolMatchService matchService, NotificationService notificationService, AuthService authService
    ) {
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
                    .notificationId(UUID.randomUUID().toString())
                    .targetSiteCode(accountEntity.getMemberSiteCode())
//                    .content(gameStartDt + " 에 플레이한 " + gameModeName + " 게임에 새로운 평가가 등록되었습니다.")
                    .championName((String) matchInfo.get("champ_name"))
                    .championIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, (String) matchInfo.get("champ_name")))
                    .gameCreateDt(gameStartDt)
                    .gameModeName(gameModeName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReview(ReviewUpdateReqDto reqDto) {
        transactionService.updateSummonerReview(reqDto);
    }

    public ReviewStatsDto getReviewStats(String targetPuuid) {
        return reviewMapper.getReviewStatus(targetPuuid);
    }

    public ReviewStatsDto getReviewAvgRating(String targetPuuid) {
        return reviewMapper.getReviewAvgRating(targetPuuid);
    }

    public List<ReviewTagDto> getFrequentTagCount(String targetPuuid) {
        return reviewMapper.getFrequentTagCount(targetPuuid);
    }

    public List<ReviewDto> getRecentReviewList(String targetPuuid, int limit) {
        return reviewMapper.getRecentReviewList(targetPuuid, limit);
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
}
