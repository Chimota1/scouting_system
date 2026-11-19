package org.example.scoutingsys.player.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clubs")
@Getter
@Setter
public class ClubReference {

    @Id
    private Long id;

    @Column(name = "club_name")
    private String clubName;
}
