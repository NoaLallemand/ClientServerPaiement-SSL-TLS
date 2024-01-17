package Modele;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;
import Intefaces.*;

import javax.net.ssl.*;

public class Communication {
    private Socket socketClient;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private String configPath = "\\config\\config.txt";
    public Communication() { }

    public ObjectOutputStream getWriter() {
        return writer;
    }

    public void init(int connectionMode) throws IOException {

        if(connectionMode == ConnectionMode.NO_SECURE) {
            try {
                SocketInfos infos = getDefaultSocketInfos(false);
                System.out.println("SocketsInfos: IP=" + infos.getIp() + "/PORT=" + infos.getPort());
                socketClient = new Socket(infos.getIp(), infos.getPort());

                writer = new ObjectOutputStream(socketClient.getOutputStream());
                reader = new ObjectInputStream(socketClient.getInputStream());
            }
            catch(IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            FileInputStream fis = new FileInputStream("../KeyStoresSSL/client_keystore.jks");
            String keyStorePass = "client123";
            KeyStore ks = null;

            try {
                ks = KeyStore.getInstance("JKS");
                ks.load(fis, keyStorePass.toCharArray());

                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                String keyPass = "client123";
                kmf.init(ks, keyPass.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);

                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                SocketInfos infos = getDefaultSocketInfos(true);
                System.out.println("SocketsInfos: IP=" + infos.getIp() + "/PORT=" + infos.getPort());
                socketClient = sslSocketFactory.createSocket(infos.getIp(), infos.getPort());

                writer = new ObjectOutputStream(socketClient.getOutputStream());
                reader = new ObjectInputStream(socketClient.getInputStream());

            }
            catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException |
                   KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private SocketInfos getDefaultSocketInfos(boolean securedInfos) throws IOException {
        String rootDirectory = System.getProperty("user.dir");
        String completePath = rootDirectory + configPath;

        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(completePath);

            prop.load(fis);
            int defaultPort;
            if(securedInfos) {
                defaultPort = Integer.parseInt(prop.getProperty("portSecure"));
            }
            else {
                defaultPort = Integer.parseInt(prop.getProperty("defaultPort"));
            }

            String defaultIp = prop.getProperty("ipServeur");
            SocketInfos infos = new SocketInfos(defaultPort, defaultIp);
            fis.close();
            return infos;
        }
        catch(IOException | NumberFormatException e) {
            throw e;
        }
    }

    public Reponse traiteRequete(Requete requete) throws IOException, ClassNotFoundException {
        writer.writeObject(requete);
        return (Reponse) reader.readObject();
    }
}
