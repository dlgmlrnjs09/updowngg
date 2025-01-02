package gg.updown.backend.main.api.lol.summoner.controller;

import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerProfileReqDto;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerProfileResDto;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/summoner")
public class LolSummonerController {

    private final LolSummonerService summonerService;
    private final AccountApiService accountApiService;

    @GetMapping("/info")
    public ResponseEntity<LolSummonerProfileResDto> getInfo(@Valid LolSummonerProfileReqDto reqDto) {
        LolSummonerProfileResDto resDto = summonerService.conflictSummonerInfo(reqDto.getSummonerId(), reqDto.getTagLine());
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }
}
