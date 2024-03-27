package pet.pettracker.service;

import org.awaitility.Durations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import pet.pettracker.model.enums.CatTrackerType;

import static org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/insert_pets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class CatTrackerServiceTest {

    @Autowired
    CatTrackerService catTrackerService;

    @Test
    public void whenSearchCatsOutside_thenGetTrackerCountMap() throws Exception {
        var future = catTrackerService.getCountOutsideZone();

        await()
                .atMost(Durations.ONE_SECOND)
                .until(future::isDone);

        var result = future.get();

        Assertions.assertEquals(1L, result.get(CatTrackerType.L));
        Assertions.assertEquals(1L, result.get(CatTrackerType.S));
    }
}