package pet.pettracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pet.pettracker.model.dto.TrackerDto;
import pet.pettracker.model.dto.TrackersStatsDto;
import pet.pettracker.service.PetTrackerService;

@RestController
@RequestMapping("/api/v1/trackers")
@RequiredArgsConstructor
public class TrackerController {

    private final PetTrackerService petTrackerService;

    @GetMapping(
            path = "stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrackersStatsDto getStatistics() {
        return petTrackerService.getStatistics();
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrackerDto getTracker(@PathVariable("id") String id) {
        return petTrackerService.getTracker(id);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<TrackerDto> getTrackers(Pageable pageable) {
        return petTrackerService.getTrackers(pageable);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TrackerDto createTracker(@RequestBody @Valid TrackerDto request) {
        return petTrackerService.createTracker(request);
    }

    @PutMapping(
            path = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrackerDto updateTracker(@PathVariable("id") String id, @RequestBody @Valid TrackerDto request) {
        return petTrackerService.updateTracker(id, request);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTracker(@PathVariable("id") String id) {
        petTrackerService.deleteTracker(id);
    }

}
