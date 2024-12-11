package gg.updown.backend.main.api.lol.summoner.controller;

import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerProfileDto;
import gg.updown.backend.main.api.lol.summoner.model.LolSummonerProfileResDto;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/summoner")
public class LolSummonerController {

    private final LolSummonerService summonerService;
    private final AccountApiService accountApiService;

    @GetMapping("/info")
    public ResponseEntity<LolSummonerProfileResDto> getInfo(@RequestParam String summonerId, @RequestParam String tagLine) {
        LolSummonerProfileResDto resDto = summonerService.conflictSummonerInfo(summonerId, tagLine);
        return ResponseEntity.status(HttpStatus.OK).body(resDto);
    }
}
