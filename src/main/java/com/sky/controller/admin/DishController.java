package com.sky.controller.admin;

import com.sky.dto.FarmDTO;
import com.sky.dto.FarmPageQueryDTO;
import com.sky.entity.Farm;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.FarmService;
import com.sky.vo.FarmVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "农产品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private FarmService farmService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增农产品
     *
     * @param farmDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增农产品")
    public Result save(@RequestBody FarmDTO farmDTO) {
        log.info("新增农产品：{}", farmDTO);
        farmService.saveWithFlavor(farmDTO);

        //清理缓存数据
        String key = "farm_" + farmDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 农产品分页查询
     *
     * @param farmPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("农产品分页查询")
    public Result<PageResult> page(FarmPageQueryDTO farmPageQueryDTO) {
        log.info("农产品分页查询:{}", farmPageQueryDTO);
        PageResult pageResult = farmService.pageQuery(farmPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("农产品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("农产品批量删除：{}", ids);
        farmService.deleteBatch(ids);

        //将所有的农产品缓存数据清理掉，所有以farm_开头的key
        cleanCache("farm_*");

        return Result.success();
    }

    /**
     * 根据id查询农产品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询农产品")
    public Result<FarmVO> getById(@PathVariable Long id) {
        log.info("根据id查询农产品：{}", id);
        FarmVO farmVO = farmService.getByIdWithFlavor(id);
        return Result.success(farmVO);
    }

    /**
     * 修改农产品
     *
     * @param farmDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改农产品")
    public Result update(@RequestBody FarmDTO farmDTO) {
        log.info("修改农产品：{}", farmDTO);
        farmService.updateWithFlavor(farmDTO);

        //将所有的农产品缓存数据清理掉，所有以farm_开头的key
        cleanCache("farm_*");

        return Result.success();
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("农产品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        farmService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以farm_开头的key
        cleanCache("farm_*");

        return Result.success();
    }

    /**
     * 根据分类id查询农产品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询农产品")
    public Result<List<Farm>> list(Long categoryId) {
        List<Farm> list = farmService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
