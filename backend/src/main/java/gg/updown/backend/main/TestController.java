package gg.updown.backend.main;

import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.lol.match.model.LolMatchInfoResDto;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import gg.updown.backend.main.riot.account.service.RiotAccountService;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final AccountApiService accountApiService;
    private final RiotAccountService riotAccountService;
    private final DdragonService ddragonService;
    private final LolMatchService matchService;

    @GetMapping("/test")
    public void test() {
//        AccountInfoResDto resDto = accountApiService.getAccountInfoByRiotId("조용조용조용필", "KR2");
//        riotAccountService.insertAccountBasicInfo(RiotAccountInfoEntity.builder()
//            .puuid(resDto.getPuuid())
//            .gameName(resDto.getGameName())
//            .tagLine(resDto.getTagLine())
//        .build());

        List<LolMatchInfoResDto> list = matchService.getAndInsertMatchList("W8BbbIDV3ctMEWIklMFx0sFk6yXmTfBfuLYUdglXhwG9LPdrxc5ITkDCxaOo_vJKbWm3haDdPlDNBw", 1, 20);
        System.out.println("");
    }
}
