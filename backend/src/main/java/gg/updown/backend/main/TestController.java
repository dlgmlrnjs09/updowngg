package gg.updown.backend.main;

import gg.updown.backend.config.JasyptConfigDES;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.external.riot.api.lol.match.service.RiotMatchRawService;
import gg.updown.backend.main.api.community.duo.service.DuoCommunityService;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.riot.account.service.RiotAccountService;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private final AccountApiService accountApiService;
    private final RiotAccountService riotAccountService;
    private final DdragonService ddragonService;
    private final LolMatchService matchService;
    private final JasyptConfigDES jasyptConfig;
    private final DuoCommunityService duoCommunityService;
    private final RiotMatchRawService rawService;

    @GetMapping("/test")
    public void test() {
        rawService.syncArenaAugmentData("ko_kr");
    }

    @GetMapping("/encodeing")
    public void getJasyptEncoding(String value) {
        System.out.println(jasyptEncoding(value));
    }

    public String jasyptEncoding(String value) {
        String key = "";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
