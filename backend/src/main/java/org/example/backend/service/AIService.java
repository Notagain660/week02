package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import cn.hutool.json.JSONUtil;
import org.example.backend.enums.StatusCode;
import org.example.backend.utilities.BusiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class AIService {

    private final ChatModel chatModel;
    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    public String generateDescription(String itemName, String place, String userDesc, LocalDateTime itemTime) {
        // 精心构造提示词，指导AI生成更准确的描述
        try {
        String prompt = String.format("""
                你是一个专业的失物招领助手。请根据以下信息，为物品生成一段生动、简洁的描述，以便失主能够快速识别。
                物品名称：%s
                丢失/拾取时间：%s
                丢失/拾取地点：%s
                用户补充：%s
                
                要求：
                1. 语气温和、积极。
                2. 描述中突出物品的关键特征。
                3. 直接返回描述文本，不要包含任何多余的解释、引号或JSON格式。
                示例输出格式：“这是一款银色的MacBook Pro，2026年3月29日10:16分在第二食堂二楼靠窗位置发现，外壳左上角有一个心形贴纸。”
                """, itemName, itemTime, place, userDesc);

        ChatResponse response = chatModel.call(new Prompt(prompt));
        return response.getResult()
                .getOutput()
                .getText();
        } catch (Exception e) {
            if (e.getMessage().contains("ReadTimeout") || e.getCause() instanceof java.net.SocketTimeoutException) {
                // 超时异常，返回特定提示或抛出业务异常
                throw new BusiException(StatusCode.AI_TIMEOUT);
            }
            throw new BusiException(StatusCode.AI_ERROR);
        }
    }

    public String generateStatistics(List<Map<String, Object>> items) {
        String clue = JSONUtil.toJsonStr(items);
        String prompt = "以下是一组校园失物招领平台丢失物品的统计（物品名称:次数）：" + clue +
                "请用一段话（不超过100字）总结哪些物品丢失最多，可能的原因是什么。不要输出JSON，只输出文本。";

        ChatResponse response = chatModel.call(new Prompt(prompt));
        String test = response.getResult()
                .getOutput()
                .getText();
        log.info(test);
        return test;
    }

    public String generateStatisticsPlace(List<Map<String, Object>> items) {
        String clue = JSONUtil.toJsonStr(items);
        String prompt = "以下是一组校园失物招领平台丢失物品的地点的统计（物品名称:次数）：" + clue +
                "请用一段话（不超过100字）总结哪些地点丢失物品最多，可能的原因是什么。不要输出JSON，只输出文本。";
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return response.getResult()
                .getOutput()
                .getText();
    }

}
