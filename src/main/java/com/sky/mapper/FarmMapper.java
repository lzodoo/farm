package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.annotation.AutoFill;
import com.sky.dto.FarmPageQueryDTO;
import com.sky.entity.Farm;
import com.sky.enumeration.OperationType;
import com.sky.vo.FarmVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FarmMapper extends BaseMapper<Farm> {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from farm where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     *
     * @param farm
     */
    @AutoFill(value = OperationType.INSERT)
    void insertFarm(Farm farm);

    /**
     * 菜品分页查询
     *
     * @param farmPageQueryDTO
     * @return
     */

    /**
     * 根据主键查询菜品
     *
     * @param id
     * @return
     */
    @Select("select * from farm where id = #{id}")
    Farm getById(Long id);

    /**
     * 根据主键删除菜品数据
     *
     * @param id
     */
    @Delete("delete from farm where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id动态修改菜品数据
     *
     * @param farm
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Farm farm);

    /**
     * 动态条件查询菜品
     *
     * @param farm
     * @return
     */
    List<Farm> list(Farm farm);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from farm a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Farm> getBySetmealId(Long setmealId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
