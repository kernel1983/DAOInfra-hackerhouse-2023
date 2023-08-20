package com.bfit.recommand.service;

import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.BaseAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContentBasedRecommendationUtil {

    // 物品数据，key是物品ID，value是物品的描述文本
    private Map<Long, String> itemDescriptions;

    public ContentBasedRecommendationUtil(Map<Long, String> itemDescriptions) {
        this.itemDescriptions = itemDescriptions;
    }

    // 使用Ansj分词进行文本处理
    private List<String> tokenize(String text) {
        Result result = BaseAnalysis.parse(text);
        List<String> tokens = new ArrayList<>();
        result.forEach(term -> tokens.add(term.getName()));
        return tokens;
    }

    // 计算文本相似度（使用简单的Jaccard相似性）
    private double calculateSimilarity(List<String> tokens1, List<String> tokens2) {
        Set<String> set1 = new HashSet<>(tokens1);
        Set<String> set2 = new HashSet<>(tokens2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    // 获取给定物品的推荐物品列表
    public List<Long> getRecommendations(long itemId) {
        String targetDescription = itemDescriptions.get(itemId);
        List<String> targetTokens = tokenize(targetDescription);

        Map<Long, Double> scores = new HashMap<>();

        for (long id : itemDescriptions.keySet()) {
            if (id != itemId) {
                String description = itemDescriptions.get(id);
                List<String> tokens = tokenize(description);
                double similarity = calculateSimilarity(targetTokens, tokens);
                scores.put(id, similarity);
            }
        }

        List<Long> recommendations = new ArrayList<>(scores.keySet());
        recommendations.sort((id1, id2) -> Double.compare(scores.get(id2), scores.get(id1)));

        return recommendations;
    }

    public static void main(String[] args) {
        // 示例数据
        Map<Long, String> itemDescriptions = new HashMap<>();



        itemDescriptions.put(101L, "这是一本精彩的小说，讲述了一个动人的故事。");
        itemDescriptions.put(102L, "这款手机具有出色的性能和优美的设计。");
        itemDescriptions.put(103L, "这部电影是一部经典之作，深受观众喜爱。");
        itemDescriptions.put(104L, "这个城市是一个国际化金融之都，深世人众喜爱。");
        itemDescriptions.put(105L, "这个景区具有世界上少有的地形，有玩过的人们都感觉到很惊艳。");
        itemDescriptions.put(106L, "这款电脑很好用。");
        itemDescriptions.put(107L, "这个区块链技术解决了大家的问题。");


        ContentBasedRecommendationUtil cb = new ContentBasedRecommendationUtil(itemDescriptions);

        int itemToRecommend = 106;
        List<Long> recommendations = cb.getRecommendations(itemToRecommend);

        System.out.println("推荐给物品 " + itemToRecommend + " 的物品列表：");
        for (long itemId : recommendations) {
            System.out.println(itemId);
        }
    }
}

