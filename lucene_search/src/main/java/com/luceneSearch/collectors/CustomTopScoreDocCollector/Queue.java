package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import java.util.ArrayList;

public abstract class Queue<T> {
    public ArrayList<T> items;
    public abstract void addDoc(T doc);
}
