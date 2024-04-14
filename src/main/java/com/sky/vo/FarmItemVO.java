package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmItemVO implements Serializable {

    //农产品名称
    private String name;

    //份数
    private Integer copies;

    //农产品图片
    private String image;

    //农产品描述
    private String description;
}
