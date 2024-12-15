package gg.updown.backend.main.api.review.service;

import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.ReviewSubmitReqDto;
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
        reviewMapper.insertReviewTags(reqDto.getSummonerReviewSeq(), reqDto.getTagCodeList());
    }
}
