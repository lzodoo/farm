package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Farm;
import com.sky.result.Result;
import com.sky.service.FarmService;
import com.sky.vo.FarmVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userFarmController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-农产品浏览接口")
public class FarmController {
    @Autowired
    private FarmService farmService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询农产品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询农产品")
    public Result<List<FarmVO>> list(Long categoryId) {

        //构造redis中的key，规则：dish_分类id
        String key = "farm_" + categoryId;

        //查询redis中是否存在农产品数据
        List<FarmVO> list = (List<FarmVO>) redisTemplate.opsForValue().get(key);
        if(list != null && list.size() > 0){
            //如果存在，直接返回，无须查询数据库
            return Result.success(list);
        }

        Farm farm = new Farm();
        farm.setCategoryId(categoryId);
        farm.setStatus(StatusConstant.ENABLE);//查询起售中的农产品

        //如果不存在，查询数据库，将查询到的数据放入redis中
        list = farmService.listWithFlavor(farm);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}
