package com.exithere.rain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name="TB_DEVICE")
public class Device {

    @Id
    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "FCM_TOKEN")
    private String fcmToken;

    @ManyToOne
    @JoinColumn(name = "FIRST_REGION_CD")
    private Region firstRegionCd;

    @ManyToOne
    @JoinColumn(name = "SECOND_REGION_CD")
    private Region secondRegionCd;

    @ManyToOne
    @JoinColumn(name = "THIRD_REGION_CD")
    private Region thirdRegionCd;

    @ManyToOne
    @JoinColumn(name = "SELECT_REGION_CD")
    private Region selectRegionCd;

    @Column(name = "PUSH_BTN")
    private boolean pushBtn;

    @OneToOne
    @JoinColumn(name = "DEVICE_CD")
    private Alarm alarm;

    @Column(name = "SPECIAL_REPORT")
    private boolean specialReport;

    @CreatedDate
    @Column(name = "CREATE_AT", updatable = false, nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "LAST_LOGIN_AT")
    private LocalDateTime lastLoginAt;

    public void updatePushBtn(boolean toggle){
        this.pushBtn = toggle;
    }

    public void updateSpecialBtn(boolean toggle){
        this.specialReport = toggle;
    }

    public void updateLastLogin(){
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }

    public void updateFirstRegion(Region region) {
        this.firstRegionCd =  region;
    }

    public void updateSecondRegion(Region region) {
        this.secondRegionCd =  region;
    }

    public void updateThirdRegion(Region region) {
        this.thirdRegionCd =  region;
    }

    public void updateSelectRegion(Region region){
        this.selectRegionCd = region;
    }

    public void deleteRegion(int select){
        if(select == 1){
            // 첫번째 삭제 -> 두번재 , 세번째가 있는지 확인
            if(this.secondRegionCd != null){
                this.firstRegionCd = this.secondRegionCd;

                if(this.thirdRegionCd != null){
                    this.secondRegionCd = this.thirdRegionCd;
                    this.thirdRegionCd = null;
                }
                else{
                    this.secondRegionCd = null;
                }
            }
        }

        if(select == 2){
            if(this.thirdRegionCd != null){
                this.secondRegionCd = this.thirdRegionCd;
                this.thirdRegionCd = null;
            }
            else{
                this.secondRegionCd = null;
            }
        }

        if(select == 3){
            this.thirdRegionCd = null;
        }
    }
}
