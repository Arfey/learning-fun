package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import java.util.Arrays;
import java.util.List;

public class TestTopK {
    public static void main(String[] args) {
        List<Integer> items = Arrays.asList(8, 1, 4, 3, 5, 2);
        List<Integer> tempArr = items.subList(0, 3);


        for (Integer i = tempArr.size() - 1; i < items.size(); i++) {
            Integer minValue = tempArr.get(0);
            Integer minIndex = 0;
            for (Integer j = 1; j < tempArr.size(); j++) {
                if (tempArr.get(j) < minValue) {
                    minValue = tempArr.get(j);
                    minIndex = j;
                }
            }

            if (minValue < items.get(i)) {
                tempArr.set(minIndex, items.get(i));
            }
        }

        System.out.println(Arrays.toString(tempArr.toArray()));
    }
}
