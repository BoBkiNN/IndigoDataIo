package xyz.bobkinn.indigodataio;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Pair where values are final and cannot be set
 * setValue will throw UnsupportedOperationException
 */
@RequiredArgsConstructor
public class ImmutablePair<L, R> extends Pair<L, R>{
    private final L left;
    private final R right;

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException("setValue(R) on ImmutablePair");
    }

    /**
     * Creates an immutable pair of two objects inferring the generic types
     * @param <L> the left element type
     * @param <R> the right element type
     * @param left  the left element, may be null
     * @param right  the right element, may be null
     * @return a pair formed from the two parameters, not null
     */
    @Contract("_, _ -> new")
    public static <L, R> @NotNull ImmutablePair<L, R> of(final L left, final R right) {
        return new ImmutablePair<>(left, right);
    }

    /**
     * <p>Creates an immutable pair from an existing pair.</p>
     * @param <L> the left element type
     * @param <R> the right element type
     * @param pair the existing pair.
     * @return a pair formed from the two parameters, not null
     */
    @Contract("_ -> new")
    public static <L, R> @NotNull ImmutablePair<L, R> of(final Map.Entry<L, R> pair) {
        final L left;
        final R right;
        if (pair != null) {
            left = pair.getKey();
            right = pair.getValue();
        } else {
            left = null;
            right = null;
        }
        return new ImmutablePair<>(left, right);
    }
}
