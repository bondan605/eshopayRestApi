package com.codeid.eshopay_backend.model.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity {

    @Column(name = "created_date", nullable = false)
    private Instant createDate = Instant.now();

    @Version
    @Column(name = "modified_date")
    private Instant modifiedDate;
}
