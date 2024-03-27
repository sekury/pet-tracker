package pet.pettracker.model.dto;

import pet.pettracker.model.enums.CatTrackerType;
import pet.pettracker.model.enums.DogTrackerType;

import java.util.Map;

public record TrackerZoneStats(Map<CatTrackerType, Long> cats, Map<DogTrackerType, Long> dogs) {
}
