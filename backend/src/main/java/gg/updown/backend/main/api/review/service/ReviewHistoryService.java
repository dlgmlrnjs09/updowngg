package gg.updown.backend.main.api.review.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
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
        List<ReviewHistoryDto> historyList = new ArrayList<>();
        List<ReviewDto> writtenReviews = reviewService.getWroteReviewList(null, String.valueOf(siteCode));
        for (ReviewDto review : writtenReviews) {
            ReviewHistoryDto historyDto = new ReviewHistoryDto();
            LolMatchInfoDto matchInfoDto = new LolMatchInfoDto();
            List<LolMatchParticipantDto> participantDtoList = new ArrayList<>();
            BeanUtils.copyProperties(matchService.getMatchInfo(review.getMatchId()), matchInfoDto);
            matchInfoDto.setGameModeName(SiteMatchGameMode.findByQueueCode(matchInfoDto.getGameMode()).getQueueName());
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
