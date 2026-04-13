package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import cn.hutool.json.JSONUtil;
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

    public String generateStatistics(List<Map<String, Object>> items) {
        String clue = JSONUtil.toJsonStr(items);
        String prompt = String.format("""
                你是一个专业的词义近似和统计助手，以下是校园失物招领平台中丢失物品的原始统计
                （物品名称: 丢失次数）：%s
                
                请完成以下任务：
                         1. 将语义相近的物品名称合并为一个通用类别（例如“伞”和“雨伞”合并为“雨伞”）。
                         2. 合并后，按丢失次数从高到低排序，输出丢失次数最多的前10个物品类别，
                            格式为 JSON 对象，键为合并后的物品类别，值为总次数。
                         3. 在 JSON 对象之外，用一句简短的话总结丢失最多的物品类别和可能原因。
                         4. 返回格式必须为 JSON，结构如下：
                         {
                           "items": [
                             {"name": "校园卡", "count": 20},
                             {"name": "雨伞", "count": 20},
                             ...
                           ],
                           "summary": "校园卡和雨伞是丢失最多的物品，可能因为雨天和频繁使用校园卡导致。"
                         }
                """, clue);
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return response.getResult()
                .getOutput()
                .getText();
    }

    public String generateStatisticsPlace(List<Map<String, Object>> items) {
        String clue = JSONUtil.toJsonStr(items);
        String prompt = String.format("""
                你是一个专业的词义近似和统计助手，以下是校园失物招领平台中丢失物品的地点的原始统计
                （物品名称: 丢失次数）：%s
                
                请完成以下任务：
                         1. 将语义相近的物品名称合并为一个通用类别（例如“伞”和“雨伞”合并为“雨伞”）。
                         2. 合并后，按丢失次数从高到低排序，输出丢失次数最多的前10个物品类别，
                            格式为 JSON 对象，键为合并后的物品类别，值为总次数。
                         3. 在 JSON 对象之外，用一句简短的话总结丢失物品最多的地点的类别和可能原因。
                         4. 返回格式必须为 JSON，结构如下：
                         {
                           "items": [
                             {"name": "西三饭堂", "count": 20},
                             {"name": "教学三号楼", "count": 20},
                             ...
                           ],
                           "summary": "西三饭堂和教学三号楼是丢失物品最多的地方，可能因为西三食堂和教学三号楼人员流动大导致。"
                         }
                """, clue);
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return response.getResult()
                .getOutput()
                .getText();
    }

}
