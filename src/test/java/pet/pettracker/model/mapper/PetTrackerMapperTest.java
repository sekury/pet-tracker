package pet.pettracker.model.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pet.pettracker.model.dto.CatTrackerDto;
import pet.pettracker.model.dto.DogTrackerDto;
import pet.pettracker.model.dto.TrackerDto;
import pet.pettracker.model.entity.Cat;
import pet.pettracker.model.entity.Dog;
import pet.pettracker.model.entity.Pet;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTrackerMapperTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsForInferringPetClass")
    void givenTracker_whenInferringPetClass_thenInferPetClass(TrackerDto dto, Class<?> expected) {
        var mapper = new PetTrackerMapper(null);
        var petClass = mapper.inferPetClass(dto);
        assertEquals(expected, petClass);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForInferringTrackerClass")
    void givenPet_whenInferringTrackingClass_thenInferTrackClass(Pet pet, Class<?> expected) {
        var mapper = new PetTrackerMapper(null);
        var trackerClass = mapper.inferTrackerClass(pet);
        assertEquals(expected, trackerClass);
    }

    private static Stream<Arguments> provideArgumentsForInferringPetClass() {
        return Stream.of(
                Arguments.of(new CatTrackerDto(), Cat.class),
                Arguments.of(new DogTrackerDto(), Dog.class)
        );
    }

    private static Stream<Arguments> provideArgumentsForInferringTrackerClass() {
        return Stream.of(
                Arguments.of(new Cat(), CatTrackerDto.class),
                Arguments.of(new Dog(), DogTrackerDto.class)
        );
    }
}