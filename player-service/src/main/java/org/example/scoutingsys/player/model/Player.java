package org.example.scoutingsys.player.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_name",nullable = false)
    private String playerName;

    @Column(name = "player_age",nullable = false)
    private Integer age;

    @Column(name = "player_goals",nullable = false)
    private Integer goals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubReference club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private CountryReference country;
}
