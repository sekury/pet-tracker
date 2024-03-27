package pet.pettracker.model.projection;

import pet.pettracker.model.enums.DogTrackerType;

public record DogCountProjection(DogTrackerType trackerType, long count) {
}
