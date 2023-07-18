package com.exithere.rain.service;

import com.exithere.rain.dto.request.DeviceJoinRequest;
import com.exithere.rain.dto.request.DeviceRegionRequest;
import com.exithere.rain.dto.response.DeviceTotalResponse;
import com.exithere.rain.dto.response.RegionListResponse;
import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.DeviceRepository;
import com.exithere.rain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final RegionService regionService;
    private final AlarmService alarmService;

    private Optional<Device> findDeviceByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }

    @Transactional
    public DeviceTotalResponse loadAndSaveUser(DeviceJoinRequest deviceJoinRequest) {
        // 해당 device id 가 있는지 확인
        Optional<Device> existDeviceId = this.findDeviceByDeviceId(deviceJoinRequest.getDeviceId());
        if(existDeviceId.isPresent()){
            // 있으면 해당 device id의 설정을 return
            existDeviceId.get().updateLastLogin();
            return DeviceTotalResponse.from(existDeviceId.get());
        }
        else {
            // 없으면 device id 를 생성하고 기본 설정값 return
            Region region = regionService.findRegion(deviceJoinRequest.getRegionName(), deviceJoinRequest.getRegionX(), deviceJoinRequest.getRegionY());

            // 알람 기본 값 생성
            Alarm defaultAlarm = alarmService.defaultAlarmCreate(deviceJoinRequest.getDeviceId());

            Device newDevice = Device.builder()
                    .deviceId(deviceJoinRequest.getDeviceId())
                    .fcmToken(deviceJoinRequest.getFcmToken())
                    .firstRegionCd(region)
                    .selectRegionCd(region)
                    .pushBtn(true)
                    .specialReport(true)
                    .alarm(defaultAlarm)
                    .build();

            Device saveDevice = deviceRepository.save(newDevice);
            saveDevice.updateLastLogin();
            return DeviceTotalResponse.from(saveDevice);
        }
    }

    @Transactional
    public Map<String, Boolean> togglePushBtn(String deviceId) {
        Optional<Device> existDevice = this.findDeviceByDeviceId(deviceId);

        if(existDevice.isPresent()){
            // 현재 토글 버튼 상태 확인
            boolean currentBtn = existDevice.get().isPushBtn();
            if(currentBtn){
                existDevice.get().updatePushBtn(false);
            }
            else{
                existDevice.get().updatePushBtn(true);
            }
            return Map.of("pushBtn", existDevice.get().isPushBtn());
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Transactional
    public Map<String, Boolean> toggleSpecialBtn(String deviceId) {
        Optional<Device> existDevice = this.findDeviceByDeviceId(deviceId);

        if(existDevice.isPresent()){
            // 현재 토글 버튼 상태 확인
            boolean currentBtn = existDevice.get().isSpecialReport();
            if(currentBtn){
                existDevice.get().updateSpecialBtn(false);
            }
            else{
                existDevice.get().updateSpecialBtn(true);
            }
            return Map.of("specialBtn", existDevice.get().isSpecialReport());
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Transactional
    public RegionListResponse updateRegion(DeviceRegionRequest deviceRegionRequest) {
        Optional<Device> existDeviceId = this.findDeviceByDeviceId(deviceRegionRequest.getDeviceId());

        if(existDeviceId.isPresent()){
            Device device = existDeviceId.get();
            if(device.getFirstRegionCd() != null && device.getSecondRegionCd() != null && device.getThirdRegionCd() != null){
                throw new CustomException(ErrorCode.CAN_NOT_ADD_MORE);
            }
            else{
                Region region = regionService.findRegion(deviceRegionRequest.getRegionName(), deviceRegionRequest.getRegionX(), deviceRegionRequest.getRegionY());

                if(device.getFirstRegionCd() != null && region.getRegionId() == device.getFirstRegionCd().getRegionId() ||
                    device.getSecondRegionCd() != null && region.getRegionId() == device.getSecondRegionCd().getRegionId() ||
                    device.getThirdRegionCd() != null && region.getRegionId() == device.getThirdRegionCd().getRegionId()){
                    throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
                }

                // 이후 어디에 저장할 것인가..?
                if(device.getFirstRegionCd() == null){
                    device.updateFirstRegion(region);
                }
                else if(device.getSecondRegionCd() == null){
                    device.updateSecondRegion(region);
                }
                else if(device.getThirdRegionCd() == null){
                    device.updateThirdRegion(region);
                }

                return RegionListResponse.from(device);
            }
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Transactional
    public RegionListResponse updateSelectRegion(DeviceRegionRequest deviceRegionRequest) {
        Optional<Device> existDeviceId = this.findDeviceByDeviceId(deviceRegionRequest.getDeviceId());
        if(existDeviceId.isPresent()){
            Region region = regionService.findRegion(deviceRegionRequest.getRegionName(), deviceRegionRequest.getRegionX(), deviceRegionRequest.getRegionY());

            region.getRegionId();
            if(region.getRegionId() == existDeviceId.get().getFirstRegionCd().getRegionId()
                || region.getRegionId() == existDeviceId.get().getSecondRegionCd().getRegionId()
                || region.getRegionId() == existDeviceId.get().getThirdRegionCd().getRegionId()
            ){
                existDeviceId.get().updateSelectRegion(region);
                return RegionListResponse.from(existDeviceId.get());
            }
            else{
                throw new CustomException(ErrorCode.CAN_NOT_SELECT_REGION);
            }
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Transactional
    public RegionListResponse deleteRegion(DeviceRegionRequest deviceRegionRequest) {
        Optional<Device> existDeviceId = this.findDeviceByDeviceId(deviceRegionRequest.getDeviceId());

        if(existDeviceId.isPresent()){
            if(deviceRegionRequest.getRegionName().equals(existDeviceId.get().getFirstRegionCd().getRegionName())){
                existDeviceId.get().deleteRegion(1);
            }
            else if(deviceRegionRequest.getRegionName().equals(existDeviceId.get().getSecondRegionCd().getRegionName())){
                existDeviceId.get().deleteRegion(2);
            }
            else if(deviceRegionRequest.getRegionName().equals(existDeviceId.get().getThirdRegionCd().getRegionName())){
                existDeviceId.get().deleteRegion(3);
            }
            else{
                throw new CustomException(ErrorCode.REGION_NOT_FOUND);
            }

            return RegionListResponse.from(existDeviceId.get());
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }

    @Transactional
    public List<Device> getAllDevice(){
        return deviceRepository.findAll();
    }
}
