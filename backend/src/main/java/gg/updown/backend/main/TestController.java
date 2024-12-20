package gg.updown.backend.main;

import gg.updown.backend.config.JasyptConfigDES;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.riot.account.service.RiotAccountService;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final AccountApiService accountApiService;
    private final RiotAccountService riotAccountService;
    private final DdragonService ddragonService;
    private final LolMatchService matchService;
    private final JasyptConfigDES jasyptConfig;

    @GetMapping("/test")
    public void test() {
//        AccountInfoResDto resDto = accountApiService.getAccountInfoByRiotId("조용조용조용필", "KR2");
//        riotAccountService.insertAccountBasicInfo(RiotAccountInfoEntity.builder()
//            .puuid(resDto.getPuuid())
//            .gameName(resDto.getGameName())
//            .tagLine(resDto.getTagLine())
//        .build());

//        List<LolMatchInfoResDto> list = matchService.getAndInsertMatchList("W8BbbIDV3ctMEWIklMFx0sFk6yXmTfBfuLYUdglXhwG9LPdrxc5ITkDCxaOo_vJKbWm3haDdPlDNBw", 1, 20);
//        System.out.println("");

        System.out.println("db-url = " + jasyptEncoding("jdbc:log4jdbc:postgresql://13.124.117.180:5432/updowngg"));
        System.out.println("db-username = " + jasyptEncoding("dlgmlrnjs09"));
        System.out.println("db-password = " + jasyptEncoding("fkflfk123"));
        System.out.println("riot-api-key = " + jasyptEncoding("RGAPI-84f64606-b562-4110-b337-c47c6aa105dd"));
        System.out.println("jwt-secret = " + jasyptEncoding("e4f569456859c1789f80184d8c89bce1e04a9e3c0321aa166b3bde52b69fcc1d"));
    }

    public String jasyptEncoding(String value) {

        String key = "gmlrnjs0";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
