package pet.pettracker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pet.pettracker.model.document.Pet;

public interface PetRepository extends MongoRepository<Pet<?>, String> {
}
