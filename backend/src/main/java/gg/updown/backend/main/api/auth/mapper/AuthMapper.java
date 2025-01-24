package gg.updown.backend.main.api.auth.mapper;

import gg.updown.backend.main.api.auth.model.DiscordAccountEntity;
import gg.updown.backend.main.api.auth.model.SiteAccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {
    SiteAccountEntity getSiteAccountByEmail(String email);
    SiteAccountEntity getSiteAccountByPuuid(String puuid);
    boolean isExistDuplicateEmail(String email);
    void insertSiteAccount(
            @Param("puuid")String puuid,
            @Param("memberEmail")String memberEmail,
            @Param("memberPassword") String memberPassword
    );
    DiscordAccountEntity getDiscordAccountBySiteCode(Long siteCode);
    void conflictDiscordAccount(@Param("siteCode") long siteCode, @Param("entity") DiscordAccountEntity entity);
    void disconnectDiscordAccount(long siteCode);
}
