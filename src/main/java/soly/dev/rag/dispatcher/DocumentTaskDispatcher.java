package soly.dev.rag.dispatcher;

import soly.dev.rag.entity.UploadRequestContext;

public interface DocumentTaskDispatcher {

    /**
     * 文档分片任务分发
     */
    void taskDispatch(UploadRequestContext requestContext);
}
