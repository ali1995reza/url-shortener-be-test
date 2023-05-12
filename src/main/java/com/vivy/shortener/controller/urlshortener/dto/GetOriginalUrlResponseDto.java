package com.vivy.shortener.controller.urlshortener.dto;

import com.vivy.shortener.controller.dto.BaseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class GetOriginalUrlResponseDto extends BaseResponseDto {

    private String originalUrl;
}
