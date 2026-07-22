package com.prathamcodes.scattermerge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provider_results")
@Getter
@Setter
public class ProviderResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

}
