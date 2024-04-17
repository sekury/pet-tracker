package pet.pettracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pet.pettracker.model.enums.CatTrackerType;
import pet.pettracker.model.projection.CatCountProjection;
import pet.pettracker.repository.CatRepository;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatTrackerService {

    private final CatRepository catRepository;

    @Async
    public CompletableFuture<Map<CatTrackerType, Long>> getCountOutsideZone() {
        log.info("Getting cats outside power safe zone");
        var map = catRepository.countByInZoneFalse().stream()
                .collect(Collectors.toUnmodifiableMap(CatCountProjection::trackerType, CatCountProjection::count));
        return CompletableFuture.completedFuture(map);
    }
}
