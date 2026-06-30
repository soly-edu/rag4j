package soly.dev.rag.controller;

import org.springframework.web.bind.annotation.*;
import soly.dev.rag.entity.SearchResult;
import soly.dev.rag.service.QAService;

import java.util.List;

@RestController
@RequestMapping("/qa")
public class QAController {

    private final QAService qaService;

    public QAController(QAService qaService) {
        this.qaService = qaService;
    }

    @GetMapping(value = "/search")
    public String search(@RequestParam(value = "question") String question, @RequestParam(value = "namespace", defaultValue = "default") String namespace, @RequestParam(value = "topK", defaultValue = "3") int topK) {

        List<SearchResult> searchResult = qaService.searchSimilarTopK(question, namespace, topK);
        return qaService.askLLM(question, searchResult);
    }


}
