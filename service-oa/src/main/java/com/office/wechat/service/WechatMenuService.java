package com.office.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.wechat.WechatMenu;
import com.office.vo.wechat.WechatMenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-16
 */
public interface WechatMenuService extends IService<WechatMenu> {
    List<WechatMenuVo> findWechatMenuInfo();

    void syncWechatMenu();

    void removeMenu();
}
