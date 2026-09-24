package org.example.scoutingsys.repository;

import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCountryName(String countryName);
    List<Country> findAllByContinent(String continent);
    Optional<Country> findAllByPlayersId(Long playerId);
}