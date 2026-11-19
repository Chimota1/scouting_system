package org.example.scoutingsys.repository;

import org.example.scoutingsys.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager,Long> {
    Optional<Manager> findByManagerName(String managerName);
    Optional<Manager> findByManagerAge(Integer managerAge);
    Optional<Manager> findAllByClubId(Long clubId);
}
