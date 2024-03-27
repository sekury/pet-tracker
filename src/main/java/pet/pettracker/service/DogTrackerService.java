package pet.pettracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pet.pettracker.model.enums.DogTrackerType;
import pet.pettracker.model.projection.DogCountProjection;
import pet.pettracker.repository.DogRepository;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DogTrackerService {

    private final DogRepository dogRepository;

    @Async
    public CompletableFuture<Map<DogTrackerType, Long>> getCountOutsideZone() {
        log.error("Getting dogs outside power safe zone");
        var map = dogRepository.countByInZoneFalse().stream()
                .collect(Collectors.toUnmodifiableMap(DogCountProjection::trackerType, DogCountProjection::count));
        return CompletableFuture.completedFuture(map);
    }
}
