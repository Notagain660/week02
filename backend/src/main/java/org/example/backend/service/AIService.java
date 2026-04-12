package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(rollbackFor = Exception.class)
public class AIService {

    private final ChatModel chatModel;

    public String generateDescription(String itemName, String place, String userDesc, LocalDateTime itemTime) {
        // 精心构造提示词，指导AI生成更准确的描述
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
    }

}
