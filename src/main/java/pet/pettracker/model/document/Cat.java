package pet.pettracker.model.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import pet.pettracker.model.enums.CatTrackerType;

@Document(collection = "pet")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Cat extends Pet {

    public Cat() {
        super("Cat");
    }

    private CatTrackerType trackerType;
    private Boolean lostTracker;
}


