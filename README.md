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

## Adding support for your own types
> This is a rough draft; you'll kinda have to wing it. Look at `HexposeTypes.kt` if you want an example.

1. Add the two mavens above and the appropriate dependencies.
2. Implement `Encoder` and `Decoder` in a new class (conventionally named `(mod name)Conversions`).
3. Choose type IDs - see `RESERVATIONS.md` in the same directory as `Encoder` and `Decoder` for details
4. Create an instance of your class, and add it to `Encoder.converters` and `Decoder.converters`.
5. Call `(yourInstance).defineTypes(Types.types)` to register your types.

### Adding _documentation_ for your own types
Protocol documentation happens entirely in Jinja HTML. For an example, see `hexdoc_mediatransport/_templates/types/moreiotas.html.jinja`.

You'll also need to add a new hook class (if you put this in the same file as your hexdoc ones, note that `hookimpl` is _not the same as the one from hexdoc_):
```py
from hexdoc_mediatransport import (
    hookimpl as mt_hookimpl,
    MediaTransportPlugImpl,
    MediaTransportExtension,
    ExtensionSection
)

class YourModMTExt(MediaTransportExtension):
    def __init__(self):
        super().__init__()
        # TODO

class YourModMediaTransport(MediaTransportPlugImpl):
    @staticmethod
    @mt_hookimpl
    def mediatransport() -> MediaTransportExtension:
        return YourModMTExt()
```

To finish it off, declare it as an entrypoint in your `pyproject.toml`:
```toml
[project.entry-points.mediatransport]
modid="hexdoc_modname._hooks:YourModMediaTransportDoc"
```

By default, extensions render in _mediatransport's_ entry; however, you may want to display your entry standalone. Create a new entry that is only visible to hexdoc (i.e. `doc/resources/assets/hexcasting`...), and make a page with type `mediatransport:protocol_section` and `sections`:

> syntax may vary if you're using JSON5

```json
{
    "pages": [
        {
            "type": "mediatransport:protocol_section",
            "sections": [
                "your-section-name"
            ]
        }
    ]
}
```

TODO: add symbols to plugin spec; somehow prevent duplicate entries in the book; flags?
