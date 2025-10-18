package dev.ace.repo;

import dev.ace.domain.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    @Query("""
        select i from Intervention i where i.startAt < :to and i.endAt > :from order by i.startAt
    """)
    List<Intervention> findOverlapping(OffsetDateTime from, OffsetDateTime to);
}
