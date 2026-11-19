package org.example.scoutingsys.player.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class CountryReference {

    @Id
    private Long id;

    @Column(name = "country_name")
    private String countryName;
}
