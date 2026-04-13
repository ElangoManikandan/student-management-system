package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {

    private Long id;
    private String title;
    private int credits;
    private String duration;
}