__all__ = ["ExtensionSection", "hookimpl", "MediaTransportPlugImpl"]

import pluggy

from .api import PLUGGY_NS, ExtensionSection, MediaTransportPlugImpl

hookimpl = pluggy.HookimplMarker(PLUGGY_NS)
"""
Pluggy marker for attaching to mediatransport hooks.
"""
