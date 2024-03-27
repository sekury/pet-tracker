package pet.pettracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.pettracker.model.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {


}
