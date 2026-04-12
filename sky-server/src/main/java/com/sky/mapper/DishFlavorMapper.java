package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    @AutoFill(OperationType.INSERT)
    void addDishFlavor(List<DishFlavor> dishFlavorsList);

    List<DishFlavor> getFlavorById(Integer id);

    void deleteFlavor(List<Long> ids);
}
