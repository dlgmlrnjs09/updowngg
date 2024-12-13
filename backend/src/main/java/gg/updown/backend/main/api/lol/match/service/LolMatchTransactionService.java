package gg.updown.backend.main.api.lol.match.service;

import gg.updown.backend.main.api.lol.match.mapper.LolMatchMapper;
import gg.updown.backend.main.api.lol.match.model.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.LolMatchParticipantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LolMatchTransactionService {
    private final LolMatchMapper lolMatchMapper;

    @Transactional
    public void saveMatchWithParticipants(LolMatchEntity match, List<LolMatchParticipantEntity> participants) {
        lolMatchMapper.insertMatch(match);
        lolMatchMapper.insertMatchParticipants(participants);
    }
}
