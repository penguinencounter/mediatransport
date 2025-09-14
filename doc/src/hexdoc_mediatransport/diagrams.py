# Block diagrams and other protocol helpers.

import re
from dataclasses import dataclass
from typing import Any, Literal, NamedTuple

from hexdoc.minecraft import I18n
from jinja2 import pass_context
from jinja2.runtime import Context
from markupsafe import Markup

from .lang import ArglessI18n, I18nTuple, plural_factory

PROTOCOL = "mediatransport.book.protocol"
SYMBOLS = f"{PROTOCOL}.symbols"
TOOLTIPS = f"{PROTOCOL}.tooltips"
plural = plural_factory("mediatransport.book.pluralizations")


@dataclass
class ProtoSymbol:
    name: str
    size: str | int | None = None

    def render_name(self, ctx: Context) -> ArglessI18n:
        i18n = I18n.of(ctx)
        return I18nTuple.of(i18n.localize(f"{SYMBOLS}.{self.name}"))

    def render_tooltip(self, ctx: Context) -> I18nTuple[Any]:
        i18n = I18n.of(ctx)
        stack: list[I18nTuple[Any]] = [I18nTuple.untranslated(self.name)]

        if self.size is not None:
            contents: I18nTuple[Any]
            if isinstance(self.size, str):
                contents = I18nTuple.ofa(
                    i18n.localize(f"{TOOLTIPS}.size_ref"), (self.size,)
                )
            else:  # int
                contents = plural(ctx, "byte", self.size)

            stack.append(I18nTuple.ofa(i18n.localize(f"{TOOLTIPS}.size"), (contents,)))

        return I18nTuple.join("\n", stack)


symbols = {
    "type": ProtoSymbol(name="type", size=1),
    "data": ProtoSymbol(name="data", size=None),
    "double_value": ProtoSymbol(name="value", size=8),
    "dir": ProtoSymbol(name="dir", size=1),
    "pattern_len": ProtoSymbol(name="length", size=4),
    "angles": ProtoSymbol(name="angles", size="len")
}


@pass_context
def sym_name(ctx: Context, word: str) -> str:
    symbol = symbols[word]
    return symbol.render_name(ctx).resolve()


@pass_context
def symdef(ctx: Context, word: str) -> str:
    symbol = symbols[word]
    return (
        f'<span class="protocol-sym-def" id="mediatransport-protocol-{word}"'
        f' title="{symbol.render_tooltip(ctx).resolve_html_oneline()}">'
        f"{symbol.render_name(ctx).resolve()}"
        "</span>"
    )

@pass_context
def symr(ctx: Context, word: str) -> str:
    symbol = symbols[word]
    return (
        f'<span class="protocol-sym-raw"'
        f' title="{symbol.render_tooltip(ctx).resolve_html_oneline()}">'
        f"{symbol.render_name(ctx).resolve()}"
        "</span>"
    )

@pass_context
def sym(ctx: Context, word: str) -> str:
    symbol = symbols[word]
    return (
        f'<a class="protocol-sym" href="#mediatransport-protocol-{word}"'
        f' title="{symbol.render_tooltip(ctx).resolve_html_oneline()}">'
        f"{symbol.render_name(ctx).resolve()}"
        "</a>"
    )



tags = {"symdef": symdef, "sym": sym, "symr": symr}

matching_pattern = re.compile(r"{(sym(?:|def|r)):(\w+)}")


def _make_matcher(context: Context):
    def _handle_match(match: re.Match[str]) -> str:
        tag, value = match.groups()
        return tags[tag](context, value)

    return _handle_match


def process_markup(context: Context, raw: str) -> Markup:
    return Markup(matching_pattern.sub(_make_matcher(context), raw))


@pass_context
def tl(context: Context, key: str) -> Markup:
    i18n = I18n.of(context)
    translated = i18n.localize(f"{PROTOCOL}.{key}").value
    return process_markup(context, translated)


class Block(NamedTuple):
    size: int | tuple[str, str] | None
    kind: Literal["literal", "sym"]
    sym: str


@pass_context
def dia(context: Context, blocks: list[Block]) -> Markup:
    block_template = context.environment.get_template("block_diagram.html.jinja")
    new_ctx = context.get_all().copy()
    new_ctx["blocks"] = blocks
    return Markup(block_template.render(new_ctx))
