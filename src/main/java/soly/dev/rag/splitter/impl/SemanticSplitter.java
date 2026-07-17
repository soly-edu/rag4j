package soly.dev.rag.splitter.impl;

import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.splitter.DocumentSplitter;

import java.util.List;

/**
 * 语义切分器
 * TODO 待实现
 */
public class SemanticSplitter implements DocumentSplitter {
    @Override
    public List<String> split(String fullContent) {
        return List.of();
    }

    @Override
    public String getStrategyName() {
        return SplitterStrategy.SEMANTIC.getName();
    }
}
