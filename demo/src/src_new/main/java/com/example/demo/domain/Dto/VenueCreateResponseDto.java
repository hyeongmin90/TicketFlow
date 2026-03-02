package com.example.demo.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VenueCreateResponseDto {

    private String name;

    private String address;

    private Integer row;

    private Integer col;
}
