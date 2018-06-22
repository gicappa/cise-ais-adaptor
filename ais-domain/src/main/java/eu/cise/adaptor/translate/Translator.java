package eu.cise.adaptor.translate;

/**
 * This is a generic abstraction on the concept of transforming an object of
 * type A into type B.
 * <p>
 * The overall process of the adaptor is mainly bound to a type transformation
 * so this abstraction gives a common way to name all the transformation steps.
 *
 * @param <A> source type
 * @param <B> destination type
 */
@FunctionalInterface
public interface Translator<A, B> {

    /**
     * Self descriptive. The method translate the type of the input from A
     * to B
     *
     * @param type of the value in input (A)
     * @return translated value in output type (B)
     */
    B translate(A type);
}
