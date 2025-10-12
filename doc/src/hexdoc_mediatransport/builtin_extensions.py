from hexdoc_mediatransport import hookimpl

from .api import ExtensionSection, MediatransportDocImpl

sections = [
    ExtensionSection(
        id="hexcasting",
        template="mediatransport:types/hexcasting.html.jinja",
        ordering=0,
    ),
    ExtensionSection(
        id="moreiotas",
        template="mediatransport:types/moreiotas.html.jinja",
        ordering=10,
    ),
    ExtensionSection(
        id="hexpose",
        template="mediatransport:types/hexpose.html.jinja",
        ordering=20,
    ),
    ExtensionSection(
        id="builtin_specials",
        template="mediatransport:types/builtin_specials.html.jinja",
        ordering=100,
    ),
]


class MediatransportBuiltins(MediatransportDocImpl):
    @staticmethod
    @hookimpl
    def mediatransport_doc_extend() -> list[ExtensionSection]:
        return sections
