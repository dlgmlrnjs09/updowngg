package gg.updown.backend.main;

import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/test/")
public class TestController {
    private final AccountApiService accountApiService;

    @RequestMapping("test")
    public void test() {
        System.out.println(accountApiService.getAccountInfoByRiotId("조용조용조용필", "KR2"));
    }
}
