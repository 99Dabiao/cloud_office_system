package com.office.process.service;

/**
 * @ClassName WeChatMessageService
 * @Description TODO 消息推送
 * @Author Dabiao
 * @Date 2023/4/18 12:45
 * @Version 1.0
 */
public interface WeChatMessageService {
    /**
     * 推送待审批人员
     * @param processId
     * @param userId
     * @param taskId
     */
    void pushPendingMessage(Long processId, Long userId, String taskId);

    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
