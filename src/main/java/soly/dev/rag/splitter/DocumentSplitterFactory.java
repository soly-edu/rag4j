package soly.dev.rag.splitter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soly.dev.rag.constants.SplitterStrategy;
import soly.dev.rag.constants.StaticRule;
import soly.dev.rag.entity.UploadRequestContext;
import soly.dev.rag.splitter.impl.SemanticSplitter;
import soly.dev.rag.splitter.impl.SlidingWindowSplitter;
import soly.dev.rag.splitter.impl.StaticRuleSplitter;
import soly.dev.rag.splitter.impl.TokenWindowSplitter;

@Component
public class DocumentSplitterFactory {

    @Value("${rag.splitter.static-rule.max-chunk-size}")
    private int maxChunkSize;
    @Value("${rag.splitter.sliding-window.window-size}")
    private int windowSize;
    @Value("${rag.splitter.sliding-window.overlap-size}")
    private int overlapSize;

    public DocumentSplitter getDocumentSplitter(UploadRequestContext requestContext) {
        SplitterStrategy splitterStrategy = requestContext.getSplitterStrategy();
        switch (splitterStrategy) {
            case STATIC_RULE -> {
                return createStaticRuleSplitter(requestContext.getStaticRule(), requestContext.getCustomPattern(), maxChunkSize);
            }
            case SLIDING_WINDOW -> {
                return createSlidingWindowSplitter(windowSize, overlapSize);
            }
            case TOKEN_WINDOW -> {
                return createTokenWindowSplitter();
            }
            case SEMANTIC -> {
                return createSemanticSplitter();
            }
        }
        throw new IllegalArgumentException("Unsupported splitter strategy: " + splitterStrategy);
    }

    private DocumentSplitter createStaticRuleSplitter(StaticRule staticRule, String customPattern, int maxChunkSize) {
        if (staticRule == StaticRule.CUSTOM) {
            if (StringUtils.isBlank(customPattern)) {
                throw new IllegalArgumentException("Custom pattern must be provided for CUSTOM static rule.");
            }
            return new StaticRuleSplitter(customPattern, maxChunkSize);
        }
        return new StaticRuleSplitter(staticRule, maxChunkSize);
    }

    private DocumentSplitter createSlidingWindowSplitter(int windowSize, int overlapSize) {
        return new SlidingWindowSplitter(windowSize, overlapSize);
    }
    private DocumentSplitter createTokenWindowSplitter() {
        return new TokenWindowSplitter();
    }
    private DocumentSplitter createSemanticSplitter() {
        return new SemanticSplitter();
    }
}
