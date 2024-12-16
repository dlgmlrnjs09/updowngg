package gg.updown.backend.main.api.auth.mapper;

import gg.updown.backend.main.api.auth.model.SiteAccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {
    SiteAccountEntity getSiteAccountByEmail(String email);
    boolean isExistDuplicateEmail(String email);
    void insertSiteAccount(
            @Param("puuid")String puuid,
            @Param("memberEmail")String memberEmail,
            @Param("memberPassword") String memberPassword
    );
}
