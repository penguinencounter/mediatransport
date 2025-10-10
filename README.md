# mediatransport

[![powered by hexdoc](https://img.shields.io/endpoint?url=https://hexxy.media/api/v0/badge/hexdoc?label=1)](https://github.com/hexdoc-dev/hexdoc)

hex casting - fsb integration (the dev version of 1.20.1 figura)

## To build:
1. Clone **penguinencounter/Figura** alongside this project and checkout `maven-server-common`;
2. run `./gradlew publishToMavenLocal` in there
3. sync and build this project.

## Want to depend on this?
It's on [penguinencounter/mvn](https://github.com/penguinencounter/mvn):

```kts
maven {
    // for mediatransport
    name = "penguinencounter releases"
    url = "https://penguinencounter.github.io/mvn/releases"
}
maven {
    // for FSB
    name = "penguinencounter snapshots"
    url = "https://penguinencounter.github.io/mvn/snapshots"
}
```

Coordinates:
- `penguinencounter.mediatransport:mediatransport-common`
- `penguinencounter.mediatransport:mediatransport-fabric`
- `penguinencounter.mediatransport:mediatransport-forge`
