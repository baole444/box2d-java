/*
 * Stand-in <math.h> for binding generation only.
 *
 * Bindings are generated with -nostdinc so that no host C runtime is parsed. Box2D's public headers include <math.h> solely for the bodies of their inline functions,
 * which jextract never binds, so declarations are enough. Add any function a newer Box2D header calls here.
 */
#pragma once

float sqrtf(float x);
double sqrt(double x);
double sin(double x);
double cos(double x);
double log(double x);
