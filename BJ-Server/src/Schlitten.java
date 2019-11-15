import java.util.ArrayList;
import java.util.Collections;

class Schlitten {

    //Attribute
    private ArrayList<Karte> schlitten = new ArrayList<>(); //Liste der Karten die sich im Schlitten befinden

    //Konstruktor
    Schlitten(int anzDecks) {
        for (int i = 0; i < anzDecks; i++) {
            deckHinzu(new Deck());
        }
    }

    //Deck zu Schlitten hinzufügen
    private void deckHinzu(Deck deck){
        for (int i = 0; i < deck.getGroesse(); i++){
            schlitten.add(deck.karteAusteilen());
        }
    }

    //Schlitten mischeln
    void mischeln(){
        Collections.shuffle(schlitten);
    }

    //letzte Karte des Schlittens ausgeben
    Karte karteAusteilen(){
        Karte karte = schlitten.get(schlitten.size() - 1); //letzte Karte im Schlitten
        schlitten.remove(karte);
        return karte;
    }

    //übrige karten im schlitten ausgeben
    int kartenUebrig(){
        return schlitten.size();
    }
}
