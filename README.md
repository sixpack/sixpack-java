# sixpack-java

[![Build Status](https://magnum.travis-ci.com/seatgeek/sixpack-java.svg?token=ycL4XWSrwx9ci6onAtBb&branch=master)](https://magnum.travis-ci.com/seatgeek/sixpack-java) [![Coverage Status](https://coveralls.io/repos/seatgeek/sixpack-java/badge.svg?branch=master)] (https://coveralls.io/r/seatgeek/sixpack-java?branch=master)

A Java client for SeatGeek's Sixpack a/b testing framework: https://github.com/seatgeek/sixpack

# Installing

Sixpack-java is currently only being deployed to maven snapshots, to use it, add the following to your build.grade:

```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

dependencies {
    compile 'com.seatgeek:sixpack:0.0.1-SNAPSHOT'
}
```

# Getting Started

```java
    Sixpack sixpack = new SixpackBuilder()
            .setSixpackUrl("http://api.mycompany.com/sixpack")
            .setClientId(user != null ? user.sixpackId : getCachedClientId())
            .build();
```

# Creating Experiments

```java
    Experiment colorsExperiment = Sixpack.experiment()
            .withName("Colors")
            .withAlternative(new Alternative("Red"))
            .withAlternative(new Alternative("Green"))
            .withAlternative(new Alternative("Blue"))
            .withAlternative(new Alternative("Control"))
            .build();
```

# License

Sixpack-Java is released under the [BSD 2-Clause License](http://opensource.org/licenses/BSD-2-Clause)
