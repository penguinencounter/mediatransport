from hexdoc.minecraft import LocalizedStr
from hexdoc.patchouli.page import Page, PageWithText


class PageWithTextAlternate(PageWithText, type="hexcasting:mediatransport/text"):
    # Literally the same as the superclass
    pass


class HtmlPage(Page, type="hexcasting:mediatransport/hexdoc/html"):
    content: LocalizedStr


class BeginAltnMarker(Page, type="hexcasting:mediatransport/hexdoc/begin_altn"):
    pass


class EndAltnMarker(Page, type="hexcasting:mediatransport/hexdoc/end_altn"):
    pass
