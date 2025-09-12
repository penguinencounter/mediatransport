package penguinencounter.mediatransport;

import miyucomics.hexpose.HexposeMain;
import org.figuramc.figura.server.FiguraServer;
import ram.talia.moreiotas.common.lib.hex.MoreIotasActions;

// kotlin compiler is too good for tricks like this unfortunately
// so we shelve it for Java to handle
@SuppressWarnings("unused")
public class InteropGlue {
    public static boolean moreiotas() {
        try {
            Class<?> k = MoreIotasActions.class;
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static boolean hexpose() {
        try {
            Class<?> k = HexposeMain.class;
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static boolean isFiguraValid() {
        try {
            Class<?> k = FiguraServer.class;
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static final String figuraErrorMessage = "The version of Figura installed in this instance doesn't contain FSB (Figura Server Backend); to use mediatransport, a development version of Figura is required.";
}
