package gg.updown.backend.main.api.lol.summoner.controller;

import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerProfileReqDto;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerProfileResDto;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/summoner")
@Tag(name = "User", description = "LOL, Riot 계정 관련 API")
public class LolSummonerController {

    private final LolSummonerService summonerService;
    private final AccountApiService accountApiService;

    @Operation(summary = "라이엇 닉네임/태그로 사용자 조회", description = "라이엇 계정정보, LOL 소환사 정보 조회")
    @GetMapping("/info/by-summoner")
    public ResponseEntity<LolSummonerProfileResDto> getInfo(@Valid @ParameterObject LolSummonerProfileReqDto reqDto) {
        LolSummonerProfileResDto resDto = summonerService.conflictSummonerInfo(reqDto.getSummonerId(), reqDto.getTagLine());
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }

    @Operation(summary = "puuid로 사용자 조회", description = "라이엇 계정정보, LOL 소환사 정보 조회")
    @GetMapping("/info/by-puuid")
    public ResponseEntity<LolSummonerProfileResDto> getInfo(@Parameter(description = "라이엇 계정 고유ID") @RequestParam String puuid) {
        LolSummonerProfileResDto resDto = summonerService.conflictSummonerInfo(puuid);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }
}
