package gg.updown.backend.main.api.review.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.external.riot.enums.MatchGameMode;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchParticipantDto;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.api.review.model.dto.ReviewDto;
import gg.updown.backend.main.api.review.model.dto.ReviewHistoryDto;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewHistoryService {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final ReviewService reviewService;
    private final LolMatchService matchService;

    public List<ReviewHistoryDto> getWrittenHistory(long siteCode) {
        List<ReviewDto> writtenReviews = reviewService.getWroteReviewList(null, String.valueOf(siteCode));
        return this.getReviewHistories(writtenReviews);
    }

    public List<ReviewHistoryDto> getReceivedHistory(String puuid) {
        List<ReviewDto> writtenReviews = reviewService.getReceivedReviewList(puuid);
        return this.getReviewHistories(writtenReviews);
    }

    private List<ReviewHistoryDto> getReviewHistories(List<ReviewDto> reviews) {
        List<ReviewHistoryDto> historyList = new ArrayList<>();
        for (ReviewDto review : reviews) {
            ReviewHistoryDto historyDto = new ReviewHistoryDto();
            LolMatchInfoDto matchInfoDto = new LolMatchInfoDto();
            List<LolMatchParticipantDto> participantDtoList = new ArrayList<>();
            BeanUtils.copyProperties(matchService.getMatchInfo(review.getMatchId()), matchInfoDto);
            matchInfoDto.setGameModeName(MatchGameMode.getQueueName(matchInfoDto.getQueueId()));
            matchService.getMatchParticipant(review.getMatchId()).forEach(lolMatchParticipantEntity -> {
                        LolMatchParticipantDto participantDto = new LolMatchParticipantDto();
                        BeanUtils.copyProperties(lolMatchParticipantEntity, participantDto);
                        participantDto.setChampProfileIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, lolMatchParticipantEntity.getChampName()));
                        participantDtoList.add(participantDto);
                    }
            );

            historyDto.setReviewDto(review);
            historyDto.setMatchDto(matchInfoDto);
            historyDto.setParticipantDtoList(participantDtoList);
            historyList.add(historyDto);
        }
        return historyList;
    }
}
