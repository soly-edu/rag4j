package soly.dev.rag.splitter.impl;

import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.splitter.DocumentSplitter;

import java.util.List;

/**
 * Token 限制切分器
 * TODO 待实现
 */
public class TokenWindowSplitter implements DocumentSplitter {
    @Override
    public List<String> split(String fullContent) {
        return List.of();
    }

    @Override
    public String getStrategyName() {
        return SplitterStrategy.TOKEN_WINDOW.getName();
    }
}
