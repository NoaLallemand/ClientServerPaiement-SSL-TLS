import Logging.ConsoleLogger;
import Protocoles.VESPAP;
import ServeurGenerique.ThreadServeurPool;

import java.io.IOException;

public class MainServeurSecureSSL {
    public static void main(String[] args) {
        ConsoleLogger logger = new ConsoleLogger();
        VESPAP protocole = new VESPAP(logger);
        try {
            ThreadServeurPool threadServeurPool = new ThreadServeurPool(65000, protocole, 5, logger, true);
            threadServeurPool.start();
        }
        catch (IOException e) {
            logger.Trace("Erreur I/O lors du lancement du serveur!");
        }
    }
}
