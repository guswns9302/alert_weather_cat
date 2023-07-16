package com.exithere.rain.repository;

import com.exithere.rain.entity.ShortForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ShortForecastRepository extends JpaRepository<ShortForecast, Long> {
    Optional<ShortForecast> findByRegion_RegionIdAndForecastDateTime(Long regionId, LocalDateTime localDateTime);

    List<ShortForecast> findByRegion_RegionIdAndForecastDateTimeBetweenOrderByForecastDateTimeAsc(Long regionId, LocalDateTime fromLDT, LocalDateTime toLDT);
}
