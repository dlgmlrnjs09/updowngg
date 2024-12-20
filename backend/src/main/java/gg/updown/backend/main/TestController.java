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
    }

    @GetMapping("/jasyptEncoding")
    public void getJasyptEncoding(String value) {
        System.out.println(jasyptEncoding(value));
    }

    public String jasyptEncoding(String value) {

        String key = "gmlrnjs0";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
