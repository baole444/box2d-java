<div style="text-align: center;" align="center">

# Box2D Java

**Java binding for [Box2D](https://github.com/erincatto/box2d).**

</div>

> [!CAUTION]
> This is an extremely experimental binding, not intended for production.
> If there are any bugs, please report them in Issue section.

## Requirements

This library use Java FFM API, `java.lang.foreign`, [JDK 25](https://adoptium.net/temurin/releases?version=25&os=any&arch=any) is required.

As a native library is loaded during runtime, native access must be granted with one of these JVM arguments:
- On the module path: `--enable-native-access=org.box2d`
- On the classpath: `--enable-native-access=ALL-UNANMED`

