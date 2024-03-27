package pet.pettracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pet.pettracker.model.entity.Cat;
import pet.pettracker.model.projection.CatCountProjection;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat, Long> {

    @Query("""
            select new pet.pettracker.model.projection.CatCountProjection(c.trackerType, count(c))
            from Cat c where c.inZone = false group by c.trackerType""")
    List<CatCountProjection> countByInZoneFalse();
}
