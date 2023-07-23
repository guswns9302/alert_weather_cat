package com.exithere.rain.repository;

import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByRegionNameAndRegionXAndRegionY(String regionName, int regionX, int regionY);
}
