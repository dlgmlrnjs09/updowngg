package gg.updown.backend.main.riot.ddragon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaAugmentEntity {
    private Integer id;
    private String apiName;
    private String name;
    private String description;
    private String cleanDescription;
    private String tooltip;
    private Integer rarityId;
    private String iconLarge;
    private String iconLargeUrl;
    private String iconSmall;
    private String iconSmallUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}