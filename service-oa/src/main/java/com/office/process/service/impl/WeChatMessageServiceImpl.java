package com.office.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.office.auth.service.SysUserService;
import com.office.model.process.Process;
import com.office.model.process.ProcessTemplate;
import com.office.model.system.SysUser;
import com.office.process.service.ProcessService;
import com.office.process.service.ProcessTemplateService;
import com.office.process.service.WeChatMessageService;
import com.office.security.custom.LoginUserInfoHelper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName WeChatMessageServiceImpl
 * @Description TODO
 * @Author Dabiao
 * @Date 2023/4/18 12:46
 * @Version 1.0
 */
@Slf4j
@Service
public class WeChatMessageServiceImpl implements WeChatMessageService  {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private ProcessService processService;

    @Resource
    private ProcessTemplateService processTemplateService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        //查询流程信息
        Process process = processService.getById(processId);
        //查询推送人信息
        SysUser sysUser = sysUserService.getById(userId);
        //查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        //获取提交审批人信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        String openId = sysUser.getOpenId();
        if (StringUtils.isEmpty(openId))
        {
            openId = "oyQyA6c4hJ5RV2zcDfnH_H3C-PQ4";
        }
        //设置消息发送消息
        WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId("sRjIThKi-lwQEU2-6Cz99_33N3Octj1YPQIbfrNXCm8")
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/" + processId + "/" + taskId)
                .build();

        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuilder content = new StringBuilder();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }

        //设置模板参数值
        wxMpTemplateMessage
                .addData(new WxMpTemplateData("first",submitSysUser.getName()+
                        "提交"+processTemplate.getName()+"，请注意查看。","#272727"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        //发送
        try {
            String sendTemplateMsg  = wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            log.info("推送消息返回：{}", sendTemplateMsg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = processService.getById(processId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "oyQyA6c4hJ5RV2zcDfnH_H3C-PQ4";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("GftuGh61WiDfWCla8dpZp2xDxcQxN0OaIakqOik5tAs")//模板id
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/"+processId+"/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = null;
        try {
            msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        log.info("推送消息返回：{}", msg);
    }

}

