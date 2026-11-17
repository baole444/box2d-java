/**
 * Rigid body simulation: worlds, bodies and their definitions.
 * <p>
 * <b>Invalid input:</b>
 * <ul>
 *     <li>
 *     Box2D validates the values passed to it.
 *     When operation is given an invalid value (such as NaN, infinity, or value outside the documented range), it will reject the input and kept the previous value.
 *     </li>
 *     <li>
 *     A world or body created from a definition holding an invalid value is invalid from the start, its {@code isValid()} returns false.
 *     Each rejection is logged, see {@link org.box2d.Box2D#logStream(java.io.PrintStream)} to view the log.
 *     </li>
 * </ul>
 * <p>
 * Methods therefore document only their valid range and any value that is clamped rather than rejected.
 */
package org.box2d.dynamics;
