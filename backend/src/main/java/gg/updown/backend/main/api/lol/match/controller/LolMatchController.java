package gg.updown.backend.main.api.lol.match.controller;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.lol.match.model.LolMatchInfoResDto;
import gg.updown.backend.main.api.lol.match.model.LolMatchParticipantDto;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<LolMatchInfoResDto>> getMatchList(@RequestParam("puuid") String searchPuuid, int startIndex, int count, @AuthenticationPrincipal UserDetailImpl userDetail) {
        List<LolMatchInfoResDto> responseDto = lolMatchService.getAndInsertMatchList(searchPuuid, startIndex, count, userDetail);
        System.out.println("### startIndex = " + startIndex);
        System.out.println("### count = " + count);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/update")
    public ResponseEntity<List<String>> updateMatchListV2(String puuid) {
        Long startDate = DateUtil.yyyyMMddToMilliseconds("2024-01-01 00:00");
        Long endDate = DateUtil.getCurrentTimeMillis();
        List<String> responseDto = lolMatchService.getAndInsertMatchIdList(puuid, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
