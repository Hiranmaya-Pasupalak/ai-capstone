package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull @Min(1) @Max(5)
    private Integer rating;

    @NotBlank @Size(max = 500)
    private String comment;
}
