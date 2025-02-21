package gg.updown.backend.main.api.community.party.service;

import gg.updown.backend.main.api.community.common.service.CommunityService;
import gg.updown.backend.main.api.community.party.mapper.PartyCommunityMapper;
import gg.updown.backend.main.api.community.party.model.PartyCommunityApplicantEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityParticipantEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyCommunityTransactionService {
    private final CommunityService communityService;
    private final PartyCommunityMapper partyCommunityMapper;

    @Transactional
    public void insertPartyCommunityPost(
            String communityCode,
            PartyCommunityEntity partyCommunityEntity,
            PartyCommunityParticipantEntity participantEntity
    ) {
        communityService.insertPost(communityCode, partyCommunityEntity);
        partyCommunityMapper.insertPartyPost(partyCommunityEntity);
        participantEntity.setPostId(partyCommunityEntity.getPostId());
        partyCommunityMapper.insertPartyParticipant(participantEntity);
    }

    @Transactional
    public boolean applyPartyCommunityPost(long postId, String position, String puuid) {
        PartyCommunityParticipantEntity participantEntity = partyCommunityMapper.selectParticipantWithLock(postId);
        if (!this.isPositionAvailable(participantEntity, position)) {
            // 마감된 포지션
            return false;
        }

        partyCommunityMapper.insertPartyApplicant(PartyCommunityApplicantEntity.builder()
                        .postId(postId)
                        .position(position)
                        .puuid(puuid)
                .build()
        );

        return true;
    }

    private boolean isPositionAvailable(PartyCommunityParticipantEntity participant, String position) {
        return switch (position) {
            case "TOP" -> participant.getTopPuuid() == null;
            case "JG" -> participant.getJunglePuuid() == null;
            case "MID" -> participant.getMidPuuid() == null;
            case "AD" -> participant.getAdPuuid() == null;
            case "SUP" -> participant.getSupPuuid() == null;
            default -> false;
        };
    }
}
