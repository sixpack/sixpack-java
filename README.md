# sixpack-java

[![Build Status](https://travis-ci.org/seatgeek/sixpack-java.svg?branch=master)](https://travis-ci.org/seatgeek/sixpack-java) [![Coverage Status](https://coveralls.io/repos/seatgeek/sixpack-java/badge.svg?branch=master&service=github&t=rRY7ax)](https://coveralls.io/github/seatgeek/sixpack-java?branch=master)

A Java client for SeatGeek's Sixpack a/b testing framework: https://github.com/seatgeek/sixpack

### Installing

Sixpack-java is currently only being deployed to maven snapshots, to use it, add the following dependency to your build.gradle:

```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

dependencies {
    compile 'com.seatgeek:sixpack-java:0.4-SNAPSHOT'
}
```

or, if you're a maven user:

```xml
<dependency>
  <groupId>com.seatgeek</groupId>
  <artifactId>sixpack-java</artifactId>
  <version>0.4-SNAPSHOT</version>
</dependency>
```

### Overview

The Sixpack client has some nomenclature to be familiar with...

- A _Sixpack server_ is the deployment of the [Sixpack-server](https://github.com/seatgeek/sixpack)
    that will be hosting your test results
- An _experiment_ represents a single test in the _Sixpack server_. It can have multiple _alternatives_
- An _alternative_ is one potential result returned to the client when _participating_ in an _experiment_
- You start a test by _participating_ in an _experiment_ with _alternatives_
- Once the server has selected an _alternative_ for you, you can _convert_ the _experiment_ when the user
    performs the action that you're measuring

### Getting Started

After [installing](#installing)...

The first thing to do is create a `Sixpack` client using the `SixpackBuilder`:

```java
    Sixpack sixpack = new SixpackBuilder()
            .setSixpackUrl("http://api.mycompany.com/sixpack")
            .setClientId(user != null ? user.sixpackId : getCachedClientId())
            .build();
```

It is recommended that you maintain a singleton instance of `Sixpack` with the DI library of your choice.

### Creating Experiments

0. Create a new experiment from your `Sixpack` instance:

    ```java
        Experiment colorsExperiment = Sixpack.experiment()
                .withName("colors")
                .withAlternatives(
                    new Alternative("control"),
                    new Alternative("red"),
                    new Alternative("green"),
                    new Alternative("blue")
                )
                .build();
    ```
0. Participate in that new `Experiment` by calling `participate()`

    ```java
        ParticipatingExperiment participatingExperiment = colorsExperiment.participate();
    ```
0. When the user performs the action measured in the test, convert the experiment

    ```java
        participatingExperiment.convert();
    ```

### Contributing

0. Fork this repo and clone your fork
0. Make your desired changes
0. Add tests for your new feature and ensure all tests are passing
0. Commit and push
0. Submit a Pull Request through Github's interface and a project maintainer will decide your changes
    fate.

_note: issues can be submitted via [github issues](https://github.com/seatgeek/sixpack-java/issues/new)_

### License

Sixpack-Java is released under the [BSD 2-Clause License](http://opensource.org/licenses/BSD-2-Clause)
