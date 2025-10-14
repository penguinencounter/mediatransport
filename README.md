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

## Adding documentation
Protocol documentation happens entirely in Jinja HTML. For an example, see `hexdoc_mediatransport/_templates/types/moreiotas.html.jinja`.

You'll also need to add a new hook class (if you put this in the same file as your hexdoc ones, note that `hookimpl` is _not the same as the one from hexdoc_):
```py
from hexdoc_mediatransport import (
    hookimpl as mt_hookimpl,
    MediaTransportPlugImpl,
    MediaTransportExtension
)

class YourModMTExt(MediaTransportExtension):
    def __init__(self):
        super().__init__("your_mod_id")
        # TODO

class YourModMediaTransport(MediaTransportPlugImpl):
    @staticmethod
    @mt_hookimpl
    def mediatransport() -> MediaTransportExtension:
        return YourModMTExt()
```

To register the extension, declare the `PlugImpl` as an entrypoint in your `pyproject.toml`:
```toml
[project.entry-points.mediatransport]
modid="hexdoc_modname._hooks:YourModMediaTransport"
```

### Sections

A _section_ is a template that renders within the context of the protocol documentation.

To add new protocol documentation, you'll want to create a section:
```py
from hexdoc_mediatransport import (
    MediaTransportExtension,
    ExtensionSection
)

class YourModMTExt(MediaTransportExtension):
    def __init__(self):
        super().__init__("your_mod_id")
        self.register_section(ExtensionSection(
            id="section_id",
            template="your_mod:path_to/template.html.jinja",
            ordering=25,
        ))
```

### Writing Documentation 1
`mediatransport.diagrams.context` is the entrypoint to all the formatting magic:
```jinja2
{# in mediatransport itself, the root is `"mediatransport.book.protocol"` #}
{% set d = mediatransport.diagrams.context("path.to.root.of.section") %}
```

You can then use `d` to get translations:
```jinja2
{# Corresponds to translation `path.to.root.of.section.key`. #}
{{ d.tl("key") }}
```

Or build diagrams:
```jinja2
{# width 1 literal value 0x05, then
   width 8 symbol key 'double_value' (see next section) #}
{{ d.dia(
  (1, 'literal', '05'),
  (8, 'sym', 'double_value')
]) }}
```

### Symbols in Python
A symbol is a translatable unit and a link combined into one. Symbols have
an _id_ and a _translation_; _many symbols can have the same translation_.

For example, `type` (and basically any word in blue) is a symbol.

You can define your own symbols if you specify where the translation
keys are located:
```py
from hexdoc_mediatransport import (
    MediaTransportExtension,
    ExtensionSection
)

class YourModMTExt(MediaTransportExtension):
    def __init__(self):
        super().__init__("your_mod_id")
        # ... other stuff ...
        self.symbol_root_key = "your_mod.path_to.symbols"
        self.create_symbol("id", "translate_as")
        # your_mod.path_to.symbols.translate_as
        
        # define in bulk
        self.create_symbols({
            "id1": "translate1",
            "id2": "translate2",
            "id3": "translate1",
            "id4": "translate4",
        })
```

### Translated Text
Inside text included by `{{ d.tl(...) }}`, you can
- use HTML
- reference symbols:
  - `{symdef:id}` to _define_ a symbol; this creates an anchor on the text
  - `{sym:id}` to _reference_ a symbol; this creates a link to the aforementioned anchor
  - `{symr:id}` renders the text of a symbol with the color but no link
  - `{anchor:id}` defines the anchor without any text (maybe put this in a section header?)
  - each of these that produces text can be provided text to display _instead of_ the translation. This is raw text, not a translation key: `{sym:id:alias}`

## Rendering documentation

By default, extensions render in _mediatransport's_ entry if your extension is loaded; however, you may want to display your entry standalone. Create a new entry that is only visible to hexdoc (i.e. `doc/resources/assets/hexcasting`...), and make a page with type `mediatransport:protocol_section` and `sections`:

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
