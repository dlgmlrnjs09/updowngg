package gg.updown.backend.main.api.community.party.service;

import gg.updown.backend.main.api.community.common.service.CommunityService;
import gg.updown.backend.main.api.community.party.mapper.PartyCommunityMapper;
import gg.updown.backend.main.api.community.party.model.PartyCommunityApplicantEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityParticipantEntity;
import gg.updown.backend.main.enums.SitePartyApplyStatus;
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        partyCommunityEntity.setPostStatus("OPEN");
        communityService.insertPost(communityCode, partyCommunityEntity);
        partyCommunityMapper.insertPartyPost(partyCommunityEntity);
        participantEntity.setPostId(partyCommunityEntity.getPostId());
        partyCommunityMapper.insertPartyParticipant(participantEntity);
    }

    @Transactional
    public void applyPartyCommunityPost(long postId, String position, String puuid) {
        PartyCommunityParticipantEntity participantEntity = partyCommunityMapper.selectParticipantWithLock(postId);
        if (!this.isPositionAvailable(participantEntity, position)) {
            // 마감된 포지션
            throw new SiteCommonException(
                HttpStatus.CONFLICT,
                SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage(),
                SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage(),
                SiteErrorMessage.ALREADY_PARTY_POSITION.getMessage()
            );
        }

        long applicantSeq = partyCommunityMapper.getNextApplicantSeq(postId);

        partyCommunityMapper.insertPartyApplicant(PartyCommunityApplicantEntity.builder()
                        .postId(postId)
                        .position(position)
                        .applicantPuuid(puuid)
                        .applicantSeq(applicantSeq)
                        .applyStatus(SitePartyApplyStatus.PENDING.getStatus())
                .build()
        );
    }

    @Transactional
    public void updateApplicantAndParticipant(long postId, long applicantSeq, boolean isApprove) {
        String approveStatus = isApprove ? SitePartyApplyStatus.APPROVE.getStatus() : SitePartyApplyStatus.REJECT.getStatus();
        // 승인/거절로 상태 변경
        partyCommunityMapper.updateApplicantStatus(postId, applicantSeq, approveStatus);

        if (isApprove) {
            PartyCommunityApplicantEntity applicantEntity = partyCommunityMapper.getApplicant(postId, applicantSeq);
            
            if (applicantEntity == null) {
                throw new SiteCommonException(
                    HttpStatus.NOT_FOUND,
                    SiteErrorMessage.NOT_EXIST_PARTY.getMessage(),
                    SiteErrorMessage.NOT_EXIST_PARTY.getMessage(),
                    "신청 정보를 찾을 수 없습니다."
                );
            }

            // 해당 post의 다른 포지션 신청서 삭제
            partyCommunityMapper.deleteAnotherApplicant(applicantEntity);

            // 참가자 목록 업데이트
            PartyCommunityParticipantEntity sqlParamEntity = new PartyCommunityParticipantEntity();
            sqlParamEntity.setPostId(postId);
            sqlParamEntity.setTopPuuid(applicantEntity.getPosition().equals("TOP") ? applicantEntity.getApplicantPuuid() : null);
            sqlParamEntity.setJunglePuuid(applicantEntity.getPosition().equals("JG") ? applicantEntity.getApplicantPuuid() : null);
            sqlParamEntity.setMidPuuid(applicantEntity.getPosition().equals("MID") ? applicantEntity.getApplicantPuuid() : null);
            sqlParamEntity.setAdPuuid(applicantEntity.getPosition().equals("AD") ? applicantEntity.getApplicantPuuid() : null);
            sqlParamEntity.setSupPuuid(applicantEntity.getPosition().equals("SUP") ? applicantEntity.getApplicantPuuid() : null);

            partyCommunityMapper.updateParticipant(sqlParamEntity);
        }
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
