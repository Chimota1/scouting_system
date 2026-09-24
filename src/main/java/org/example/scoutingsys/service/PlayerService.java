package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.PlayerDto;
import org.example.scoutingsys.mapper.PlayerMapper;
import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.Player;
import org.example.scoutingsys.repository.ClubRepository;
import org.example.scoutingsys.repository.CountryRepository;
import org.example.scoutingsys.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final ClubRepository clubRepository;
    private final CountryRepository countryRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper,
                         ClubRepository clubRepository, CountryRepository countryRepository) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.clubRepository = clubRepository;
        this.countryRepository = countryRepository;
    }

    public PlayerDto create(PlayerDto playerDto) {
        Player player = playerMapper.toEntity(playerDto);
        setRelations(player, playerDto);

        return playerMapper.toDto(playerRepository.save(player));
    }

    public List<PlayerDto> getAll() {
        return playerRepository.findAll().stream()
                .map(playerMapper::toDto)
                .toList();
    }

    public PlayerDto getById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Гравець з ID " + id + " не знайдений"));
        return playerMapper.toDto(player);
    }

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
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + dto.getCountryId() + " не знайдена"));
        player.setCountry(country);

        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Клуб з ID " + dto.getClubId() + " не знайдений"));
            player.setClub(club);
        } else {
            player.setClub(null);
        }
    }

    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Гравець з ID " + id + " не знайдений");
        }
        playerRepository.deleteById(id);
    }
}
