package pet.pettracker.model.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static pet.pettracker.model.document.Pet.PETS;

@Document(collection = PETS)
@Data
@FieldNameConstants
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public sealed abstract class Pet<T> permits Cat, Dog {

    public final static String PETS = "pets";

    @Id
    private String id;
    private Long ownerId;
    private Boolean inZone;
    private T trackerType;
    private final String petType;
}
