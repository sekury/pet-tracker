package pet.pettracker.model.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;
import pet.pettracker.model.enums.CatTrackerType;

import static pet.pettracker.model.document.Pet.PETS;


@Document(collection = PETS)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Cat extends Pet<CatTrackerType> {

    private Boolean lostTracker;

    public Cat() {
        super("cat");
    }

    public Cat(Long ownerId, Boolean inZone, CatTrackerType trackerType, Boolean lostTracker) {
        super(null, ownerId, inZone, trackerType, "cat");
        this.lostTracker = lostTracker;
    }

    @PersistenceCreator
    private Cat(String id, String petType, Long ownerId, Boolean inZone, CatTrackerType trackerType, Boolean lostTracker) {
        super(id, ownerId, inZone, trackerType, petType);
        this.lostTracker = lostTracker;
    }
}


