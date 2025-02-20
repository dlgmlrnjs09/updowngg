package gg.updown.backend.main.api.community.common.controller;

import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.community.common.CommunityServiceFactory;
import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityServiceFactory communityServiceFactory;

    @GetMapping("/{communityCode}/list")
    public <T extends CommunityPostDto> ResponseEntity<List<T>> getPosts(
            @PathVariable String communityCode,
            @RequestParam Map<String, String> searchParamMap
    ) {
        CommunityInterface communityService = communityServiceFactory.getCommunityService(communityCode);
        List<T> resultList = communityService.getPostList(communityCode, searchParamMap);

        return ResponseEntity.ok(resultList);
    }

    @PostMapping("/{communityCode}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> submitPost(
            @PathVariable String communityCode,
            @RequestBody CommunityPostSubmitReqDto paramEntity,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        paramEntity.setWriterSiteCode(((UserDetailImpl) userDetails).getSiteCode());
        paramEntity.setWriterPuuid(((UserDetailImpl) userDetails).getPuuid());
        CommunityInterface communityService = communityServiceFactory.getCommunityService(communityCode);
        communityService.insertPost(communityCode, paramEntity);
        return ResponseEntity.ok(true);
    }
}
