package com.exithere.rain.repository;

import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {

    List<AlarmHistory> findByDeviceIdOrderByPushDateTimeDesc(String deviceId);

}
