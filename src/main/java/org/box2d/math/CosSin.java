package org.box2d.math;

/**
 * Cosine and sine pair, as computed by {@link org.box2d.Box2D#computeCosSin(float)} with Box2D's approximation designed for cross-platform determinism.
 * <p>
 * Mirror of native {@code b2CosSin}.
 * @param cosine the cosine
 * @param sine the sine
 */
public record CosSin(float cosine, float sine) {
}
