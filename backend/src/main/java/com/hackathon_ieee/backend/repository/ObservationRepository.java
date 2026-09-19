package com.hackathon_ieee.backend.repository;

import com.hackathon_ieee.backend.enums.Parameter;
import com.hackathon_ieee.backend.model.ObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface ObservationRepository extends JpaRepository<ObservationEntity, String> {

    List<ObservationEntity> findByParameterOrderByObservedAtDesc(Parameter parameter);

    List<ObservationEntity> findByObservedAtBetweenOrderByObservedAtDesc(OffsetDateTime from, OffsetDateTime to);

    List<ObservationEntity> findByParameterAndObservedAtBetweenOrderByObservedAtDesc(
            Parameter parameter, OffsetDateTime from, OffsetDateTime to);

    List<ObservationEntity> findAllByOrderByObservedAtDesc();

    @Query("""
            select distinct o.locationName as locationName, o.stationNo as stationNo,
                   o.sampleType as sampleType, o.latitude as latitude, o.longitude as longitude
            from ObservationEntity o
            order by o.locationName, o.stationNo, o.sampleType
            """)
    List<LocationProjection> findDistinctLocations();

    /*
    Spring Data JPA'da Projection (İzdüşüm), veritabanından koca bir entity (varlık) nesnesini tüm alanlarıyla çekmek yerine,
    sadece ihtiyacımız olan spesifik sütunları çekmemizi sağlayan bir arayüzdür (interface).

    Sizin yazdığınız @Query içerisinde sadece lokasyon adı, istasyon numarası, örneklem tipi ve koordinatlar select edilmektedir.
    Spring Data, veritabanından dönen bu satırları doğrudan LocationProjection arayüzündeki get... metotlarıyla eşleştirir. Bu sayede,
    devasa ObservationEntity nesneleri yaratılıp RAM'i doldurmak yerine, bellekte çok daha az yer kaplayan hafif nesneler oluşturulur.
    * */



}