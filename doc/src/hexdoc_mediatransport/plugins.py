import itertools

import pluggy

from .api import PLUGGY_NS, ExtensionSection, MediatransportDocSpec
from .builtin_extensions import MediatransportBuiltins


class MediaTransportPlugins:
    def __init__(self) -> None:
        self.plugs = pluggy.PluginManager(PLUGGY_NS)
        self.plugs.add_hookspecs(MediatransportDocSpec)
        self.entrypoints()

    def entrypoints(self):
        self.plugs.load_setuptools_entrypoints(PLUGGY_NS)
        self.plugs.check_pending()
        self.plugs.register(MediatransportBuiltins)

    def get_extensions(self) -> list[ExtensionSection]:
        hook = self.plugs.hook
        extensions: list[list[ExtensionSection]] = hook.mediatransport_doc_extend()
        return list(sorted(itertools.chain(*extensions), key=lambda k: k.ordering))
