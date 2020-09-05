package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreMode;

public abstract class TopDocCollector implements Collector {
    public ScorePriorityQueue pq;
    public int totalHits = 0;

    public TopDocCollector(Integer top) {
        pq = new ScorePriorityQueue(top);
    }


    @Override
    public ScoreMode scoreMode() {
        // think about HitsThresholdChecker
        return ScoreMode.COMPLETE;
    }
}
