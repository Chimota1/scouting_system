package org.example.scoutingsys.repository;

import org.example.scoutingsys.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    Optional<League> findByLeagueName(String leagueName);
    List<League> findAllByCountryId(Long countryId);
    Optional<League> findByClubsId(Long clubId);
}