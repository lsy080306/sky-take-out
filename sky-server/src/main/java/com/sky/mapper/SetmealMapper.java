package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    @Select("select *,c.name as categoryName from setmeal s left join category c on s.category_id=c.id where s.id=#{id}")
    SetmealVO getSetmealById(Integer id);

    @AutoFill(OperationType.UPDATE)
    void updateSetmealInfo(Setmeal setmeal);

    void deleteSetmeal(List<Long> ids);

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    List<Setmeal> list(Setmeal setmeal);

    @Select("select count(*) from setmeal where status = #{enable}")
    Integer getEnableNum(Integer enable);

    @Select("select count(*) from setmeal where status = #{disable}")
    Integer getDisableNum(Integer disable);
}
