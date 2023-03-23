package com.office.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.office.model.system.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户角色 Mapper 接口
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
@Repository
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
