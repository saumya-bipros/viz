package com.vizzionnaire.server.common.data.query;

public interface SimpleKeyFilterPredicate<T> extends KeyFilterPredicate {

    FilterPredicateValue<T> getValue();

}
