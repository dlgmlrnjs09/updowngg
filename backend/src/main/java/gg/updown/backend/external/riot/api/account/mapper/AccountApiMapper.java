package gg.updown.backend.external.riot.api.account.mapper;

import gg.updown.backend.external.riot.api.account.model.AccountInfoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountApiMapper {
    void insertAccountInfo(AccountInfoEntity entity);
}
