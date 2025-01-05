package gg.updown.backend.main.api.review.service;

import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.dto.ReviewSubmitReqDto;
import gg.updown.backend.main.api.review.model.dto.ReviewUpdateReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewTransactionService {

    private final ReviewMapper reviewMapper;

    @Transactional
    public void insertSummonerReview(ReviewSubmitReqDto reqDto) {
        reviewMapper.insertReview(reqDto);
        if (!reqDto.getTagCodeList().isEmpty()) {
            reviewMapper.insertReviewTags(reqDto.getSummonerReviewSeq(), reqDto.getTagCodeList());
        }
    }

    @Transactional
    public void updateSummonerReview(ReviewUpdateReqDto reqDto) {
        reviewMapper.updateReview(reqDto);
        reviewMapper.deleteReviewTags(reqDto.getSummonerReviewSeq());
        if (!reqDto.getTagCodeList().isEmpty()) {
            reviewMapper.insertReviewTags(reqDto.getSummonerReviewSeq(), reqDto.getTagCodeList());
        }
    }
}
