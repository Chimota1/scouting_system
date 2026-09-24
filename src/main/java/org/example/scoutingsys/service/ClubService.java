package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.mapper.ClubMapper;
import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.League;
import org.example.scoutingsys.model.Manager;
import org.example.scoutingsys.repository.ClubRepository;
import org.example.scoutingsys.repository.LeagueRepository;
import org.example.scoutingsys.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;
    private final LeagueRepository leagueRepository;
    private final ManagerRepository managerRepository;

    public ClubService(ClubRepository clubRepository, ClubMapper clubMapper,
                       LeagueRepository leagueRepository, ManagerRepository managerRepository) {
        this.clubRepository = clubRepository;
        this.clubMapper = clubMapper;
        this.leagueRepository = leagueRepository;
        this.managerRepository = managerRepository;
    }

    public ClubDto create(ClubDto clubDto) {
        Club club = clubMapper.toEntity(clubDto);
        League league = leagueRepository.findById(clubDto.getLeagueId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ліга з ID " + clubDto.getLeagueId() + " не знайдена"));
        club.setLeague(league);

        Club savedClub = clubRepository.save(club);
        setManager(savedClub, clubDto.getManager());

        return clubMapper.toDto(savedClub);
    }

    public List<ClubDto> getAll() {
        return clubRepository.findAll().stream()
                .map(clubMapper::toDto)
                .toList();
    }

    public ClubDto getById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Клуб з ID " + id + " не знайдений"));
        return clubMapper.toDto(club);
    }

    public ClubDto update(Long id, ClubDto clubDto) {
        Club existingClub = clubRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Клуб з ID " + id + " не знайдений"));

        existingClub.setClubName(clubDto.getClubName());

        League league = leagueRepository.findById(clubDto.getLeagueId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ліга з ID " + clubDto.getLeagueId() + " не знайдена"));
        existingClub.setLeague(league);

        Club updatedClub = clubRepository.save(existingClub);
        setManager(updatedClub, clubDto.getManager());

        return clubMapper.toDto(updatedClub);
    }

    private void setManager(Club club, org.example.scoutingsys.dto.ManagerDto managerDto) {
        if (managerDto == null || managerDto.getId() == null) {
            return;
        }

        Manager manager = managerRepository.findById(managerDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Менеджер з ID " + managerDto.getId() + " не знайдений"));
        manager.setClub(club);
        managerRepository.save(manager);
        club.setManager(manager);
    }

    public void delete(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new EntityNotFoundException("Клуб з ID " + id + " не знайдений");
        }
        clubRepository.deleteById(id);
    }
}
