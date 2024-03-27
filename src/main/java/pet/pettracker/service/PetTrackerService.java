package pet.pettracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pet.pettracker.model.dto.TrackerDto;
import pet.pettracker.model.dto.TrackerZoneStats;
import pet.pettracker.model.dto.TrackersStats;
import pet.pettracker.model.entity.Pet;
import pet.pettracker.model.mapper.PetTrackerMapper;
import pet.pettracker.repository.PetRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetTrackerService {

    private final PetTrackerMapper mapper;
    private final PetRepository repository;
    private final CatTrackerService catTrackerService;
    private final DogTrackerService dogTrackerService;

    public TrackersStats getStatistics() {
        log.info("Getting trackers statistics");
        return new TrackersStats(
                new TrackerZoneStats(
                        catTrackerService.getCountOutsideZone().join(),
                        dogTrackerService.getCountOutsideZone().join()
                ));
    }

    public TrackerDto getTracker(long id) {
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

    public TrackerDto updateTracker(long id, TrackerDto request) {
        log.info("Updating tracker: {}, {}", id, request);
        var pet = updatePet(request, findById(id));
        return mapper.mapToDto(pet);
    }

    public void deleteTracker(long id) {
        log.info("Deleting tracker with id: {}", id);
        repository.delete(findById(id));
    }

    private Pet findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracker not found"));
    }

    private Pet updatePet(TrackerDto request, Pet pet) {
        try {
            mapper.mapToEntity(request, pet);
            return repository.save(pet);
        } catch (IllegalArgumentException e) {
            log.error("Exception occurred while updating pet: {}", pet, e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tracker can't be updated", e);
        }
    }
}
