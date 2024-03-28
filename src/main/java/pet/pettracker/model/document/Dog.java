package pet.pettracker.model.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;
import pet.pettracker.model.enums.DogTrackerType;

import static pet.pettracker.model.document.Pet.PETS;

@Document(collection = PETS)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Dog extends Pet<DogTrackerType> {

    public Dog() {
        super("dog");
    }

    public Dog(Long ownerId, Boolean inZone, DogTrackerType trackerType) {
        super(null, ownerId, inZone, trackerType, "dog");
    }

    @PersistenceCreator
    private Dog(String id, String petType, Long ownerId, Boolean inZone, DogTrackerType trackerType) {
        super(id, ownerId, inZone, trackerType, petType);
    }
}


