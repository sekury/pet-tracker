package pet.pettracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pet.pettracker.model.document.Pet;
import pet.pettracker.model.dto.TrackerDto;
import pet.pettracker.model.dto.TrackerTypeCountDto;
import pet.pettracker.model.dto.TrackersStatsDto;
import pet.pettracker.model.mapper.PetTrackerMapper;
import pet.pettracker.repository.PetRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetTrackerService {

    private final PetTrackerMapper mapper;
    private final PetRepository repository;
    private final MongoTemplate mongoTemplate;

    public TrackersStatsDto getStatistics() {
        log.info("Getting trackers statistics");

        var matchOperation = Aggregation.match(new Criteria("inZone").is(false));

        var groupOperation = Aggregation.group(Pet.Fields.petType, Pet.Fields.trackerType)
                .count()
                .as(TrackerTypeCountDto.Fields.numOfTrackers);

        var sortOperation = Aggregation.sort(Sort.by(Pet.Fields.petType, Pet.Fields.trackerType));

        var projectionOperation = Aggregation.project(
                        TrackerTypeCountDto.Fields.numOfTrackers, Pet.Fields.petType, Pet.Fields.trackerType)
                .andExclude("_id");

        var aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortOperation, projectionOperation);

        return new TrackersStatsDto(
                mongoTemplate.aggregateStream(aggregation, Pet.class, TrackerTypeCountDto.class).toList());
    }

    public TrackerDto getTracker(String id) {
        log.info("Getting tracker: {}", id);
        var pet = findById(id);
        return mapper.mapToDto(pet);
    }

    public Page<TrackerDto> getTrackers(Pageable pageable) {
        log.info("Getting trackers: {}", pageable);
        return repository.findAll(pageable).map(mapper::mapToDto);
    }

    public TrackerDto createTracker(TrackerDto request) {
        log.info("Creating tracker: {}", request);
        var pet = repository.save(mapper.mapToEntity(request));
        return mapper.mapToDto(pet);
    }

    public TrackerDto updateTracker(String id, TrackerDto request) {
        log.info("Updating tracker: {}, {}", id, request);
        var pet = updatePet(request, findById(id));
        return mapper.mapToDto(pet);
    }

    public void deleteTracker(String id) {
        log.info("Deleting tracker with id: {}", id);
        repository.delete(findById(id));
    }

    private Pet<?> findById(String id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracker not found"));
    }

    private Pet<?> updatePet(TrackerDto request, Pet<?> pet) {
        try {
            mapper.mapToEntity(request, pet);
            return repository.save(pet);
        } catch (IllegalArgumentException e) {
            log.error("Exception occurred while updating pet: {}", pet, e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tracker can't be updated", e);
        }
    }
}
