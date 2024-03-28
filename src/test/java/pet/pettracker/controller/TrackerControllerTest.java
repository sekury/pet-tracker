package pet.pettracker.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pet.pettracker.model.document.Cat;
import pet.pettracker.model.document.Dog;
import pet.pettracker.model.document.Pet;
import pet.pettracker.model.dto.CatTrackerDto;
import pet.pettracker.model.dto.DogTrackerDto;
import pet.pettracker.model.enums.CatTrackerType;
import pet.pettracker.model.enums.DogTrackerType;
import pet.pettracker.service.PetTrackerService;

import java.util.List;
import java.util.stream.IntStream;

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
class TrackerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MongoTemplate mongoTemplate;

    @SpyBean
    PetTrackerService petTrackerService;

    @AfterEach
    public void cleanUp() {
        mongoTemplate.dropCollection(Pet.PETS);
    }

    @Test
    public void givenCat_whenRequestById_thenReturnCatTracker() throws Exception {
        var cat = mongoTemplate.insert(new Cat(1L, true, CatTrackerType.L, false));

        mockMvc.perform(get("/api/v1/trackers/" + cat.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(cat.getId())))
                .andExpect(jsonPath("$.petType", equalTo("cat")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker", equalTo(false)))
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker(cat.getId());
    }

    @Test
    public void givenDog_whenRequestById_thenReturnDogTracker() throws Exception {
        var dog = mongoTemplate.insert(new Dog(1L, true, DogTrackerType.L));

        mockMvc.perform(get("/api/v1/trackers/" + dog.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dog.getId())))
                .andExpect(jsonPath("$.petType", equalTo("dog")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker").doesNotExist())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker(dog.getId());
    }

    @Test
    public void givenNoPet_whenRequestById_thenTrackerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trackers/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTracker("100");
    }

    @Test
    public void givenPets_whenRequestPage_thenReturnPage() throws Exception {
        mongoTemplate.insertAll(IntStream.range(0, 10).mapToObj(ignore -> new Cat()).toList());

        mockMvc.perform(get("/api/v1/trackers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", equalTo(10)))
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).getTrackers(any(Pageable.class));
    }

    @Test
    public void givenPets_whenRequestStats_thenReturnTrackersStats() throws Exception {
        mongoTemplate.insertAll(List.of(
                new Cat(1L, false, CatTrackerType.L, false),
                new Cat(1L, false, CatTrackerType.L, false),
                new Cat(1L, false, CatTrackerType.S, false),
                new Dog(1L, false, DogTrackerType.L),
                new Dog(1L, false, DogTrackerType.L),
                new Dog(1L, false, DogTrackerType.L),
                new Dog(1L, false, DogTrackerType.M),
                new Dog(1L, false, DogTrackerType.M),
                new Dog(1L, false, DogTrackerType.S)
        ));

        mockMvc.perform(get("/api/v1/trackers/stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "out":[
                                {"numOfTrackers":2,"petType":"cat","trackerType":"L"},
                                {"numOfTrackers":1,"petType":"cat","trackerType":"S"},
                                {"numOfTrackers":3,"petType":"dog","trackerType":"L"},
                                {"numOfTrackers":2,"petType":"dog","trackerType":"M"},
                                {"numOfTrackers":1,"petType":"dog","trackerType":"S"}
                            ]
                        }
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
                .andExpect(jsonPath("$.id").isNotEmpty())
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
                .andExpect(jsonPath("$.id").isNotEmpty())
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
        var cat = mongoTemplate.insert(new Cat(1L, true, CatTrackerType.L, false));
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/" + cat.getId()).content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(cat.getId())))
                .andExpect(jsonPath("$.petType", equalTo("cat")))
                .andExpect(jsonPath("$.trackerType", equalTo("S")))
                .andExpect(jsonPath("$.ownerId", equalTo(1)))
                .andExpect(jsonPath("$.inZone", equalTo(true)))
                .andExpect(jsonPath("$.lostTracker", equalTo(true)))
                .andDo(print());

        var argument = ArgumentCaptor.forClass(CatTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(cat.getId()), argument.capture());
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
    public void givenNoCat_whenUpdateCatTracker_thenCatNotFound() throws Exception {
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/100").content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq("100"), any());
    }

    @Test
    public void givenDogTrackerRequest_whenUpdateDogTracker_thenUpdateDog() throws Exception {
        var dog = mongoTemplate.insert(new Dog(1L, true, DogTrackerType.L));
        var request = """
                {"petType":"dog","ownerId":200,"inZone":false,"trackerType":"L"}
                """;

        mockMvc.perform(put("/api/v1/trackers/" + dog.getId()).content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dog.getId())))
                .andExpect(jsonPath("$.petType", equalTo("dog")))
                .andExpect(jsonPath("$.trackerType", equalTo("L")))
                .andExpect(jsonPath("$.ownerId", equalTo(200)))
                .andExpect(jsonPath("$.inZone", equalTo(false)))
                .andExpect(jsonPath("$.lostTracker").doesNotExist())
                .andDo(print());

        var argument = ArgumentCaptor.forClass(DogTrackerDto.class);
        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(dog.getId()), argument.capture());
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
        var dog = mongoTemplate.insert(new Dog(1L, true, DogTrackerType.L));
        var request = """
                {"petType":"cat","ownerId":1,"inZone":true,"trackerType":"s","lostTracker":true}
                """;

        mockMvc.perform(put("/api/v1/trackers/" + dog.getId()).content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(dog.getId()), any());
    }

    @Test
    public void givenDogTrackerRequest_whenUpdateCatTracker_thenConflict() throws Exception {
        var cat = mongoTemplate.insert(new Cat(1L, true, CatTrackerType.L, false));
        var request = """
                {"petType":"dog","ownerId":200,"inZone":false,"trackerType":"L"}
                """;

        mockMvc.perform(put("/api/v1/trackers/" + cat.getId()).content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());

        Mockito.verify(petTrackerService, Mockito.only()).updateTracker(eq(cat.getId()), any());
    }

    @Test
    public void givenCat_whenDeleteCatTracker_thenCatDeleted() throws Exception {
        var cat = mongoTemplate.insert(new Cat(1L, true, CatTrackerType.L, false));
        mockMvc.perform(delete("/api/v1/trackers/" + cat.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}