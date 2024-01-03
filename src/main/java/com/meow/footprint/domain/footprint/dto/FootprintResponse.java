package com.meow.footprint.domain.footprint.dto;

import com.meow.footprint.domain.footprint.entity.Footprint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FootprintResponse {
    long id;
    String writer;
    String content;
    LocalDateTime createTime;
    boolean isChecked;
    boolean isSecret;

    public static FootprintResponse from(Footprint footprint){
        return new FootprintResponse(footprint.getId(),
                footprint.getWriter(),
                footprint.getContent(),
                footprint.getCreateTime(),
                footprint.isChecked(),
                footprint.isSecret());
    }
    public LocalDate getCreateDate(){
        return this.createTime.toLocalDate();
    }
}
