package pet.pettracker.model.dto;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public record TrackerTypeCountDto(long numOfTrackers, String petType, String trackerType) {
}
