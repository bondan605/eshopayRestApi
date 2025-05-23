package com.codeid.eshopay_backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fintechs", schema = "fintech")
public class Bank extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fint_code")
    private String fintCode;

    @Column(name = "fint_name")
    private String fintName;

    @Column(name = "fint_short_name")
    private String fintShortName;

    @Column(name = "fint_type")
    private String fintType;

}
