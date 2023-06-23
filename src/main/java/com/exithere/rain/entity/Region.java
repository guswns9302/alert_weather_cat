package com.exithere.rain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "TB_REGION")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REGION_ID")
    private Long regionId;

    @Column(name = "REGION_NAME")
    private String regionName;

    @Column(name = "REGION_X")
    private int regionX;

    @Column(name = "REGION_Y")
    private int regionY;
}
