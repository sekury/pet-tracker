package pet.pettracker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pet.pettracker.model.document.Pet;

import java.util.Optional;

public interface PetRepository extends MongoRepository<Pet, String> {
    Optional<Pet> findPetDocumentById(String id);
}
