package com.sisipapa.springbatch.custom;

import org.springframework.batch.item.file.transform.LineAggregator;

public class CustomPassThroughLineAggregator<T> implements LineAggregator<T> {

    @Override
    public String aggregate(T item){
        return item.toString();
    }
}
