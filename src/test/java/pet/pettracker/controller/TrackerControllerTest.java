package pet.pettracker.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import pet.pettracker.model.dto.CatTrackerDto;
import pet.pettracker.model.dto.DogTrackerDto;
import pet.pettracker.model.enums.CatTrackerType;
import pet.pettracker.model.enums.DogTrackerType;
import pet.pettracker.service.CatTrackerService;
import pet.pettracker.service.DogTrackerService;
import pet.pettracker.service.PetTrackerService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/insert_pets.sql"})
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TrackerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    PetTrackerService petTrackerService;

    @MockBean
    CatTrackerService catTrackerService;

    @MockBean
    DogTrackerService dogTrackerService;

    @Test
    public void givenCat_whenRequestById_thenReturnCatTracker() throws Exception {
        mockMvc.perform(get("/api/v1/trackers/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.petType", equalTo("cat")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker", equalTo(false)))
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker(1L);
    }

    @Test
    public void givenDog_whenRequestById_thenReturnDogTracker() throws Exception {
        mockMvc.perform(get("/api/v1/trackers/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(5)))
                .andExpect(jsonPath("$.petType", equalTo("dog")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker").doesNotExist())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker(5L);
    }

    @Test
    public void givenNoPet_whenRequestById_thenTrackerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trackers/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker(100L);
    }

    @Test
    public void givenPets_whenRequestPage_thenReturnPage() throws Exception {
        mockMvc.perform(get("/api/v1/trackers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", equalTo(10)))
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTrackers(any(Pageable.class));
    }

    @Test
    public void givenPets_whenRequestStats_thenReturnTrackersStats() throws Exception {
        Mockito.when(catTrackerService.getCountOutsideZone()).thenReturn(
                CompletableFuture.completedFuture(Map.of(CatTrackerType.L, 2L, CatTrackerType.S, 1L)));
        Mockito.when(dogTrackerService.getCountOutsideZone()).thenReturn(
                CompletableFuture.completedFuture(Map.of(DogTrackerType.L, 3L, DogTrackerType.M, 2L, DogTrackerType.S, 1L)));

        mockMvc.perform(get("/api/v1/trackers/stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"out":{"cats":{"L":2,"S":1},"dogs":{"L":3,"M":2,"S":1}}}
                        """))
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getStatistics();
    }

    @Test
    public void givenCatTrackerRequest_whenCreateCatTracker_thenCreateCat() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"L","lostTracker":false}
                """;

        mockMvc.perform(post("/api/v1/trackers").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(11)))
                .andExpect(jsonPath("$.petType", equalTo("cat")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker", equalTo(false)))
                .andDo(print());

        var argument = ArgumentCaptor.forClass(CatTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).createTracker(argument.capture());
        var tracker = argument.getValue();

        assertAll(() -> {
            assertNull(tracker.getId());
            assertEquals(1L, tracker.getOwnerId());
            assertEquals(CatTrackerType.L, tracker.getTrackerType());
            assertTrue(tracker.getInZone());
            assertFalse(tracker.getLostTracker());
        });
    }

    @Test
    public void givenDogTrackerRequest_whenCreateDogTracker_thenCreateDog() throws Exception {
        var request = """
                {"petType":"dog","ownerId":1,"inZone":true,"trackerType":"M"}
                """;

        mockMvc.perform(post("/api/v1/trackers").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(11)))
                .andExpect(jsonPath("$.petType", equalTo("dog")))
                .andExpect(jsonPath("$.trackerType", equalTo("M")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker").doesNotExist())
                .andDo(print());

        var argument = ArgumentCaptor.forClass(DogTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).createTracker(argument.capture());
        var tracker = argument.getValue();

        assertAll(() -> {
            assertNull(tracker.getId());
            assertEquals(1L, tracker.getOwnerId());
            assertEquals(DogTrackerType.M, tracker.getTrackerType());
            assertTrue(tracker.getInZone());
        });
    }

    @Test
    public void givenInvalidCatTrackerRequest_whenCreateCatTracker_thenBadRequest() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"M"}
                """;

        mockMvc.perform(post("/api/v1/trackers").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.never()).createTracker(any());
    }

    @Test
    public void givenInvalidDogTrackerRequest_whenCreateDogTracker_thenBadRequest() throws Exception {
        var request = """
                {"petType":"dog","ownerId":1,"inZone":true,"trackerType":"XXL"}
                """;

        mockMvc.perform(post("/api/v1/trackers").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.never()).createTracker(any());
    }

    @Test
    public void givenCatTrackerRequest_whenUpdateCatTracker_thenUpdateCat() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/1").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.petType", equalTo("cat")))
                .andExpect(jsonPath("$.trackerType", equalTo("S")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker", equalTo(true)))
                .andDo(print());

        var argument = ArgumentCaptor.forClass(CatTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(1L), argument.capture());
        var tracker = argument.getValue();

        assertAll(() -> {
            assertNull(tracker.getId());
            assertEquals(1L, tracker.getOwnerId());
            assertEquals(CatTrackerType.S, tracker.getTrackerType());
            assertTrue(tracker.getInZone());
            assertTrue(tracker.getLostTracker());
        });
    }

    @Test
    public void givenNoPet_whenUpdateCatTracker_thenCatNotFound() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/100").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(100L), any());
    }

    @Test
    public void givenDogTrackerRequest_whenUpdateDogTracker_thenUpdateDog() throws Exception {
        var request = """
                {"petType":"dog","ownerId":200,"inZone":false,"trackerType":"L"}
                """;

        mockMvc.perform(put("/api/v1/trackers/5").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(5)))
                .andExpect(jsonPath("$.petType", equalTo("dog")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(200)))
                .andExpect(jsonPath("$.inZone", equalTo(false)))
                .andExpect(jsonPath("$.lostTracker").doesNotExist())
                .andDo(print());

        var argument = ArgumentCaptor.forClass(DogTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(5L), argument.capture());
        var tracker = argument.getValue();

        assertAll(() -> {
            assertNull(tracker.getId());
            assertEquals(200L, tracker.getOwnerId());
            assertEquals(DogTrackerType.L, tracker.getTrackerType());
            assertFalse(tracker.getInZone());
        });
    }

    @Test
    public void givenCatTrackerRequest_whenUpdateDogTracker_thenConflict() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/5").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(5L), any());
    }

    @Test
    public void givenDogTrackerRequest_whenUpdateCatTracker_thenConflict() throws Exception {
        var request = """
                {"petType":"dog","ownerId":200,"inZone":false,"trackerType":"L"}
                """;

        mockMvc.perform(put("/api/v1/trackers/1").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(1L), any());
    }

    @Test
    public void givenCat_whenDeleteCatTracker_thenCatDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/trackers/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}