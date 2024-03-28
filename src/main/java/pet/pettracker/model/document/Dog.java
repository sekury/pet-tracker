package pet.pettracker.model.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import pet.pettracker.model.enums.DogTrackerType;

@Document(collection = "pet")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Dog extends Pet {

    public Dog() {
        super("Dog");
    }

    private DogTrackerType trackerType;
}


