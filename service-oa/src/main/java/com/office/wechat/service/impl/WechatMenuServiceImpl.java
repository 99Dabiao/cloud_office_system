package com.office.wechat.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.model.wechat.WechatMenu;
import com.office.vo.wechat.WechatMenuVo;
import com.office.wechat.mapper.WechatMenuMapper;
import com.office.wechat.service.WechatMenuService;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-16
 */
@Service
public class WechatMenuServiceImpl extends ServiceImpl<WechatMenuMapper, WechatMenu> implements WechatMenuService {
    @Autowired
    private WxMpService wxMpService;

    /**
     * @author Dabiao
     * @date 2023/4/16 9:26
    * @description 同步、推送菜单
     **/

    @Override
    public void syncWechatMenu() {
        List<WechatMenuVo> menuVoList = this.findWechatMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(WechatMenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://dabiao.shop/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(WechatMenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://dabiao.shop/#" +twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMenuKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //推送菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @author Dabiao
     * @date 2023/4/16 9:5
     * @description 删除同步菜单
     **/


    @SneakyThrows
    @Override
    public void removeMenu() {
        wxMpService.getMenuService().menuDelete();
    }
    /**
     * @author Dabiao
     * @date 2023/4/16 9:25
    * @description 菜单分页
     **/
    @Override
    public List<WechatMenuVo> findWechatMenuInfo() {
        List<WechatMenuVo> list = new ArrayList<>();
        //查询所有菜单
        List<WechatMenu> menuList = baseMapper.selectList(null);
        //查询所有一级菜单，parent_id = 0，返回list集合
        List<WechatMenuVo>  firstLevelMenuList = menuList
                .stream()
                .filter(menu -> menu.getParentId().longValue() == 0)
                .map(firstLevelMenu -> {
                    WechatMenuVo firstLevelWechatMenuVo = new WechatMenuVo();
                    BeanUtils.copyProperties(firstLevelMenu,firstLevelWechatMenuVo);
                    return firstLevelWechatMenuVo;
                })
                .collect(Collectors.toList());
        //遍历一级菜单集合
        for (WechatMenuVo firstLevelWechatMenuVo : firstLevelMenuList) {
            //获取一级菜单中的二级菜单 id=parent_id
            //获取后封装到一级菜单children集合中
            List<WechatMenuVo> children = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == firstLevelWechatMenuVo.getId())
                    .sorted(Comparator.comparing(WechatMenu::getSort))
                    .map(secondLevelMenu -> {
                        WechatMenuVo secondLevelWechatMenuVo = new WechatMenuVo();
                        BeanUtils.copyProperties(secondLevelMenu,secondLevelWechatMenuVo);
                        return secondLevelWechatMenuVo;
                    })
                    .collect(Collectors.toList());
            firstLevelWechatMenuVo.setChildren(children);
            list.add(firstLevelWechatMenuVo);
        }
        return list;
    }


}
