package com.exithere.rain.repository;

import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, String> {

}
