package com.hackathon_ieee.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WaterQualityDto {
    private Double ph;
    private Double ecUsCm;
    private Double tdsMgL;
    private Double salinityPsu;
    private Double doMgL;
}