package com.office.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.office.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
@Repository
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {


    List<SysMenu> findListByUserId(@Param("userId") Long userId);
}
