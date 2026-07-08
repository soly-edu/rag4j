package soly.dev.rag.dispatcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import soly.dev.rag.service.DocumentTaskProcessor;

@Component
@ConditionalOnProperty(name = "rag.dispatcher.type", havingValue = "async", matchIfMissing = true)
public class AsyncDocTaskDispatcher implements DocumentTaskDispatcher{

    private final DocumentTaskProcessor processor;

    public AsyncDocTaskDispatcher(DocumentTaskProcessor processor) {
        this.processor = processor;
    }

    @Override
    @Async
    public void taskDispatch(String uniqueFilename, String namespace, String embeddingModel) {
        processor.taskExecute(uniqueFilename, namespace, embeddingModel);
    }
}
