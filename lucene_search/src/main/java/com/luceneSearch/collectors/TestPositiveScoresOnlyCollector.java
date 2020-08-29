package com.luceneSearch.collectors;

import org.apache.lucene.search.CachingCollector;
import org.apache.lucene.search.PositiveScoresOnlyCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;

public class TestPositiveScoresOnlyCollector {
    public static void main(String[] args) {
        new PositiveScoresOnlyCollector(TopScoreDocCollector.create(10, 0));
        new TotalHitCountCollector();
        CachingCollector.create(new TotalHitCountCollector(), false, -1);
    }
}
