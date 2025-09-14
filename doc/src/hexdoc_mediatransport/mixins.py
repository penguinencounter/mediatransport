from typing import Callable, Self, cast

from hexdoc.patchouli import Entry
from hexdoc.patchouli.page.abstract_pages import Page
from hexdoc.plugin import PluginManager
from pydantic import BaseModel, model_validator
from pydantic.fields import FieldInfo
from rich import print as rp


def inject_recursive(
    base: type, mutator: Callable[[type], None], exclude: set[type] | None = None
) -> set[type]:
    """
    Apply the mutator to the base class and all subclasses, except those in the exclusion set.
    """
    if exclude is not None and base in exclude:
        rp(
            f"[yellow][bold]warning[/]: not doing anything because root class {base} is in exclusion list[/]"
        )
        return exclude

    frontier: list[type] = [base]
    seen: set[type] = exclude or set()
    while frontier:
        it = frontier.pop()
        mutator(it)
        seen.add(it)
        frontier += list(set(it.__subclasses__()) - seen - set(frontier))
    return seen


def add_entry_after(entry_type: type[Entry]):
    name = "mediatransport_filter_hidden"

    class TransplantModel(BaseModel):
        @model_validator(mode="after")
        def mediatransport_filter_hidden(self) -> Self:
            self_ = cast(Entry, self)
            filtered_pages: list[Page] = list(
                filter(lambda x: not x.hexdoc_hide, self_.pages)  # type: ignore (added by mixin)
            )
            if len(filtered_pages) != len(self_.pages):
                rp(
                    Rf"[bold]\[INFO][/] [bright_black]mediatransport[/] [blue_violet][bold]Filtered[/] entry {self_.id}: [bold]{len(filtered_pages) - len(self_.pages):+} page(s)[/][/]"
                )
                self_.pages = filtered_pages
            return self

    entry_type.__pydantic_decorators__.model_validators[name] = (
        TransplantModel.__pydantic_decorators__.model_validators[name]
    )
    entry_type.model_rebuild(force=True)


def add_hide(it: type[BaseModel]):
    rp(rf"[bright_black] patch {it}[/]")
    it.model_fields["hexdoc_hide"] = FieldInfo(annotation=bool, default=False)
    it.model_rebuild(force=True)


def stage_2():
    rp(
        R"[bold]\[INFO][/] [bright_black]mediatransport[/] [yellow]Patching...[/]",
        end="",
        flush=True,
    )
    added_hide = inject_recursive(Page, add_hide)
    entry_types = inject_recursive(Entry, add_entry_after)
    rp(
        "[bold green] ok![/] [bold cyan]Changes summary:[/]\n"
        f" * [blue_violet]{len(added_hide): 4d} +hexdoc_hide[/]\n"
        f" * [bright_blue]{len(entry_types): 4d} Entry types[/]\n"
    )


def stage_1():
    _init_plugins = PluginManager.init_plugins

    def init_plugins_wrapper(self: PluginManager):
        result = _init_plugins(self)
        stage_2()
        return result

    PluginManager.init_plugins = init_plugins_wrapper
