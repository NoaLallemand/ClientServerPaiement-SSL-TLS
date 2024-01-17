package ServeurGenerique;

import Logging.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;

public abstract class ThreadServeur extends Thread {
    protected int port;
    protected Protocole protocole;
    protected Logger logger;
    protected ServerSocket socketServeur;

    public ThreadServeur(int port, Protocole protocole, Logger logger, boolean secure) throws IOException {
        super("TH Serveur (port=" + port + ", protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;
        this.logger = logger;
        if(secure) {
            FileInputStream fis = new FileInputStream("../KeyStoresSSL/server_keystore.jks");
            String keyStorePass = "server123";
            KeyStore ks = null;

            try {
                ks = KeyStore.getInstance("JKS");
                ks.load(fis, keyStorePass.toCharArray());

                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                String keyPass = "server123";
                kmf.init(ks, keyPass.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);

                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

                socketServeur = sslServerSocketFactory.createServerSocket(port);
            }
            catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        else {
            socketServeur = new ServerSocket(port);
        }
    }
}
