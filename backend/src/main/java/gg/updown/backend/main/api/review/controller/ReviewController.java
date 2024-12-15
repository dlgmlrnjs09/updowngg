package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.main.api.review.model.ReviewTagEntity;
import gg.updown.backend.main.api.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/tag")
    public List<ReviewTagEntity> getReviewTagList() {
        return reviewService.getReviewTagList();
    }
}
