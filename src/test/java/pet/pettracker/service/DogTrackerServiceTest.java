package pet.pettracker.service;

import org.awaitility.Durations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import pet.pettracker.model.enums.CatTrackerType;
import pet.pettracker.model.enums.DogTrackerType;

import static org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/insert_pets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class DogTrackerServiceTest {

    @Autowired
    DogTrackerService dogTrackerService;

    @Test
    public void whenSearchDogsOutside_thenGetTrackerCountMap() throws Exception {
        var future = dogTrackerService.getCountOutsideZone();

        await()
                .atMost(Durations.ONE_SECOND)
                .until(future::isDone);

        var result = future.get();

        Assertions.assertEquals(1L, result.get(DogTrackerType.L));
        Assertions.assertEquals(1L, result.get(DogTrackerType.M));
        Assertions.assertEquals(1L, result.get(DogTrackerType.S));
    }
}