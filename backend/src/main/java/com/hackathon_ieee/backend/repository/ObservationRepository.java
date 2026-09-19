package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.enums.Parameter;
import com.hackathon_ieee.backend.model.ObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ObservationRepository extends JpaRepository<ObservationEntity, String> {

    List<ObservationEntity> findByParameterOrderByObservedAtDesc(Parameter parameter);

    List<ObservationEntity> findByObservedAtBetweenOrderByObservedAtDesc(OffsetDateTime from, OffsetDateTime to);

    List<ObservationEntity> findByParameterAndObservedAtBetweenOrderByObservedAtDesc(
            Parameter parameter, OffsetDateTime from, OffsetDateTime to);

    List<ObservationEntity> findAllByOrderByObservedAtDesc();
}