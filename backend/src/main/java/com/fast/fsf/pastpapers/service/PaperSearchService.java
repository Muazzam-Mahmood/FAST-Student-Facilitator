package com.fast.fsf.pastpapers.service;

import com.fast.fsf.pastpapers.adapter.ApprovedPaperCatalog;
import com.fast.fsf.pastpapers.criterion.PaperSearchCriterion;
import com.fast.fsf.pastpapers.domain.PastPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching past papers based on specific criteria.
 */
@Service
public class PaperSearchService {

    private final ApprovedPaperCatalog paperCatalog;

    @Autowired
    public PaperSearchService(ApprovedPaperCatalog paperCatalog) {
        this.paperCatalog = paperCatalog;
    }

    public List<PastPaper> search(PaperSearchCriterion criterion) {
        // Fetches all approved papers (via adapter) and filters in memory using the strategy
        return paperCatalog.findAllApproved().stream()
                .filter(criterion::matches)
                .collect(Collectors.toList());
    }
}
