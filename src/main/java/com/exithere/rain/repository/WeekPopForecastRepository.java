package com.exithere.rain.repository;

import com.exithere.rain.entity.WeekForecast;
import com.exithere.rain.entity.WeekPopForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeekPopForecastRepository extends JpaRepository<WeekPopForecast, Long> {
    Optional<WeekPopForecast> findByRegionIdAndForecastDate(String regionCode, LocalDate now);

    Optional<WeekPopForecast> findByRegionId(String regionCode);
}
