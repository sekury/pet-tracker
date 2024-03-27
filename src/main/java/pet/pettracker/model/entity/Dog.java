package pet.pettracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pet.pettracker.model.enums.DogTrackerType;

@Entity
@Table(name = "dog")
@DiscriminatorValue("DOG")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class Dog extends Pet {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tracker_type", nullable = false)
    @NotNull
    private DogTrackerType trackerType;
}
