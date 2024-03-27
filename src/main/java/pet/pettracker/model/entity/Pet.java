package pet.pettracker.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pet")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "pet_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public abstract class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    @NotNull
    private Long ownerId;

    @Column(name = "in_zone", nullable = false)
    @NotNull
    private Boolean inZone; // TODO consider bitmap index
}
