package pet.pettracker.model.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pet.pettracker.model.document.Cat;
import pet.pettracker.model.document.Dog;
import pet.pettracker.model.document.Pet;
import pet.pettracker.model.dto.CatTrackerDto;
import pet.pettracker.model.dto.DogTrackerDto;
import pet.pettracker.model.dto.TrackerDto;

@Component
@RequiredArgsConstructor
public class PetTrackerMapper {

    private final ModelMapper modelMapper;

    public Pet mapToEntity(TrackerDto tracker) {
        return modelMapper.map(tracker, inferPetClass(tracker));
    }

    public TrackerDto mapToDto(Pet pet) {
        return modelMapper.map(pet, inferTrackerClass(pet));
    }

    /**
     * Maps the {@code tracker} state to the corresponding {@code pet} entity
     *
     * @param tracker
     * @param pet
     * @throws IllegalArgumentException if {@code tracker} and {@code pet} have incompatible types or are null
     */
    public void mapToEntity(TrackerDto tracker, Pet pet) {
        if (inferPetClass(tracker).isInstance(pet)) {
            modelMapper.map(tracker, pet);
            return;
        }
        throw new IllegalArgumentException("Unexpected pet type: " + pet);
    }

    protected Class<? extends Pet> inferPetClass(TrackerDto tracker) {
        return switch (tracker) {
            case CatTrackerDto ignored -> Cat.class;
            case DogTrackerDto ignored -> Dog.class;
        };
    }

    protected Class<? extends TrackerDto> inferTrackerClass(Pet pet) {
        return switch (pet) {
            case Cat ignored -> CatTrackerDto.class;
            case Dog ignored -> DogTrackerDto.class;
        };
    }
}
