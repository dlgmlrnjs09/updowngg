package gg.updown.backend.main.api.community.common.controller;

import gg.updown.backend.common.model.PagingDto;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.community.common.CommunityServiceFactory;
import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.party.model.*;
import gg.updown.backend.main.api.community.party.service.PartyCommunityService;
import jakarta.validation.Valid;
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

    private static final int DISPLAY_PAGE_COUNT = 5;

    private final CommunityServiceFactory communityServiceFactory;
    private final PartyCommunityService partyCommunityService;

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

    @PostMapping("/party/apply")
    @PreAuthorize("isAuthenticated()")
    public void applyParty(
            @RequestBody @Valid PartyCommunityApplyReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.apply(puuid, reqDto.getPostId(), reqDto.getPosition());
    }

    @PostMapping("/party/applicant/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PartyCommunityApplicantDto>> getApplyParty(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<Long> postIds
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        return ResponseEntity.ok(partyCommunityService.getApplicantList(puuid, postIds));
    }

    @PostMapping("/party/my/applicant/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PartyCommunityApplicantDto>> getApplyParty(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        return ResponseEntity.ok(partyCommunityService.getApplicantList(puuid));
    }

    @PostMapping("/party/approve")
    @PreAuthorize("isAuthenticated()")
    public void approve(
            @RequestBody @Valid PartyCommunityApproveReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.updateApplicantStatus(reqDto, puuid, true);
    }

    @PostMapping("/party/reject")
    @PreAuthorize("isAuthenticated()")
    public void reject(
            @RequestBody @Valid PartyCommunityApproveReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.updateApplicantStatus(reqDto, puuid, false);
    }

    @PostMapping("/party/leave")
    @PreAuthorize("isAuthenticated()")
    public void leave(
            @RequestBody @Valid PartyCommunityLeaveReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.leaveMyParty(reqDto.getPostId(), puuid);
    }

    @PostMapping("/party/close/{postId}")
    @PreAuthorize("isAuthenticated()")
    public void close(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.updatePartyStatus(postId, puuid, "CLOSE");
    }

    @PostMapping("/party/cancel/{postId}")
    @PreAuthorize("isAuthenticated()")
    public void cancel(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        partyCommunityService.updatePartyStatus(postId, puuid, "CANCEL");
    }

    @GetMapping("/party/history/hosted")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagingDto<PartyCommunityHistoryDto>> getPartyHostedHistory(
            @Valid PartyCommunityHistoryReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();

        int totalCount = partyCommunityService.getPartyHostedHistoryCount(puuid);
        List<PartyCommunityHistoryDto> historyDtoList = partyCommunityService.getPartyHostedHistory(puuid, reqDto.getPage(), reqDto.getLimit());
        PagingDto<PartyCommunityHistoryDto> responseDto = new PagingDto<>(
                historyDtoList,
                reqDto.getPage(),
                reqDto.getLimit(),
                totalCount,
                DISPLAY_PAGE_COUNT
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/party/history/participated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagingDto<PartyCommunityHistoryDto>> getPartyParticipatedHistory(
            PartyCommunityHistoryReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();

        int totalCount = partyCommunityService.getPartyParticipatedHistoryCount(puuid);
        List<PartyCommunityHistoryDto> historyDtoList = partyCommunityService.getPartyParticipatedHistory(puuid, reqDto.getPage(), reqDto.getLimit());
        PagingDto<PartyCommunityHistoryDto> responseDto = new PagingDto<>(
                historyDtoList,
                reqDto.getPage(),
                reqDto.getLimit(),
                totalCount,
                DISPLAY_PAGE_COUNT
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/party/history/applied")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagingDto<PartyCommunityAppliedHistoryDto>> getPartyAppliedHistory(
            PartyCommunityHistoryReqDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();

        int totalCount = partyCommunityService.getPartyAppliedHistoryCount(puuid);
        List<PartyCommunityAppliedHistoryDto> historyDtoList = partyCommunityService.getPartyAppliedHistory(puuid, reqDto.getPage(), reqDto.getLimit());
        PagingDto<PartyCommunityAppliedHistoryDto> responseDto = new PagingDto<>(
                historyDtoList,
                reqDto.getPage(),
                reqDto.getLimit(),
                totalCount,
                DISPLAY_PAGE_COUNT
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/party/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MyPartyDto> getMyParty(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String puuid = ((UserDetailImpl) userDetails).getPuuid();
        return ResponseEntity.ok(partyCommunityService.getMyActivePartyPost(puuid));
    }

}
