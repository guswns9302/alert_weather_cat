package com.exithere.rain.repository;

import com.exithere.rain.entity.ShortForecast;
import com.exithere.rain.entity.WeekForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeekForecastRepository extends JpaRepository<WeekForecast, Long> {
    Optional<WeekForecast> findByRegionIdAndForecastDate(String regionId, LocalDate forecastDate);

    Optional<WeekForecast> findByRegionId(String regionId);
}
