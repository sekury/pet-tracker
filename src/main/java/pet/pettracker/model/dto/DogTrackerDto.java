package pet.pettracker.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pet.pettracker.model.enums.DogTrackerType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class DogTrackerDto extends TrackerDto {

    @NotNull
    private DogTrackerType trackerType;
}

