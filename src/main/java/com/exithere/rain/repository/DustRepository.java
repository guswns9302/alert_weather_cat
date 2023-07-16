package com.exithere.rain.repository;

import com.exithere.rain.entity.DustForecast;
import com.exithere.rain.entity.ShortForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DustRepository extends JpaRepository<DustForecast, Long> {
    Optional<DustForecast> findByForecastDateAndDustTypeOrderByDustIdAsc(LocalDate forecastDate, String dustType);

    List<DustForecast> findByForecastDateOrderByDustIdAsc(LocalDate now);
}
