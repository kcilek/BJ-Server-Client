import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Spieler implements Runnable {

    //Attribute
    private static int maxPunkte = 21; //Maximaler Wert aller Karten auf der Hand bevor es als "Bust" gewertet wird
    private Tisch tisch; //tisch bzw. spiel dem beigetreten wird
    private BufferedReader in; //input von client
    private PrintWriter out; //output zu client
    private ArrayList<Hand> spielerHaende = new ArrayList<>(); //haende des spielers
    private Hand hand; //hand des spielers
    private double geld; //geld/guthaben des spielers
    private boolean hatBlackjack = false; //prüfen ob spieler blackjack hat
    private String auswahl; //auswahl die der spieler getätigt hat
    private boolean auswahlErhalten = false; //prüfen ob spieler auswahl getätigt hat
    private double insuranceWette; //Betrag der "Insurance" - Wette
    private boolean insuranceWetteGesetzt; //prüfen ob insurance wette gesetzt wurde
    private CountDownLatch startLatch; //latch um zu warten bis alle spieler beigetreten sind
    private CountDownLatch wetteLatch; // latch um zu warten bis alle spieler ihre wetten plaziert haben
    private CountDownLatch insuranceWetteLatch; // latch um zu warten bis alle spieler ihre versicherungen gespielt haben
    private CountDownLatch austeilLatch; //latch um zu warten bis karten an alle spieler vergeben wurden
    private CountDownLatch dealerDrannLatch; //latch um zu warten bis der dealer fertig ist
    private boolean weiterSpielen = false; //prüfen ob der spieler weiterspielen möchte

    //Konstruktor
    Spieler(Socket socket, Tisch tisch, int geld){
        this.tisch = tisch;
        this.geld = geld;
        try{
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Spieler Thread Run
    @Override
    public void run(){
        out.println("servernachricht--willkommen");
        do{
            spielenBlackjack();
        }while (weiterSpielen);
            out.println("servernachricht--spielzuende--" + String.format("%.2f", geld));
        }


    //Spieler auf Runde vorbereiten
    private void spielerVorbereiten(){
        spielerHaende.clear();
        hand = new Hand();
        spielerHaende.add(hand);
        hatBlackjack = false;
        auswahlErhalten = false;
        insuranceWetteGesetzt = false;
        weiterSpielen = false;
        startLatch = new CountDownLatch(1);
        wetteLatch = new CountDownLatch(1);
        insuranceWetteLatch = new CountDownLatch(1);
        austeilLatch = new CountDownLatch(1);
        dealerDrannLatch = new CountDownLatch(1);
        out.println("servernachricht--warten--willkommen");
    }

    //Wette des Spielers einlesen
    private void getWette(){
        auswahlErhalten = false;
        do{
            boolean wetteKeineZahl = false; //prüfen ob die wette ein positiver int ist
            out.println("servernachricht--getwette--" + String.format("%.2f", geld) + "--" + String.format("%.2f", tisch.getMinWette()));
            getAuswahl();
            try{
                int wette = Integer.parseInt(auswahl);
                hand.setzeWette(wette);
            } catch (NumberFormatException e){
                wetteKeineZahl = true;
            }
            if(wetteKeineZahl){
                out.println("servernachricht--wetteantwort--ungueltig");
                auswahlErhalten = false;
            } else if(hand.getWette() > geld){
                out.println("servernachricht--wetteantwort--zuhoch");
                auswahlErhalten = false;
            } else if(hand.getWette() < tisch.getMinWette()){
                out.println("servernachricht--wetteantwort--zuniedrig");
                auswahlErhalten = false;
            }
        } while (!auswahlErhalten);
        geld -= hand.getWette();
        tisch.wetteGesetztLatchCD();
        out.println("servernachricht--wetteantwort--erfolg--" + String.format("%.2f", geld));
        if (tisch.getAnzSpieler() > 1){
            out.println("servernachricht--warten--wette");
        }
    }

    //prüfen ob spieler eine insurance wette spielen will
    private void getInsuranceWette(){
        if(geld >= hand.getWette() / 2){
            auswahlErhalten = false;
            do{
                out.println("servernachricht--getinsurancewette");
                getAuswahl();
                if(!auswahl.equals("Ja") && !auswahl.equals("Nein")){
                    out.println("servernachricht--insurancewetteantwort--fehler");
                    auswahlErhalten = false;
                }
            } while (!auswahlErhalten);
            if(auswahl.equals("Ja")){
                insuranceWette = hand.getWette() / 2;
                geld -= insuranceWette;
                insuranceWetteGesetzt = true;
                out.println("servernachricht--insurancewetteantwort--gesetzt--" + String.format("%.2f", insuranceWette) + "--" + String.format("%.2f", geld));
            } else {
                out.println("servernachricht--insurancewetteantwort--nichtgesetzt");
            }
        } else{
            out.println("servernachricht--insurancewettenichtmoeglich");
        }
        if(tisch.getAnzSpieler() > 1){
            out.println("servernachricht--warten--insurancewette");
        }
    }

    //spielzug auswahl des spielers lesen
    private void getAuswahl(){
        try{
            while (!auswahlErhalten){
                String clientNachricht;
                if((clientNachricht = in.readLine()) != null){
                    auswahl = clientNachricht;
                    auswahlErhalten = true;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //optionen für spielzüge
    private void beideOptionen(Hand hand){
        auswahlErhalten = false;
        do {
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
            out.println("servernachricht--spielzugoption--beide--" + spielerHaende.indexOf(hand));
            getAuswahl();
            if (!auswahl.equals("Hit") && !auswahl.equals("Stand") && !auswahl.equals("Split Pairs") && !auswahl.equals("Double Down")) {
                out.println("servernachricht--spielzugoptionfehler--" + spielerHaende.indexOf(hand));
                auswahlErhalten = false;
            }
        } while (!auswahlErhalten) ;
    }
    private void splitPaareOption(Hand hand){
        auswahlErhalten = false;
        do{
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
            out.println("servernachricht--spielzugoption--splitpairs--" + spielerHaende.indexOf(hand));
            getAuswahl();
            if (!auswahl.equals("Hit") && !auswahl.equals("Stand") && !auswahl.equals("Split Pairs")){
                out.println("servernachricht--spielzugoptionfehler--" + spielerHaende.indexOf(hand));
                auswahlErhalten = false;
            }
        } while (!auswahlErhalten);
    }
    private void doubleDownOption(Hand hand){
        auswahlErhalten = false;
        do {
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
            out.println("servernachricht--spielzugoption--doubledown--" + spielerHaende.indexOf(hand));
            getAuswahl();
            if (!auswahl.equals("Hit") && !auswahl.equals("Stand") && !auswahl.equals("Double Down")){
                out.println("servernachricht--spielzugoptionfehler--" + spielerHaende.indexOf(hand));
                auswahlErhalten = false;
            }
        } while (!auswahlErhalten);
    }
    private void keineOption(Hand hand){
        auswahlErhalten = false;
        do {
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
            out.println("servernachricht--spielzugoption--keine" + spielerHaende.indexOf(hand));
            getAuswahl();
            if (!auswahl.equals("Hit") && !auswahl.equals("Stand")) {
                out.println("servernachricht--spielzugoptionfehler--" + spielerHaende.indexOf(hand));
                auswahlErhalten = false;
            }
        } while(!auswahlErhalten);
    }

    //Hand splitten
    private void splitPaare(Hand hand){
        hand.setPaareSplitten();
        geld -= hand.getWette();
        out.println("servernachricht--splitpairsantwort--erfolg--" + String.format("%.2f", geld));
        Hand ersteHand = new Hand();
        Hand zweiteHand = new Hand();
        out.println("servernachricht--handentfernen--" + spielerHaende.indexOf(hand));
        spielerHaende.add(spielerHaende.indexOf(hand), zweiteHand);
        out.println("servernachricht--neuehand--" + spielerHaende.indexOf(zweiteHand));
        spielerHaende.add(spielerHaende.indexOf(zweiteHand), ersteHand);
        out.println("servernachricht--neuehand--" + spielerHaende.indexOf(ersteHand));
        spielerHaende.remove(hand);
        ersteHand.karteHinzu(hand.getKarte(0));
        out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(ersteHand) + "--" + ersteHand.getKarte(0));
        zweiteHand.karteHinzu(hand.getKarte(1));
        out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(zweiteHand) + "--" + zweiteHand.getKarte(0));
        ersteHand.setzeWette(hand.getWette());
        out.println("servernachricht--handwette--" + spielerHaende.indexOf(ersteHand) + "--" + String.format("%.2f", ersteHand.getWette()));
        zweiteHand.setzeWette(hand.getWette());
        out.println("servernachricht--handwette--" + spielerHaende.indexOf(zweiteHand) + "--" + String.format("%.2f", zweiteHand.getWette()));

        if(ersteHand.getKarte(0).getBild() == Karte.Bild.Ass && zweiteHand.getKarte(0).getBild() == Karte.Bild.Ass){
            Karte neueKarte = tisch.karteAusteilen();
            ersteHand.karteHinzu(neueKarte);
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(ersteHand) + "--" + neueKarte);
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(ersteHand) +"--"+ ersteHand.getWert());

            neueKarte = tisch.karteAusteilen();
            zweiteHand.karteHinzu(neueKarte);
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(zweiteHand) + "--" + neueKarte);
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(zweiteHand) +"--"+ ersteHand.getWert());
            if(tisch.getAnzSpieler() > 1 && zweiteHand == spielerHaende.get(spielerHaende.size() -1)){
              out.println("servernachricht--warten--spielzug");
            }
        } else {
            Karte neueKarte = tisch.karteAusteilen();
            ersteHand.karteHinzu(neueKarte);
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(ersteHand) + "--" + neueKarte);
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(ersteHand) +"--"+ ersteHand.getWert());

            neueKarte = tisch.karteAusteilen();
            zweiteHand.karteHinzu(neueKarte);
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(zweiteHand) + "--" + neueKarte);
            out.println("servernachricht--handwert--" + spielerHaende.indexOf(zweiteHand) +"--"+ ersteHand.getWert());

            macheZug(ersteHand);
            macheZug(zweiteHand);
        }
    }

    //Hand Double Down
    private void doubleDown(Hand hand){
        hand.setDoubleDown();
        geld -= hand.getWette();
        hand.setzeWette(hand.getWette() * 2);
        Karte neueKarte = tisch.karteAusteilen();
        hand.doubleDownKarteHinzu(neueKarte);
        out.println("servernachricht--handwette--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", hand.getWette()));
        out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(hand) + "--verdeckt");
        out.println("servernachricht--doubledownantwort--erfolg--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld));
    }

    //Hand Hit oder Stand
    private void hitStand(Hand hand){
        if(auswahl.equals("Hit")){
            Karte neueKarte = tisch.karteAusteilen();
            hand.karteHinzu(neueKarte);
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(hand) + "--" + neueKarte);
            while(auswahl.equals("Hit") && hand.getWert() <= maxPunkte){
                keineOption(hand);
                if (auswahl.equals("Hit")){
                    neueKarte = tisch.karteAusteilen();
                    hand.karteHinzu(neueKarte);
                    out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(hand) + "--" + neueKarte);
                }
            }
        }
        out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
        if (hand.getWert() > maxPunkte){
            out.println("servernachricht--bust--" + spielerHaende.indexOf(hand));
        }
    }

    //Dealer Karten an CLient senden
    private void dealerKartenSenden(){
        out.println("servernachricht--ergebnissenden");
        out.println("servernachricht--umgedrehtekartevondealerentfernen");
        for (int i = 1; i < tisch.getDealerHand().getGroesse(); i++){
            out.println("servernachricht--neuedealerkarte--" + tisch.getDealerHand().getKarte(i));
        }
        out.println("servernachricht--wertdealerhand--" + tisch.getDealerHand().getWert());
    }

    //Ausführen des Spielzuges, abfragen nach hit, stand, split, doubledown
    void macheZug(Hand hand){
       if(this.hand == hand){
           out.println("servernachricht--spielzug");
           if(hatBlackjack && tisch.getDealerHatBlackjack()){
               out.println("servernachricht--spielzugblackjack--spielerunddealer");
           } else if(hatBlackjack && !tisch.getDealerHatBlackjack()){
               out.println("servernachricht--spielzugblackjack--spieler");
           } else if(!hatBlackjack && tisch.getDealerHatBlackjack()){
               out.println("servernachricht--spielzugblackjack--dealer");
           }
       }
        //Max Punkte bis Zu veroppelt werden darf
        int maxDDpunkte = 11;
        //Mindestanzahl von Punkten bevor verdoppelt werden darf
        int minDDpunkte = 9;
        //Min Punkte für verdoppeln bei Soft Hand
        int minDDSoftPunkte = 19;
        // Max Punkte für verdoppeln bei Soft Hand
        int maxDDSoftPunkte = 21;
        if (!hatBlackjack && !tisch.getDealerHatBlackjack()
                && hand.getKarte(0).getBild() == hand.getKarte(1).getBild() && ((hand.getWert() >= minDDpunkte && hand.getWert() <= maxDDpunkte)
                || (hand.istSoft() && hand.getWert() >= minDDSoftPunkte && hand.getWert() <= maxDDSoftPunkte)) && geld >= hand.getWette()) {
            beideOptionen(hand);
        } else if (!hatBlackjack && !tisch.getDealerHatBlackjack() && !hand.wurdeDoubleDown()
                && hand.getKarte(0).getBild() == hand.getKarte(1).getBild() && geld >= hand.getWette()) {
            splitPaareOption(hand);
        } else if (!hatBlackjack && !tisch.getDealerHatBlackjack() && !hand.wurdeGesplittet() && ((hand.getWert() >= minDDpunkte && hand.getWert() <= maxDDpunkte)
                || (hand.istSoft() && hand.getWert() >= minDDSoftPunkte && hand.getWert() <= maxDDSoftPunkte)) && geld >= hand.getWette()) {
            doubleDownOption(hand);
        } else if (!hatBlackjack && !tisch.getDealerHatBlackjack() && !hand.wurdeGesplittet() && !hand.wurdeDoubleDown()) {
            keineOption(hand);
        } switch (auswahl){
            case "Split Pairs":
                splitPaare(hand);
                break;
            case "Double Down":
                doubleDown(hand);
                break;
            case "Hit":
            case "Stand":
                hitStand(hand);
                break;
        }
        if (tisch.getAnzSpieler() > 1 && !hatBlackjack && !tisch.getDealerHatBlackjack() && hand == spielerHaende.get(spielerHaende.size() - 1)) {
            out.println("servernachricht--warten--spielzug");
        }
    }

    //Zeigt Spieler seine ersten zwei karten, die offene des dealers und ob einer von beiden blackjack hat
    private void rundenInfoSenden(){

        out.println("servernachricht--neuerunde--" + String.format("%.2f", geld));
        out.println("servernachricht--neuehand--0");

        for(int i = 0; i < hand.getGroesse(); i++){
            out.println("servernachricht--neuespielerkarte--0--" + hand.getKarte(i));
        }
        out.println("servernachricht--handwert--0--" + hand.getWert());
        out.println("servernachricht--handwette--0--" + String.format("%.2f", hand.getWette()));

        if(hand.getWert() == maxPunkte){
            out.println("servernachricht--blackjack--spieler");
            hatBlackjack = true;
        }

        out.println("servernachricht--neuedealerkarte--" + tisch.getDealerOffeneKarte());
        out.println("servernachricht--neuedealerkarte--verdeckt");
        if(tisch.getDealerOffeneKarte().getBild() == Karte.Bild.Ass){
            getInsuranceWette();
        }
        tisch.insuranceWetteGesetztLatchCD();
        try{
            insuranceWetteLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        if(hand.getWert() == maxPunkte && tisch.getDealerHand().getWert() == maxPunkte){
           out.println("servernachricht--blackjack--spielerunddealer");
           hatBlackjack = true;
            if(insuranceWetteGesetzt){
                geld += (insuranceWette + (insuranceWette*2));
                out.println("servernachricht--insurancewettegewonnen--" + String.format("%.2f", insuranceWette * 2) + "--" + String.format("%.2f", geld));
            }
        } else if(tisch.getDealerHand().getWert() == maxPunkte){
            out.println("servernachricht--blackjack--dealer");
            if(insuranceWetteGesetzt){
                geld += (insuranceWette + (insuranceWette*2));
                out.println("servernachricht--insurancewettegewonnen--" + String.format("%.2f", insuranceWette * 2) + "--" + String.format("%.2f", geld));
            }
        } else if(tisch.getDealerOffeneKarte().getBild() == Karte.Bild.Ass && tisch.getDealerOffeneKarte().getWert() != maxPunkte){
            out.println("servernachricht--blackjack--dealerkeinblackjack");
            if(insuranceWetteGesetzt){
                out.println("servernachricht_insurancewetteverloren");
            }
        }
        if (tisch.getDealerOffeneKarte().getBild() == Karte.Bild.Ass){
            out.println("servernachricht--insurancewetteabgeschlossen");
        }
        tisch.spielzugLatchCD();
        if (tisch.getAnzSpieler() > 1){
            out.println("servernachricht--warten--spielzug");
        }


    }

    //Eine Runde spielen
    private void spielenBlackjack(){
        spielerVorbereiten();
        try{
            startLatch.await();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        getWette();
        try {
            wetteLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        try {
            austeilLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        rundenInfoSenden();
        try{
            dealerDrannLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dealerKartenSenden();
        for(Hand hand : spielerHaende) {
            sendeErgebnis(hand);
        }
        getWeiterSpielen();
    }

    //ergebnis der runde an client senden
    private void sendeErgebnis(Hand hand){
        if(hand.wurdeDoubleDown()){
            out.println("servernachricht--entferneumgedrehtedoubledownkarte--" + spielerHaende.indexOf(hand));
            out.println("servernachricht--neuespielerkarte--" + spielerHaende.indexOf(hand) + "--" + hand.getDoubleDownKarte());
        }
        out.println("servernachricht--handwert--" + spielerHaende.indexOf(hand) + "--" + hand.getWert());
        if (!hatBlackjack && !tisch.getDealerHatBlackjack()){
            if(hand.getWert() > maxPunkte && tisch.getDealerHand().getWert() > maxPunkte){
              geld += hand.getWette();
              out.println("servernachricht--ergebnisrunde--bust--unentschieden--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            } else if(hand.getWert() > maxPunkte){
                out.println("servernachricht--ergebnisrunde--bust--dealer--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            } else if (tisch.getDealerHand().getWert() > maxPunkte) {
                geld += hand.getWette()*2;
                out.println("servernachricht--ergebnisrunde--bust--spieler--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            } else {
                if (hand.getWert() == tisch.getDealerHand().getWert()){
                    geld += hand.getWette();
                    out.println("servernachricht--ergebnisrunde--normal--unentschieden--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
                } else if (hand.getWert() < tisch.getDealerHand().getWert()){
                    out.println("servernachricht--ergebnisrunde--normal--dealer--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
                } else if (hand.getWert() > tisch.getDealerHand().getWert()){
                    geld += hand.getWette()*2;
                    out.println("servernachricht--ergebnisrunde--normal--spieler--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
                }
            }
        } else {
            if (hatBlackjack && tisch.getDealerHatBlackjack()){
                geld += hand.getWette();
                out.println("servernachricht--ergebnisrunde--blackjack--unentschieden--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            } else if (!hatBlackjack && tisch.getDealerHatBlackjack()){
                out.println("servernachricht--ergebnisrunde--blackjack--dealer--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            } else if(hatBlackjack && !tisch.getDealerHatBlackjack()){
                //multiplikator für gewinn im falle eines blackjacks
                double blackjackGewinn = 3.0 / 2.0;
                geld += (hand.getWette() + (hand.getWette() * blackjackGewinn));
                out.println("servernachricht--ergebnisrunde--blackjack--spieler--" + spielerHaende.indexOf(hand) + "--" + String.format("%.2f", geld) );
            }
        }
    }

    //prüfen ob spieler weiterspielen will
    private void getWeiterSpielen(){
        if (geld > tisch.getMinWette()){
            auswahlErhalten = false;
            do{
                out.println("servernachricht--weiterspielen");
                getAuswahl();
                if (!auswahl.equals("Ja") && !auswahl.equals("Nein")){
                    out.println("servernachricht--weiterspielenantwort--fehler");
                    auswahlErhalten = false;
                }
            } while (!auswahlErhalten);
            if (auswahl.equals("Ja")){
                weiterSpielen = true;
                out.println("servernachricht--weiterspielenantwort--weiterspielen");
            } else {
                tisch.spielerEntfernen(this);
            }
        } else {
            tisch.spielerEntfernen(this);
        }
        tisch.weiterspielenLatchCD();
    }

    //spieler hand ausgeben
    Hand getHand(){
        return hand;
    }

    //start latch verringern
    void startLatchCD(){
        startLatch.countDown();
    }
    //wette latch verringern
    void wetteLatchCD(){
        wetteLatch.countDown();
    }
    //insurance latch verringern
    void insuranceWetteLatchCD(){
        insuranceWetteLatch.countDown();
    }
    //austeil latch verringern
    void austeilLatchCD(){
        austeilLatch.countDown();
    }
    //dealer drann latch
    void dealerDrannLatchCD(){
        dealerDrannLatch.countDown();
    }
}
