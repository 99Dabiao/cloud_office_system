package com.office.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.office.model.system.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色菜单 Mapper 接口
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
@Repository
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}
