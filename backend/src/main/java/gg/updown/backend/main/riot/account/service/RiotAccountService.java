package gg.updown.backend.main.riot.account.service;

import gg.updown.backend.main.riot.account.mapper.RiotAccountMapper;
import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiotAccountService {
    private final RiotAccountMapper riotAccountMapper;

    public void insertAccountBasicInfo(RiotAccountInfoEntity entity) {
        riotAccountMapper.insertAccountBasicInfo(entity);
    }

    public void conflictAccountBasicInfo(RiotAccountInfoEntity entity) {
        riotAccountMapper.conflictAccountBasicInfo(entity);
    }

}
