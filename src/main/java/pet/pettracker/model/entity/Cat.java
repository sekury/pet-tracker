package pet.pettracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pet.pettracker.model.enums.CatTrackerType;

@Entity
@Table(name = "cat")
@DiscriminatorValue("CAT")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class Cat extends Pet {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tracker_type", nullable = false)
    @NotNull
    private CatTrackerType trackerType;

    @Column(name = "lost_tracker", nullable = false)
    @NotNull
    private Boolean lostTracker;
}
