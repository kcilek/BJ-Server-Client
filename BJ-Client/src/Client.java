import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

public class Client {

    //Attribute
    private static String serverAdresse = "localhost";
    private static int serverPort = 1337;
    private static int nachrichtWartezeit = 500;
    private GUI gui;
    private Info info;

    //Konstruktor
    public Client(String serverAdresse, int serverPort){
        this.serverAdresse = this.serverAdresse;
        this.serverPort = serverPort;
    }

    //Client starten
    public void clientStart(){
        System.out.println("Client wird gestartet");
        System.out.println("Adresse: " + serverAdresse);
        System.out.println("Port: " + serverPort);
        info = new Info(serverAdresse, serverPort);
        gui = new GUI(this);
        getServerNachricht();
    }

    //Servernachrichten empfangen
    private void getServerNachricht(){
        SwingWorker swingWorker = new SwingWorker<String, String>(){
            @Override
            public String doInBackground() throws Exception{
                return info.getServerNachricht();
            }
            @Override
            public void done(){
                try{
                    aktualisieren(get());
                } catch (InterruptedException | ExecutionException e){
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }

    //gui je nach servernachricht aktualisieren
    private void aktualisieren(String serverNachricht) {
        String[] serverNachrichtenTeile = serverNachricht.split("--");
        switch (serverNachrichtenTeile[1]) {
            case "willkommen":
                gui.zeigeWillkommenPanel();
                getServerNachricht();
                break;
            case "getwette":
                gui.setWillkommenWartenLabel(false);
                gui.setWeiterspielenWartenLabel(false);
                gui.zeigeEinsatzPanel();
                gui.setEinsatzGeldLabel(serverNachrichtenTeile[2]);
                gui.setMinimumEinsatzLabel(serverNachrichtenTeile[3]);
                getServerNachricht();
                break;
            case "wetteantwort":
                switch (serverNachrichtenTeile[2]) {
                    case "ungueltig":
                        gui.einsatzFehler("Dein Einsatz muss eine ganze positive Zahl sein.");
                        getServerNachricht();
                        break;
                    case "zuhoch":
                        gui.einsatzFehler("Du kannst nicht mehr setzen als du hast.");
                        getServerNachricht();
                        break;
                    case "zuniedrig":
                        gui.einsatzFehler("Dein Einsatz ist zu gering für diesen Tisch.");
                        getServerNachricht();
                        break;
                    case "erfolg":
                        gui.einsatzErfolg();
                        gui.setEinsatzGeldLabel(serverNachrichtenTeile[3]);
                        getServerNachricht();
                        break;
                }
                break;
            case "neuerunde":
                gui.setEinsatzWartenLabel(false);
                gui.zeigeSpielzugPanel();
                gui.setSpielzugGeldLabel(serverNachrichtenTeile[2]);
                getServerNachricht();
                break;
            case "blackjack":
                switch (serverNachrichtenTeile[2]) {
                    case "spielerunddealer":
                        gui.setBlackjackLabel("Du und der Dealer haben einen Blackjack.");
                        getServerNachricht();
                        break;
                    case "spieler":
                        gui.setBlackjackLabel("Du hast einen Blackjack!");
                        getServerNachricht();
                        break;
                    case "dealer":
                        gui.setBlackjackLabel("Der Dealer hat einen Blackjack!");
                        getServerNachricht();
                        break;
                    case "dealerkeinblackjack":
                        gui.setBlackjackLabel("Der Dealer hat keinen Blackjack!");
                        getServerNachricht();
                        break;
                }
            case "neuedealerkarte":
                gui.dealerKarteHinzu(info.getKarteLabel(serverNachrichtenTeile[2]));
                getServerNachricht();
                break;
            case "getinsurancewette":
                gui.aktiviereInsuranceWette();
                getServerNachricht();
                break;
            case "insurancewetteantwort":
                switch (serverNachrichtenTeile[2]) {
                    case "fehler":
                        gui.insuranceWetteFehler();
                        getServerNachricht();
                        break;
                    case "gesetzt":
                        gui.insuranceWetteErfolg();
                        gui.setNachrichtLabel("Insurance Wette: €" + serverNachrichtenTeile[3]);
                        gui.setSpielzugGeldLabel(serverNachrichtenTeile[4]);
                        getServerNachricht();
                        break;
                    case "nichtgesetzt":
                        gui.insuranceWetteErfolg();
                        gui.entferneInsuranceInfo();
                        getServerNachricht();
                        break;
                }
                break;
            case "insurancewettenichtmoeglich":
                gui.setNachrichtLabel("Nicht genug Geld für eine Insurance Wette");
                getServerNachricht();
                break;
            case "insurancewettegewonnen":
                gui.setNachrichtLabel("Sie gewinnen €" + serverNachrichtenTeile[2] + " durch Ihre Insurance Wette.");
                gui.setSpielzugGeldLabel(serverNachrichtenTeile[3]);
                getServerNachricht();
                break;
            case "insurancewetteverloren":
                gui.setNachrichtLabel("Sie haben Ihre Insurance Wette verloren");
                getServerNachricht();
                break;
            case "insurancewetteabgeschlossen":
                gui.setInsuranceWetteWartenLabel(false);
                getServerNachricht();
                break;
            case "spielzug":
                gui.setSpielzugWartenLabel(false);
                gui.entferneInsuranceInfo();
                getServerNachricht();
                break;
            case "neuehand":
                info.handPanelHinzu(Integer.parseInt(serverNachrichtenTeile[2]), new HandPanel(this));
                gui.spielerHandPanelHinzu(info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])), Integer.parseInt(serverNachrichtenTeile[2]));
                getServerNachricht();
                break;
            case "handentfernen":
                gui.spielerHandPanelEntfernen(info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])));
                info.entferneHandPanel(Integer.parseInt(serverNachrichtenTeile[2]));
                getServerNachricht();
                break;
            case "handwette":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])).setHandEinsatzLabel(serverNachrichtenTeile[3]);
                getServerNachricht();
                break;
            case "handwert":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2]));//.setHandWert(serverNachrichtenTeile[3]));
                getServerNachricht();
                break;
            case "spielzugblackjack":
                switch (serverNachrichtenTeile[2]) {
                    case "spielerunddealer":
                    gui.setBlackjackLabel("Du und der Dealer haben einen Blackjack.");
                    getServerNachricht();
                    break;
                    case "spieler":
                        gui.setBlackjackLabel("Du hast einen Blackjack!");
                        getServerNachricht();
                        break;
                    case "dealer":
                        gui.setBlackjackLabel("Der Dealer hat einen Blackjack!");
                        getServerNachricht();
                        break;
                }
                break;
            case "neuespielerkarte":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])).karteHinzu(info.getKarteLabel(serverNachrichtenTeile[3]));
                getServerNachricht();
                break;
            case "spielzugoption":
                switch (serverNachrichtenTeile[2]) {
                    case "spielerunddealer":
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereSplitPairs();
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereDoubleDown();
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereHitStand();
                        getServerNachricht();
                        break;
                    case "splitpairs":
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereSplitPairs();
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereHitStand();
                        getServerNachricht();
                        break;
                    case "doubledown":
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereDoubleDown();
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereHitStand();
                        getServerNachricht();
                        break;
                    case "keine":
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).aktiviereHitStand();
                        getServerNachricht();
                        break;
                }
                break;
            case "spielzugoptionfehler":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])).spielzugFehler();
                getServerNachricht();
                break;
            case "bust":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])).bust();
                getServerNachricht();
                break;
            case "splitpairsantwort":
                switch (serverNachrichtenTeile[2]) {
                    case "erfolg":
                        gui.setSpielzugGeldLabel(serverNachrichtenTeile[3]);
                        getServerNachricht();
                        break;
                }
                break;
            case "doubledownantwort":
                switch (serverNachrichtenTeile[2]) {
                    case "erfolg":
                        info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[3])).doubleDownErfolg();
                        gui.setSpielzugGeldLabel(serverNachrichtenTeile[4]);
                        getServerNachricht();
                        break;
                }
                break;
            case "ergebnissenden":
                gui.setSpielzugWartenLabel(false);
                getServerNachricht();
                break;
            case "umgedrehtekartevondealerentfernen":
                gui.entferneVerdeckteDealerKarte();
                getServerNachricht();
                break;
            case "wertdealerhand":
                gui.setDealerHandWertLabel(serverNachrichtenTeile[2]);
                getServerNachricht();
                break;
            case "entferneumgedrehtedoubledownkarte":
                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[2])).entferneUmgedrehteDoubleDownKarte();
                getServerNachricht();
                break;
            case "ergebnisrunde":
                switch (serverNachrichtenTeile[2]) {
                    case "bust":
                        switch (serverNachrichtenTeile[3]) {
                            case "unentschieden":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Sie und der Dealer haben einen Bust! Unentschieden.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "dealer":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Sie haben einen Bust! Dealer gewinnt.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "spieler":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Der Dealer hat einen Bust! Sie gewinnen.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                        }
                        break;
                    case "normal":
                        switch (serverNachrichtenTeile[3]) {
                            case "unentschieden":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Unentschieden!");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "dealer":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Der Dealer gewinnt!");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "spieler":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Sie gewinnen!");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                        }
                    case "blackjack":
                        switch (serverNachrichtenTeile[3]) {
                            case "unentschieden":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Sie und der Dealer haben einen Blackjack! Unentschieden.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "dealer":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Der Dealer hat einen Blackjack! Dealer gewinnt.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                            case "spieler":
                                info.getHandPanel(Integer.parseInt(serverNachrichtenTeile[4])).setHandNachrichtLabel("Sie haben einen Blackjack! Sie gewinnen.");
                                gui.setSpielzugGeldLabel(serverNachrichtenTeile[5]);
                                getServerNachricht();
                                break;
                        }
                        break;
                }
                break;
            case "weiterspielen":
                gui.aktiviereWeiterspielen();
                getServerNachricht();
                break;
            case "weiterspielenantwort":
                switch (serverNachrichtenTeile[2]) {
                    case "fehler":
                        gui.weiterspielenFehler();
                        getServerNachricht();
                        break;
                    case "weiterspielen":
                        gui.zuruecksetzen();
                        info.zuruecksetzen();
                        gui.zeigeWeiterspielenPanel();
                        getServerNachricht();
                        break;


                }
                break;
            case "spielzuende":
                gui.zeigeWeiterspielenPanel();
                gui.setWeiterspielenGeldLabel(serverNachrichtenTeile[2]);
                gui.spielVorbei();
                getServerNachricht();
                break;
            case "warten":
                switch (serverNachrichtenTeile[2]) {
                    case "willkommen":
                        gui.setWillkommenWartenLabel(true);
                        gui.setWeiterspielenWartenLabel(true);
                        getServerNachricht();
                        break;
                    case "wette":
                        gui.setEinsatzWartenLabel(true);
                        getServerNachricht();
                        break;
                    case "insurancewette":
                        gui.setInsuranceWetteWartenLabel(true);
                        getServerNachricht();
                        break;
                    case "spielzug":
                        gui.setSpielzugWartenLabel(true);
                        getServerNachricht();
                        break;
                }
                break;
            default:
                System.err.println("Unbekannte Servernachricht erhalten :-( ->" + serverNachricht);
                break;
        }
    }

    //NAChricht zu Server schicken
    public void sendeClientNachricht(String clientNachricht){
        info.sendeClientNachricht(clientNachricht);
    }

    //Spiel beenden
    public void spielBeenden(){
        info.spielBeenden();
    }

    //Main Methodde
    public static void main(String[] args){
        for (int i = 0; i < args.length; i += 2) {
            String option = args[i];
            String argument = null;
            try {
                argument = args[i + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Optionen: [-a serverAddresse] [-p serverPort]");
                System.exit(1);
            }
            switch (option) {
                case "-a":
                    String serverAddresse = argument;
                    break;
                case "-p":
                    try {
                        serverPort = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Serverport muss eine positive ganze Zahl sein");
                        System.exit(1);
                    }
                    break;
                default:
                    System.err.println("Optionen: [-a serverAddress] [-p serverPort]");
                    System.exit(1);
                    break;
            }
        }
        Client client = new Client(serverAdresse, serverPort);
        client.clientStart();
    }

}

