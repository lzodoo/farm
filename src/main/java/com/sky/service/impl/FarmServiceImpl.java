package com.sky.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.FarmDTO;
import com.sky.dto.FarmPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Farm;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.FarmMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.FarmService;
import com.sky.vo.FarmVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FarmServiceImpl extends ServiceImpl<FarmMapper, Farm> implements FarmService {

    @Autowired
    private FarmMapper farmMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 新增农产品
     *
     * @param farmDTO
     */
    @Transactional
    public void saveWithFlavor(FarmDTO farmDTO) {

        Farm farm = new Farm();

        BeanUtils.copyProperties(farmDTO, farm);

        //向农产品表插入1条数据
        farmMapper.insertFarm(farm);


    }

    /**
     * 农产品分页查询
     *
     * @param farmPageQueryDTO
     * @return
     */
    public PageResult pageQuery(FarmPageQueryDTO farmPageQueryDTO) {
        Page<Farm> page = Page.of(farmPageQueryDTO.getPage(), farmPageQueryDTO.getPageSize());
        page.addOrder(new OrderItem("create_time",false));
        String name = farmPageQueryDTO.getName();
        Integer status = farmPageQueryDTO.getStatus();
        Integer categoryId = farmPageQueryDTO.getCategoryId();
        lambdaQuery()
                .like(name!=null, Farm::getName, name)
                .eq(status != null,Farm::getStatus, status)
                .eq(categoryId != null, Farm::getCategoryId, categoryId)
                .page(page);

        List<FarmVO> list = new ArrayList();
        if (page != null && page.getTotal() > 0) {
            for (Farm farm: page.getRecords() ) {
                Long setmealCategoryId = farm.getCategoryId();
                // 查询分类名称
                Category category = categoryMapper.selectById(setmealCategoryId);
                String categoryName = category.getName();

                FarmVO farmVO = new FarmVO();
                BeanUtils.copyProperties(farm, farmVO);
                farmVO.setCategoryName(categoryName);
                list.add(farmVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 农产品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前农产品是否能够删除---是否存在起售中的农产品
        for (Long id : ids) {
            Farm farm = farmMapper.getById(id);
            if (farm.getStatus() == StatusConstant.ENABLE) {
                //当前农产品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前农产品是否能够删除---是否被套餐关联了？？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的农产品数据
        for (Long id : ids) {
            farmMapper.deleteById(id);
        }
    }

    /**
     * 根据id查询农产品
     *
     * @param id
     * @return
     */
    public FarmVO getByIdWithFlavor(Long id) {
        //根据id查询农产品数据
        Farm farm = farmMapper.getById(id);

        //将查询到的数据封装到VO
        FarmVO farmVO = new FarmVO();
        BeanUtils.copyProperties(farm, farmVO);

        return farmVO;
    }

    /**
     * 根据id修改农产品基本信息和对应的口味信息
     *
     * @param farmDTO
     */
    public void updateWithFlavor(FarmDTO farmDTO) {
        Farm farm = new Farm();
        BeanUtils.copyProperties(farmDTO, farm);

        //修改菜品表基本信息
        farmMapper.update(farm);


    }

    /**
     * 农产品起售停售
     *
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Farm farm = Farm.builder()
                .id(id)
                .status(status)
                .build();
        farmMapper.update(farm);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前农产品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据分类id查询农产品
     *
     * @param categoryId
     * @return
     */
    public List<Farm> list(Long categoryId) {
        Farm farm = Farm.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return farmMapper.list(farm);
    }

    /**
     * 条件查询农产品
     * @param farm
     * @return
     */
    public List<FarmVO> listWithFlavor(Farm farm) {
        List<Farm> farmList = farmMapper.list(farm);

        List<FarmVO> farmVOList = new ArrayList<>();

        for (Farm d : farmList) {
            FarmVO farmVO = new FarmVO();
            BeanUtils.copyProperties(d, farmVO);
            farmVOList.add(farmVO);
        }
        return farmVOList;
    }
}
