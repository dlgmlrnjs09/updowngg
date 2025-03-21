package gg.updown.backend.main.riot.ddragon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaAugmentDataValueEntity {
    private Long id;
    private Integer augmentId;
    private String key;
    private Double value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}