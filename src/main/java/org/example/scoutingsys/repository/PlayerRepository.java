package org.example.scoutingsys.repository;

import org.example.scoutingsys.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findAllByAge(Integer age);
    List<Player> findByGoalsGreaterThan(Integer minGoals);
    Optional<Player> findByPlayerName(String playerName);
    List<Player> findAllByClubId(Long clubId);
}
