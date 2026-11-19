package org.example.scoutingsys.repository;

import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByManagerId(Long managerId);
    Optional<Club> findByClubName(String name);
    List<Club> findAllByLeagueId(Long leagueId);

}
