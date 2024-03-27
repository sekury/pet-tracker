package pet.pettracker.model.projection;

import pet.pettracker.model.enums.CatTrackerType;

public record CatCountProjection(CatTrackerType trackerType, long count) {
}
