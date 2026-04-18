package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnoverDTO {
    private LocalDateTime begin;
    private LocalDateTime end;
    private Integer status;
}
