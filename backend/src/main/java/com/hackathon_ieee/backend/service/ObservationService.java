package com.hackathon_ieee.backend.service;

import com.hackathon_ieee.backend.config.ThresholdConfig;
import com.hackathon_ieee.backend.dto.CoordinatesDto;
import com.hackathon_ieee.backend.dto.ObservationCreateRequest;
import com.hackathon_ieee.backend.dto.ObservationListItemDto;
import com.hackathon_ieee.backend.dto.ObservationResponse;
import com.hackathon_ieee.backend.enums.Parameter;
import com.hackathon_ieee.backend.enums.SampleType;
import com.hackathon_ieee.backend.enums.SourceType;
import com.hackathon_ieee.backend.model.ObservationEntity;
import com.hackathon_ieee.backend.repository.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ObservationService {
    private final ObservationRepository repository;
    private final ThresholdConfig thresholdConfig;
    private final FhirClientService fhirClient;

    public ObservationResponse create(ObservationCreateRequest request) {
        return create(request, "obs-" + UUID.randomUUID());
    }

    public ObservationResponse create(ObservationCreateRequest request, String id) {
        Parameter parameter = parse(request.getParameter(), Parameter.class);
        SampleType sampleType = parse(request.getSampleType(), SampleType.class);
        SourceType sourceType = parse(request.getSourceType(), SourceType.class);
        if (request.getUnit() == null || !request.getUnit().equals(sampleType.getExpectedUnit())) {
            throw badRequest("unit must match sample_type expected unit");
        }
        if (!request.isBelowDetectionLimit() && request.getValue() == null) {
            throw badRequest("value is required unless below_detection_limit is true");
        }

        ObservationEntity entity = new ObservationEntity(id, request.getLocationName(), parameter, sourceType,
                sampleType, required(request.getTimestamp(), "timestamp"), request.getUnit());

        entity.setStationNo(request.getStationNo());
        entity.setObservedAt(request.getTimestamp());
        entity.setLatitude(request.getCoordinates() == null ? null : request.getCoordinates().getLat());
        entity.setLongitude(request.getCoordinates() == null ? null : request.getCoordinates().getLon());
        entity.setCoordinateSource(request.getCoordinateSource());
        entity.setMethod(request.getMethod());
        entity.setCitation(request.getCitation());
        entity.setBelowDetectionLimit(request.isBelowDetectionLimit());
        entity.setValue(request.isBelowDetectionLimit() ? null : request.getValue());

        if (!entity.isBelowDetectionLimit() && sampleType.supportsDrinkingWaterThresholds() && thresholdConfig.hasThreshold(parameter)) {

            final Double measuredValue = entity.getValue();
            List<String> exceeded = thresholdConfig.getThresholds(parameter).entrySet().stream()
                    .filter(entry -> measuredValue > entry.getValue())
                    .map(entry -> entry.getKey().name())
                    .toList();
            entity.setRiskFlagged(!exceeded.isEmpty());
            entity.setExceededStandardsCsv(String.join(",", exceeded));
        } else {
            entity.setRiskFlagged(false);
            entity.setExceededStandardsCsv("");
        }

        entity = repository.save(entity);
        entity.setFhirObservationId(fhirClient.createObservation(entity));
        entity = repository.save(entity);
        return toCreateResponse(entity);
    }

    public List<ObservationListItemDto> findAll(String parameter, OffsetDateTime from, OffsetDateTime to) {
        List<ObservationEntity> entities;
        if (parameter != null && from != null && to != null) {
            entities = repository.findByParameterAndObservedAtBetweenOrderByObservedAtDesc(parse(parameter, Parameter.class), from, to);
        } else if (parameter != null) {
            entities = repository.findByParameterOrderByObservedAtDesc(parse(parameter, Parameter.class));
        } else if (from != null && to != null) {
            entities = repository.findByObservedAtBetweenOrderByObservedAtDesc(from, to);
        } else {
            entities = repository.findAllByOrderByObservedAtDesc();
        }
        return entities.stream().map(this::toListItem).toList();
    }

    /*
    Esnek Sorgulama (Filtreleme): Bir dashboard üzerinde kullanıcı her zaman belirli bir parametreyi (örneğin sadece "chromium") aramak istemeyebilir.
    Kullanıcı, "Bu hafta bölgeden gelen tüm gözlemleri (kadmiyum, çinko, demir hepsi karışık) görmek istiyorum" diyebilir. Bu durumda parameter boş (null)
    gönderilir, sadece from ve to tarihleri gönderilir. İlgili else if (from != null && to != null) bloğu çalışarak o tarih aralığındaki tüm parametrelere
    ait gözlemleri getirir.

    Tam Liste Gösterimi: Kullanıcı dashboard'u ilk açtığında hiçbir filtre seçmemiş olabilir. Bu durumda API, son verileri
    (en güncelden eskiye doğru) göstermek için parametresiz ve tarihsiz çağrılır. En alttaki else bloğu (repository.findAllByOrderByObservedAtDesc())
    devreye girerek sisteme girilmiş tüm ölçümleri sayfada listelemek üzere döndürür.

    */


    private ObservationResponse toCreateResponse(ObservationEntity entity) {
        List<String> standards = entity.getExceededStandardsCsv() == null || entity.getExceededStandardsCsv().isBlank()
                ? Collections.emptyList() : Arrays.asList(entity.getExceededStandardsCsv().split(","));
        return new ObservationResponse(entity.getId(), entity.getFhirObservationId(), entity.isRiskFlagged(), standards);
    }

    private ObservationListItemDto toListItem(ObservationEntity entity) {
        CoordinatesDto coordinates = entity.getLatitude() == null || entity.getLongitude() == null ? null
                : coordinates(entity.getLatitude(), entity.getLongitude());
        return new ObservationListItemDto(entity.getId(), entity.getLocationName(), coordinates,
                entity.getObservedAt(), entity.getParameter().name().toLowerCase(), entity.getValue(), entity.getUnit(),
                entity.isBelowDetectionLimit(), entity.getSampleType().name().toLowerCase(),
                entity.getSourceType().name().toLowerCase(), entity.isRiskFlagged());
    }

    private CoordinatesDto coordinates(Double lat, Double lon) {
        CoordinatesDto value = new CoordinatesDto();
        value.setLat(lat);
        value.setLon(lon);
        return value;
    }

    private <T extends Enum<T>> T parse(String value, Class<T> type) {
        if (value == null) throw badRequest("missing enum value");
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw badRequest("invalid value: " + value);
        }
    }

    private <T> T required(T value, String field) {
        if (value == null) throw badRequest(field + " is required");
        return value;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}