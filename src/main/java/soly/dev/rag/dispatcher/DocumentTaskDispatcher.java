package soly.dev.rag.dispatcher;

public interface DocumentTaskDispatcher {

    /**
     * 文档分片任务分发
     */
    void taskDispatch(String uniqueFilename, String namespace);
}
