package soly.dev.rag.splitter;

import java.util.List;

public interface DocumentSplitter {
    /**
     * 将长文本切分为较短的文本片段
     * @param fullContent 完整的原始长文本
     * @return 切分后的文本片段列表
     */
    List<String> split(String fullContent);

    /**
     * 策略名称，用于在配置或数据库中指定当前使用的切分算法
     */
    String getStrategyName();
}
