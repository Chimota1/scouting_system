package org.example.scoutingsys.player.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.player.aspect.AuditChange;
import org.example.scoutingsys.player.dto.PlayerDto;
import org.example.scoutingsys.player.mapper.PlayerMapper;
import org.example.scoutingsys.player.model.ClubReference;
import org.example.scoutingsys.player.model.CountryReference;
import org.example.scoutingsys.player.model.Player;
import org.example.scoutingsys.player.repository.ClubReferenceRepository;
import org.example.scoutingsys.player.repository.CountryReferenceRepository;
import org.example.scoutingsys.player.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final ClubReferenceRepository clubRepository;
    private final CountryReferenceRepository countryRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper,
                         ClubReferenceRepository clubRepository, CountryReferenceRepository countryRepository) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.clubRepository = clubRepository;
        this.countryRepository = countryRepository;
    }

    @AuditChange(action = "CREATE", entity = "Player")
    public PlayerDto create(PlayerDto playerDto) {
        Player player = playerMapper.toEntity(playerDto);
        setRelations(player, playerDto);

        return playerMapper.toDto(playerRepository.save(player));
    }

    public List<PlayerDto> getAll() {
        return playerRepository.findAll().stream()
                .map(playerMapper::toDto)
                .sorted(java.util.Comparator.comparing(PlayerDto::getPlayerName,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public PlayerDto getById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Гравець з ID " + id + " не знайдений"));
        return playerMapper.toDto(player);
    }

    @AuditChange(action = "UPDATE", entity = "Player")
    public PlayerDto update(Long id, PlayerDto playerDto) {
        Player existingPlayer = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Гравець з ID " + id + " не знайдений"));

        existingPlayer.setPlayerName(playerDto.getPlayerName());
        existingPlayer.setAge(playerDto.getAge());
        existingPlayer.setGoals(playerDto.getGoals());
        setRelations(existingPlayer, playerDto);

        return playerMapper.toDto(playerRepository.save(existingPlayer));
    }

    private void setRelations(Player player, PlayerDto dto) {
        CountryReference country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + dto.getCountryId() + " не знайдена"));
        player.setCountry(country);

        if (dto.getClubId() != null) {
            ClubReference club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Клуб з ID " + dto.getClubId() + " не знайдений"));
            player.setClub(club);
        } else {
            player.setClub(null);
        }
    }

    @AuditChange(action = "DELETE", entity = "Player")
    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Гравець з ID " + id + " не знайдений");
        }
        playerRepository.deleteById(id);
    }


    public void deleteAllByClub(Long clubId) {
        playerRepository.findAllByClubId(clubId)
                .forEach(player -> this.delete(player.getId()));
    }
}
