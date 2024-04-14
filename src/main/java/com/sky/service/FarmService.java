package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.FarmDTO;
import com.sky.dto.FarmPageQueryDTO;
import com.sky.entity.Farm;
import com.sky.result.PageResult;
import com.sky.vo.FarmVO;

import java.util.List;

public interface FarmService extends IService<Farm> {

    /**
     * 新增菜品和对应的口味
     *
     * @param farmDTO
     */
    public void saveWithFlavor(FarmDTO farmDTO);

    /**
     * 菜品分页查询
     *
     * @param farmPageQueryDTO
     * @return
     */
    PageResult pageQuery(FarmPageQueryDTO farmPageQueryDTO);

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和对应的口味数据
     *
     * @param id
     * @return
     */
    FarmVO getByIdWithFlavor(Long id);

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     *
     * @param farmDTO
     */
    void updateWithFlavor(FarmDTO farmDTO);

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Farm> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param farm
     * @return
     */
    List<FarmVO> listWithFlavor(Farm farm);
}
