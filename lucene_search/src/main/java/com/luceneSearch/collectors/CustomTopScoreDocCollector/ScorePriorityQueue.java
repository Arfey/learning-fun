package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import java.util.ArrayList;
import java.util.Collections;

public class ScorePriorityQueue extends Queue<ScoreDoc> {
    // todo: move to iterate
    private Integer top_item = 10;
    protected ArrayList<ScoreDoc> items = new ArrayList<ScoreDoc>();

    public ScorePriorityQueue(Integer top) {
        top_item = top;
    }

    public void addDoc(ScoreDoc scoreDoc) {
        items.add(scoreDoc);

        System.out.println("Info: " + "max: " + top_item + " size: " + items.size() + " score: " + scoreDoc.score);

        if (items.size() > top_item) {
            Collections.sort(items);
            items.remove(top_item - 1);
        }
    }
}
