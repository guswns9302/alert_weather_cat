package com.exithere.rain.repository;

import com.exithere.rain.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String> {

    Optional<Device> findByDeviceId(String deviceId);
}
