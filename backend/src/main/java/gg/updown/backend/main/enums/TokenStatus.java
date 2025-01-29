package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum TokenStatus {
    VALID,
    EXPIRED,
    INVALID,
    BLACKLISTED
}