package gg.updown.backend.main.api.lol.match.controller;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoReqDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoResDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchTogetherReqDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchUpdateReqDto;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Match", description = "LOL 경기 관련 API")
@RequestMapping("/api/v1/match")
public class LolMatchController {
    @Value("${match.basic.start-time}")
    private String basicStartTime;

    private final LolMatchService lolMatchService;

    @Operation(summary = "LOL 경기목록 조회", description = "LOL 경기목록 및 리뷰작성내역 조회")
    @GetMapping("/list")
    public ResponseEntity<List<LolMatchInfoResDto>> getMatchList(
            @Valid @ParameterObject LolMatchInfoReqDto reqDto,
            @AuthenticationPrincipal UserDetailImpl userDetail
    ) {
        List<LolMatchInfoResDto> responseDto = lolMatchService.getAndInsertMatchList(reqDto.getPuuid(), reqDto.getStartIndex(), reqDto.getCount(), userDetail);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "LOL 경기 단건조회", description = "LOL 경기 및 리뷰작성내역 조회")
    @GetMapping("/{matchId}")
    public ResponseEntity<LolMatchInfoResDto> getMatchInfo(@PathVariable String matchId, @AuthenticationPrincipal UserDetailImpl userDetail) {
        return ResponseEntity.status(HttpStatus.OK).body(lolMatchService.getMatchResDto(matchId, userDetail.getPuuid()));
    }

    @Operation(summary = "LOL 경기목록 갱신", description = "라이엇 계정정보, LOL 소환사 정보 조회")
    @ApiResponse(description = "LOL 경기 고유ID 목록", content = @Content(schema = @Schema(implementation = List.class)))
    @GetMapping("/update")
    public ResponseEntity<List<String>> updateMatchListV2(LolMatchUpdateReqDto reqDto) {
        Long startDate = DateUtil.yyyyMMddToMilliseconds(basicStartTime);
        Long endDate = DateUtil.getCurrentTimeMillis();
        List<String> responseDto = lolMatchService.getAndInsertMatchIdList(reqDto.getPuuid(), startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "기간 내 함께 플레이한 게임 유무 조회", description = "로그인한 사용자와 특정 대상이 기간 내 함께 플레이한 게임이 있는지에 대한 유무 조회")
    @GetMapping("/played-together")
    public ResponseEntity<Boolean> checkPlayedTogether(@Valid LolMatchTogetherReqDto reqDto, @AuthenticationPrincipal UserDetailImpl userDetail) {
        return ResponseEntity.ok(lolMatchService.hasPlayedTogether(userDetail.getPuuid(), reqDto));
    }

    @Operation(summary = "기간 내 함께 플레이한 최근게임 조회", description = "기간 내 함께 플레이한 최근게임 조회")
    @GetMapping("/played-together/latest")
    public ResponseEntity<LolMatchInfoResDto> getLatestMatchInfoTogether(@Valid LolMatchTogetherReqDto reqDto, @AuthenticationPrincipal UserDetailImpl userDetail) {
        return ResponseEntity.ok(lolMatchService.getLatestMatchInfoTogether(userDetail.getPuuid(), reqDto));
    }
}
