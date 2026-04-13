package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询套餐
      * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐{}", setmealPageQueryDTO);
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        log.info("新增套餐{}",setmealDTO);
        Setmeal  setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.addSetmeal(setmeal);
        List<SetmealDish> list=setmealDTO.getSetmealDishes();
        Long id=setmeal.getId();
        for(SetmealDish setmealDish:list){
            setmealDish.setSetmealId(id);
        }
        setmealDishMapper.addSetmealDish(list);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealById(Integer id) {
        log.info("根据id查询套餐{}",id);
        SetmealVO setmealVO=setmealMapper.getSetmealById(id);
        setmealVO.setSetmealDishes(setmealDishMapper.getSetmealDishById(id));
        return setmealVO;
    }

    /**
     * 修改套餐启售状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        log.info("修改套餐启售状态{}",status);
        Setmeal setmeal=new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.updateSetmealInfo(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteSetmeal(List<Long> ids) {
        log.info("批量删除套餐{}",ids);
        setmealDishMapper.deleteSetmealDish(ids);
        setmealMapper.deleteSetmeal(ids);
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     */
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        log.info("修改套餐信息");
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateSetmealInfo(setmeal);
        setmealDishMapper.deleteSetmealDish(Collections.singletonList(setmealDTO.getId()));
        List<SetmealDish> list=setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish:list){
            setmealDish.setSetmealId(setmealDTO.getId());
        }
        setmealDishMapper.addSetmealDish(list);
    }


}
