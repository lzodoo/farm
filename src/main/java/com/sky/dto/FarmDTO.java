package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class FarmDTO implements Serializable {

    private Long id;
    //农产品名称
    private String name;
    //农产品分类id
    private Long categoryId;
    //农产品价格
    private BigDecimal price;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;

}
