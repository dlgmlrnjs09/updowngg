package gg.updown.backend.main.riot.account.mapper;

import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiotAccountMapper {
    void insertAccountBasicInfo(RiotAccountInfoEntity entity);
    void conflictAccountBasicInfo(RiotAccountInfoEntity entity);
}
