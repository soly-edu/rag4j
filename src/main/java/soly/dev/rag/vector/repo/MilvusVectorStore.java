package soly.dev.rag.vector.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import soly.dev.rag.constants.VectorStoreType;
import soly.dev.rag.entity.KnowledgeChunk;
import soly.dev.rag.entity.ScoredKnowledgeChunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "rag.vector-store.type", havingValue = "milvus")
public class MilvusVectorStore implements VectorStoreRepository {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    // TODO 后续升级为pool
    private MilvusClientV2 milvusClient;

    @Value("${rag.vector-store.milvus.host}")
    private String host;
    @Value("${rag.vector-store.milvus.port}")
    private int port;
    @Value("${rag.vector-store.milvus.token}")
    private String token;
    @Value("${rag.embedding.models.local_onnx.dimension}")
    private int dimension;

    @PostConstruct
    public void init() {
        milvusClient = new MilvusClientV2(ConnectConfig.builder()
                .uri(String.format("http://%s:%d", host, port))
                // .token(token)
                .build());
    }


    @Override
    public void saveAll(String namespace, String embeddingModel, List<KnowledgeChunk> chunks) {
        String collectionName = String.format("%s_%s", namespace, embeddingModel);
        HasCollectionReq hasCollectionReq = HasCollectionReq.builder()
                .collectionName(collectionName)
                .build();
        if (Boolean.FALSE.equals(milvusClient.hasCollection(hasCollectionReq))) {
            createCollection(collectionName);
        }

        List<JsonObject> data = chunks.stream().map(chunk -> GSON.toJsonTree(chunk).getAsJsonObject()).toList();
        InsertReq insertReq = InsertReq.builder().collectionName(collectionName).data(data).build();
        milvusClient.insert(insertReq);
    }

    @Override
    public List<ScoredKnowledgeChunk> searchTopK(String namespace, String embeddingModel, int topK, float[] queryVector) {
        String collectionName = String.format("%s_%s", namespace, embeddingModel);
        HasCollectionReq hasCollectionReq = HasCollectionReq.builder().collectionName(collectionName).build();
        if (Boolean.FALSE.equals(milvusClient.hasCollection(hasCollectionReq))) {
            throw new RuntimeException("Collection not found: " + collectionName);
        }
        SearchReq searchReq = SearchReq.builder()
                .collectionName(collectionName)
                .data(Collections.singletonList(new FloatVec(queryVector)))
                .annsField(KnowledgeChunk.VECTOR)
                .limit(topK)
                .outputFields(KnowledgeChunk.getAllFieldNames())
                .build();
        SearchResp searchResp = milvusClient.search(searchReq);
        return convertResults(searchResp);
    }

    @Override
    public String getStoreType() {
        return VectorStoreType.MILVUS.toString();
    }

    private void createCollection(String collectionName) {
        // 创建表结构
        CreateCollectionReq.CollectionSchema schema = MilvusClientV2.CreateSchema();
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.ID).dataType(DataType.Int64).isPrimaryKey(Boolean.TRUE).autoID(Boolean.TRUE).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.VECTOR).dataType(DataType.FloatVector).dimension(dimension).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_ID).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_CONTENT).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_NAMESPACE).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_HASH).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_INDEX).dataType(DataType.Int32).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_SIZE).dataType(DataType.Int32).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_SPLIT_RULE).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.CHUNK_EMBEDDING_TYPE).dataType(DataType.VarChar).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.SOURCE_META).dataType(DataType.JSON).build());
        schema.addField(AddFieldReq.builder().fieldName(KnowledgeChunk.EXT_ATTRIBUTES).dataType(DataType.JSON).isNullable(Boolean.TRUE).build());
        // 创建索引
        IndexParam indexParam = IndexParam.builder().fieldName(KnowledgeChunk.VECTOR).metricType(IndexParam.MetricType.COSINE).build();
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName(collectionName)
                .collectionSchema(schema)
                .indexParam(indexParam)
                .build();
        milvusClient.createCollection(createCollectionReq);
    }

    private List<ScoredKnowledgeChunk> convertResults(SearchResp searchResp) {
        List<SearchResp.SearchResult> searchResults = searchResp.getSearchResults().get(0);
        // 构建结果队列
        ArrayList<ScoredKnowledgeChunk> scoredKnowledgeChunks = new ArrayList<>(searchResults.size());
        for (SearchResp.SearchResult result : searchResults) {
            // Milvus SDK使用均为GSON，序列化和反序列化时也要使用GSON，否则存在类型转换问题
            JsonElement jsonElement = GSON.toJsonTree(result.getEntity());
            KnowledgeChunk chunk = GSON.fromJson(jsonElement, KnowledgeChunk.class);
            scoredKnowledgeChunks.add(new ScoredKnowledgeChunk(chunk, result.getScore()));
        }
        return scoredKnowledgeChunks;
    }
 }
