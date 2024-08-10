package xyz.bobkinn.indigodataio;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Copy of org.apache.commons.lang3.tuple.Pair
 * @param <L> left element
 * @param <R> right element
 * @see ImmutablePair
 */
@SuppressWarnings("unused")
public abstract class Pair<L, R> implements Map.Entry<L, R>, Serializable {
    @Serial
    private static final long serialVersionUID = 44678L;
    public abstract L getLeft();
    public abstract R getRight();

    public boolean isEmpty(){
        return getLeft() == null && getRight() == null;
    }

    public boolean hasAny(){
        return getLeft() != null || getRight() != null;
    }

    public boolean isFull(){
        return getLeft() != null && getRight() != null;
    }

    @Override
    public String toString() {
        return "(" + getLeft() + ',' + getRight() + ')';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Map.Entry<?,?> pair){
            return Objects.equals(getLeft(), pair.getKey()) && Objects.equals(getRight(), pair.getValue());
        }
        return false;
    }

    public L getFirst(){
        return getLeft();
    }

    public R getSecond(){
        return getRight();
    }
    public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> toMap() {
        return Collectors.toMap(Pair::getFirst, Pair::getSecond);
    }

    public Map.Entry<L, R> toEntry(){
        return Map.entry(getKey(), getValue());
    }

    // Map.Entry

    @Override
    public L getKey() {
        return getLeft();
    }

    @Override
    public R getValue() {
        return getRight();
    }
    /**
     * Creates an immutable pair of two objects inferring the generic types.
     * @param <L> the left element type
     * @param <R> the right element type
     * @param left  the left element, may be null
     * @param right  the right element, may be null
     * @return a pair formed from the two parameters, not null
     */
    @Contract("_, _ -> new")
    public static <L, R> @NotNull Pair<L, R> of(final L left, final R right) {
        return ImmutablePair.of(left, right);
    }

    /**
     * Creates an immutable pair from an existing pair.
     *
     * @param <L> the left element type
     * @param <R> the right element type
     * @param pair the existing pair.
     * @return a pair formed from the two parameters, not null
     */
    @Contract("_ -> new")
    public static <L, R> @NotNull Pair<L, R> of(final Map.Entry<L, R> pair) {
        return ImmutablePair.of(pair);
    }
}
