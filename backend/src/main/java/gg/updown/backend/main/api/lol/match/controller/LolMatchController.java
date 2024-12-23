package gg.updown.backend.main.api.lol.match.controller;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoReqDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoResDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchUpdateReqDto;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorMessage;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class LolMatchController {
    private final LolMatchService lolMatchService;

    @GetMapping("/list")
    public ResponseEntity<List<LolMatchInfoResDto>> getMatchList(@Valid LolMatchInfoReqDto reqDto, @AuthenticationPrincipal UserDetailImpl userDetail) {
        List<LolMatchInfoResDto> responseDto = lolMatchService.getAndInsertMatchList(reqDto.getPuuid(), reqDto.getStartIndex(), reqDto.getCount(), userDetail);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/update")
    public ResponseEntity<List<String>> updateMatchListV2(LolMatchUpdateReqDto reqDto) {
        Long startDate = DateUtil.yyyyMMddToMilliseconds("2024-01-01 00:00");
        Long endDate = DateUtil.getCurrentTimeMillis();
        List<String> responseDto = lolMatchService.getAndInsertMatchIdList(reqDto.getPuuid(), startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
