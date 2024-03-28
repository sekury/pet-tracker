package pet.pettracker.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public sealed abstract class Pet permits Cat, Dog {

    @Id
    private String id;
    private Long ownerId;
    private Boolean inZone;
    private String petType;

    protected Pet(String petType) {
        this.petType = petType;
    }
}
