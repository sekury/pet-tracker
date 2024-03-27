package pet.pettracker.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "petType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CatTrackerDto.class, name = "cat"),
        @JsonSubTypes.Type(value = DogTrackerDto.class, name = "dog")
})
@Data
@SuperBuilder
@NoArgsConstructor
public abstract sealed class TrackerDto permits CatTrackerDto, DogTrackerDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private Long ownerId;

    @NotNull
    private Boolean inZone;
}
