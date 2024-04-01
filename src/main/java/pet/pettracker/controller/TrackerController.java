package pet.pettracker.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Calculates number of trackers outside the power safe zone, grouped by tracker type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping(
            path = "stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = "trackers.statistics.time", description = "Time taken to return trackers statistics")
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
