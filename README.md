# IMDLib (itsmeowdevlib)

Library for itsmeow mods. Proper use is to shade the lib inside a relocated package.

## Usage

There is a buildscript provided to do this automatically, just include this in your `build.gradle` and `gradle.properties`.

### For `gradle.properties`
---

```java
imdlib_version=(VERSION HERE)
```

You can view available versions [on my maven](https://maven.itsmeow.dev/dev/itsmeow/imdlib/imdlib/).

### For `build.gradle`


```java
apply from: 'https://maven.itsmeow.dev/scripts/imdlib.gradle'
```

This buildscript will:
 - Replace the default/null classifier jar with a shade jar including imdlib as a relocated package. 
 - On build, relocate imdlib to `${project.maven_group}.imdlib` and minimize it to use only referenced classes. 
 - Include imdlib as a development dependency with a non-transitive `implementation` include.
 - Create a configuration called libShade to handle shading for this, and applies jengelman's shadow plugin version 4.0.4.
 
## Features

This library includes:
 - Tools for replacing entity models with new ones, and the configuration of such
 - Entity registration, management, and configuration
 - Various utilities
 - Lambda builder-based entity renderers