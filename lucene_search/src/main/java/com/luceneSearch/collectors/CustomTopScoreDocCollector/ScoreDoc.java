package com.luceneSearch.collectors.CustomTopScoreDocCollector;

public class ScoreDoc implements Comparable<ScoreDoc> {
    int doc;
    Float score;

    public ScoreDoc(int d, Float s) {
        doc = d;
        score = s;
    }

    @Override
    public int compareTo(ScoreDoc o) {
        if (score == o.score) {
            return 0;
        } else if (score < o.score) {
            return 1;
        };

        return -1;
    }
}
