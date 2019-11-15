
import java.util.ArrayList;

class Deck {

    //Attribute
    private ArrayList<Karte> deck = new ArrayList<>(); //Liste der Karten die sich im Deck befinden

    //Konstruktor
    Deck() {
        for (Karte.Symbol symbol : Karte.Symbol.values()) {
            for (Karte.Bild bild : Karte.Bild.values()) {
                deck.add(new Karte(bild, symbol));
            }
        }
    }

    //Karten austeilen
    Karte karteAusteilen() {
        Karte karte = deck.get(deck.size() - 1);    // letzte Karte im Deck
        deck.remove(karte);
        return karte;
    }

    //Anzahl Karten im deck ausgeben
    int getGroesse() {
        return deck.size();
    }


}
