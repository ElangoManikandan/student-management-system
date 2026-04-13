package com.sms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {

    @NotBlank(message = "Course title must not be blank")
    private String title;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    @NotBlank(message = "Duration must not be blank")
    private String duration;
}