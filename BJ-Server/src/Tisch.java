import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Tisch implements Runnable{

    private ArrayList<Spieler> tisch = new ArrayList<>();
    private int minWette;
    private int anzDecks;
    private int minKartenVorMischeln;
    private Schlitten schlitten;
    private Hand dealerHand = new Hand();
    private boolean dealerHatBlackjack;
    private CountDownLatch wetteGesetztLatch;
    private CountDownLatch insuranceWetteGesetztLatch;
    private CountDownLatch spielzugLatch;
    private CountDownLatch weiterspielenLatch;

    //Konstruktor
    Tisch(int minWette, int anzDecks, int minKartenVorMischeln){
        this.minWette = minWette;
        this.anzDecks = anzDecks;
        this.minKartenVorMischeln = minKartenVorMischeln;
    }

    //Prozess Run Methode
    @Override
    public void run(){
        schlitten = new Schlitten(anzDecks);
        schlitten.mischeln();
        do{
            spielBlackjack();
        } while(getAnzSpieler()>0);
    }

    //Spielablauf
    private void spielBlackjack(){
        tischVorbereiten();
        for (Spieler spieler : tisch){
            spieler.startLatchCD();
        }
        try{
            wetteGesetztLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        for (Spieler spieler : tisch){
            spieler.wetteLatchCD();
        }
        startKartenAusteilen();
        for (Spieler spieler : tisch){
            spieler.austeilLatchCD();
        }
        try{
            insuranceWetteGesetztLatch.await();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        for (Spieler spieler : tisch){
            spieler.insuranceWetteLatchCD();
        }
        try {
            spielzugLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        for (Spieler spieler : tisch){
            spieler.macheZug(spieler.getHand());
        }
        dealerSpielzug();
        for (Spieler spieler : tisch){
            spieler.dealerDrannLatchCD();
        }
        try {
            weiterspielenLatch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //karte austeilen
    Karte karteAusteilen(){
        if (schlitten.kartenUebrig() == 0){
            schlitten = new Schlitten(anzDecks);
            schlitten.mischeln();
        }
        return schlitten.karteAusteilen();
    }

    //Tisch für eine neue Runde vorbereiten
    private void tischVorbereiten(){
        if (schlitten.kartenUebrig() <= minKartenVorMischeln){
            schlitten = new Schlitten(anzDecks);
            schlitten.mischeln();
        }
        dealerHand.handLeeren();
        dealerHatBlackjack = false;
        wetteGesetztLatch = new CountDownLatch(getAnzSpieler());
        insuranceWetteGesetztLatch = new CountDownLatch(getAnzSpieler());
        spielzugLatch = new CountDownLatch(getAnzSpieler());
        weiterspielenLatch = new CountDownLatch(getAnzSpieler());
    }

    //Die ersten Karten ausgeben
    private void startKartenAusteilen(){
        for (int i = 0; i < 2; i++){
            dealerHand.karteHinzu(karteAusteilen());
            for (Spieler spieler : tisch){
                spieler.getHand().karteHinzu(karteAusteilen());
            }
        }
        //Attribute
        int maxPunkte = 21;
        if (dealerHand.getWert() == maxPunkte){
            dealerHatBlackjack = true;
        }
    }

    //Dealer ist drann
    private void dealerSpielzug(){
        int dealerZiel = 17;
        while ((dealerHand.istSoft() && dealerHand.getWert() == dealerZiel) || dealerHand.getWert() < dealerZiel){
           dealerHand.karteHinzu(karteAusteilen());
        }
    }

    //Spieler dem tisch hinzufügen
    void spielerHinzu(Spieler spieler){
        tisch.add(spieler);
    }

    //Spieler dem tisch entfernen
    void spielerEntfernen(Spieler spieler){
        tisch.remove(spieler);
    }

    //Anzahl spieler am tisch ausgeben
    int getAnzSpieler(){
        return tisch.size();
    }

    //minimum wette ausgeben
    double getMinWette(){
        return minWette;
    }

    //asusgeben ob dealer blackjack hat oder nicht
    boolean getDealerHatBlackjack(){
        return dealerHatBlackjack;
    }

    //offene karte des dealers ausgeben
    Karte getDealerOffeneKarte(){
        return dealerHand.getKarte(0);
    }

    //hand des dealers ausgeben
    Hand getDealerHand(){
        return dealerHand;
    }

    //countdown wette gesetzt latch
    void wetteGesetztLatchCD(){
        wetteGesetztLatch.countDown();
    }
    //countdown insurance wette latch
    void insuranceWetteGesetztLatchCD(){
        insuranceWetteGesetztLatch.countDown();
    }
    //countdown spielzug latch
    void spielzugLatchCD(){
        spielzugLatch.countDown();
    }
    //wetierspielen latch countdown
    void weiterspielenLatchCD(){
        weiterspielenLatch.countDown();
    }
}
