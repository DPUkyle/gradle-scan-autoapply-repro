# `--scan` silently disables Develocity `Test` task features when a settings plugin applies Develocity

Minimal reproducer. It uses only public artifacts.

## Setup

- `build-logic/` contains a settings convention plugin, `repro.develocity-conventions`, that calls
  `settings.plugins.apply(DevelocityPlugin)`. Its only connection to Develocity is an `implementation`
  dependency on `com.gradle:develocity-gradle-plugin:4.5.0`, the same version Gradle 9.7.1 auto-applies.
- The root `settings.gradle` uses that plugin the "legacy" way: it puts it on the `buildscript` classpath and then
  calls `apply plugin:`. So `develocity-gradle-plugin` reaches the settings classpath only transitively.
- `build.gradle` configures `develocity.testRetry` on the `test` task, but only when the
  `DevelocityTestConfiguration` extension is present (typical plugin code).
- `FlakyTest` fails on its first attempt and passes on any retry.

## Steps

```shell
./gradlew -p build-logic publish     # publishes the convention plugin to ./local-repo

./gradlew test                       # extension present, test retried, BUILD SUCCESSFUL
./gradlew test --scan                # extension MISSING, no retry, BUILD FAILED
```

Output:

```text
$ ./gradlew test
[repro] Test task 'develocity' extension present: true
FlakyTest > failsOnFirstAttemptOnly() FAILED
2 tests completed, 1 failed
BUILD SUCCESSFUL

$ ./gradlew test --scan
[repro] Test task 'develocity' extension present: false
FlakyTest > failsOnFirstAttemptOnly() FAILED
> Task :test FAILED
BUILD FAILED
```

## Workaround

Uncomment the direct `classpath 'com.gradle:develocity-gradle-plugin:4.5.0'` line in `settings.gradle`.
`./gradlew test --scan` then keeps the extension and the build passes on retry.

## Notes

- If the convention plugin is applied through the settings `plugins {}` block instead (for example from an
  included build), the problem does **not** occur. Develocity is then already applied when Gradle decides
  whether to auto-apply it.
- The problem is not caused by a version mismatch: the transitive version is the same one Gradle auto-applies.
