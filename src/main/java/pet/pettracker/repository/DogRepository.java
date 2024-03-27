package pet.pettracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pet.pettracker.model.entity.Cat;
import pet.pettracker.model.projection.DogCountProjection;

import java.util.List;

public interface DogRepository extends JpaRepository<Cat, Long> {

    @Query("""
            select new pet.pettracker.model.projection.DogCountProjection(d.trackerType, count(d))
            from Dog d where d.inZone = false group by d.trackerType""")
    List<DogCountProjection> countByInZoneFalse();
}
