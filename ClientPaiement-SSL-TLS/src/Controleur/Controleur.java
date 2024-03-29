package Controleur;

import Classes.Data.*;
import Modele.VESPAP;
import Vue.ConnectionTypeDialog;
import Vue.PayFactureDialogue;
import Vue.Principale;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controleur extends WindowAdapter implements ActionListener, MouseListener {
    private Principale vuePrincipale;

    public Controleur(Principale vuePrincipale)
    {
        this.vuePrincipale = vuePrincipale;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(vuePrincipale != null) {
            if(e.getSource() == vuePrincipale.getLoginButton()) {
                onPush_BtnLogin();
            }
            else if(e.getSource() == vuePrincipale.getMenuDeconnexion()) {
                onPush_BtnLogout(true);
            }
            else if(e.getSource() == vuePrincipale.getVoirFacturesButton()) {
                onPush_BtnVoirFactures();
            }
            else if(e.getSource() == vuePrincipale.getPayerFactureButton()) {
                onPush_BtnPayerFacture();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(VESPAP.getInstance().isClientConnected()) {
            onPush_BtnLogout(false);
        }
    }

    private void onPush_BtnLogin() {

        ConnectionTypeDialog dialog = new ConnectionTypeDialog();
        dialog.setVisible(true);
        int connectionMode = dialog.getSelectedConnectionMode();

        String username = vuePrincipale.getTxtFieldUsername().getText();
        String password = String.valueOf(vuePrincipale.getTxtFieldPassword().getPassword());
        if(!username.isEmpty() && !password.isEmpty()) {
            try {
                boolean identifiantsOk = VESPAP.getInstance().Login(username, password, connectionMode);
                if(identifiantsOk) {
                    vuePrincipale.ActiveVuePrincipale();
                    String bienvenue = "Connexion établie! Bienvenue " + username + "!";
                    JOptionPane.showMessageDialog(vuePrincipale, bienvenue, "Bienvenue", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch(Exception e) {
                JOptionPane.showMessageDialog(vuePrincipale, e.getMessage(), "Erreur de login", JOptionPane.ERROR_MESSAGE);
                vuePrincipale.getTxtFieldPassword().setText("");
            }
        }
        else {
            String message = "Erreur! Veuillez renseigner un login et un mot de passe!";
            JOptionPane.showMessageDialog(vuePrincipale, message, "Champs Manquants", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPush_BtnLogout(boolean afficheMsgDeconnexion) {
        VESPAP.getInstance().Logout();
        vuePrincipale.getTableModelFactures().clearTable(); //Permet de vider la JTable quand on se déconnecte.
        vuePrincipale.getTxtFieldNumClient().setText("");
        vuePrincipale.DesactiveVuePrincipale();
        if(afficheMsgDeconnexion) {
            JOptionPane.showMessageDialog(vuePrincipale, "Vous êtes bien déconnecté!", "Déconnexion", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onPush_BtnVoirFactures() {
        try {
            String numClientStr = vuePrincipale.getTxtFieldNumClient().getText();
            int numClient = Integer.parseInt(numClientStr);

            ArrayList<Facture> listeFacture = VESPAP.getInstance().GetFactures(numClient);
            vuePrincipale.getTableModelFactures().updateDataSource(listeFacture);
        }
        catch(NumberFormatException e) {
            String message = "Numéro de client invalide! Le numéro de client doit contenir uniquement des chiffres.";
            JOptionPane.showMessageDialog(vuePrincipale, message, "Erreur Saisie", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(vuePrincipale, e.getMessage(), "Erreur de login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPush_BtnPayerFacture() {
        try {
            int indice = vuePrincipale.getTableFactures().getSelectedRow();
            Facture facture = VESPAP.getInstance().getListeFacture().get(indice);

            PayFactureDialogue dialog = new PayFactureDialogue();
            dialog.setVisible(true);

            if(dialog.isOk())
            {
                String titulaire = dialog.getTitulaire();
                String visa = dialog.getVisa();
                boolean isPaiementOk = VESPAP.getInstance().PayFacture(facture,titulaire,visa);
                if(isPaiementOk) {
                    facture.setStatePaye(true);
                    vuePrincipale.getTableModelFactures().refreshRow(indice, indice);
                    JOptionPane.showMessageDialog(vuePrincipale, "Votre achat a bien été confirmé!", "Confirmation d'achat", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            dialog.dispose();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(vuePrincipale, e.getMessage(), "erreur de paiement", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onReceive_FactureDetaillee() {
        try {
            int indice = vuePrincipale.getTableFactures().getSelectedRow();
            Facture facture = VESPAP.getInstance().getListeFacture().get(indice);
            ArrayList<Article> listeArticles = VESPAP.getInstance().GetFactureDetaillee(facture);
            vuePrincipale.getTableModelFactureDetaillee().updateDataSource(listeArticles);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(vuePrincipale, e.getMessage(), "Erreur de login", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == vuePrincipale.getTableFactures()) {
            onReceive_FactureDetaillee();
        }
    }


    //Ces méthodes ne servent à rien ici...obligé de les redéfinir car on implémente l'interface MouseListener...
    //Impossible d'hériter de MouseAdapter (pour n'avoir qu'à redéfinir la méthode qui nous intéresse) car notre Controleur hérite déjà d'une classe (WindowAdapter).
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
