package pet.pettracker.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pet.pettracker.model.enums.CatTrackerType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class CatTrackerDto extends TrackerDto {

    @NotNull
    private CatTrackerType trackerType;

    @NotNull
    private Boolean lostTracker;
}

