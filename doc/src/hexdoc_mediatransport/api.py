from typing import NamedTuple, Protocol

import pluggy

PLUGGY_NS = "mediatransport"


hookspec = pluggy.HookspecMarker(PLUGGY_NS)


class ExtensionSection(NamedTuple):
    id: str
    """Unique identifying name for this section."""
    template: str
    """
    Location of the template to render, for example `mediatransport:filename.html.jinja`.
    """
    ordering: int
    """
    Numbering specifying render order. Not necessarily related to iota type IDs.
    
    Here's what the built in types use:
    - `0`   - Hex Casting
    - `10`  - MoreIotas
    - `20`  - Hexpose
    - `100` - Specials
    """


class MediatransportDocSpec(Protocol):
    @hookspec
    def mediatransport_doc_extend() -> list[list[ExtensionSection]]:
        """
        Return all your sections here.
        """
        ...


class MediatransportDocImpl(Protocol):
    """Implementation of a mediatransport extension.

    Technically optional, but helps with types and autocomplete and all that.
    """

    @staticmethod
    def mediatransport_doc_extend() -> list[ExtensionSection]:
        """
        Return all your sections here.
        """
        ...
