package com.example.demo.domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueCreateRequestDto {

    private String name;

    private String address;

    private Integer rowCount;

    private Integer colCount;
}
