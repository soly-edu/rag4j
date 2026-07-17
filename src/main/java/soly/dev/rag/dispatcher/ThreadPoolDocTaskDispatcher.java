package soly.dev.rag.dispatcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import soly.dev.rag.entity.UploadRequestContext;
import soly.dev.rag.service.DocumentTaskProcessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "rag.dispatcher.type", havingValue = "threadPool")
public class ThreadPoolDocTaskDispatcher implements DocumentTaskDispatcher{

    private final ExecutorService executorService;
    private final DocumentTaskProcessor processor;

    public ThreadPoolDocTaskDispatcher(DocumentTaskProcessor processor) {
        this.processor = processor;
        this.executorService = new ThreadPoolExecutor(
                4, 4,
                30*1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void taskDispatch(UploadRequestContext requestContext) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                processor.taskExecute(requestContext);
            }
        });
    }
}
