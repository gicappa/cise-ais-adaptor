package eu.cise.adaptor.tbsalling;

import java.util.Comparator;
import java.util.Map;

class ValueComparator<T, E extends Comparable<? super E>>
        implements Comparator<Map.Entry<T, E>> {

    @Override
    public int compare(Map.Entry<T, E> left, Map.Entry<T, E> right) {
        return left.getValue().compareTo(right.getValue());
    }
}