import java.util.ArrayList;

class Hand {

    //Attribute
    private ArrayList<Karte> hand = new ArrayList<>(); //Liste der Karten die sich in der Hand befinden
    private double wette; //Betrag der Wette die auf die Hand gesetzt werden soll
    private boolean split = false; //Ob die hand gesplittet werden soll oder nicht
    private boolean doubleDown = false; //Ob die wette verdoppelt werden soll oder nicht
    private Karte doubleDownKarte; //karte die hinzugefügt wird nachdem verdoppelt wurde

    //Karte der Hand Hinzufügen
    void karteHinzu(Karte neueKarte){
        hand.add(neueKarte);
    }

    //Wert der Hand ausgeben
    int getWert(){
        int wert = 0;
        for (Karte karte : hand){
            wert += karte.getWert();
        }
        if(istSoft()){
            wert += 10;
        }
        return wert;
    }

    public int wert() {
        int wert = 0;  // value of the hand
        for (Karte card : hand) {
            wert += card.getWert();
        }
        return wert;
    }

    //Prüfen ob Hand ein Ass enthält
    private boolean hatAss(){
        for(Karte karte : hand) {
            if (karte.getWert() == 1){
                return true;
            }
        }
        return false;
    }

    //Prüft ob eine Hand "Soft" ist (hat ass aber ist kleiner 12)
    boolean istSoft(){
        return hatAss() && wert() < 12;
    }

    //Wette setzen
    void setzeWette(double wette){
        this.wette = wette;
    }

    //Wette ausgeben
    double getWette(){
        return wette;
    }

    //Hand "splitten"
    void setPaareSplitten(){
       split = true;
    }

    //prüfen ob gesplittet wurde
    boolean wurdeGesplittet(){
        return split;
    }

    //Wette verdoppeln nach ausgabe der ersten zwei karten ("Double Down")
    void setDoubleDown(){
        doubleDown = true;
    }

    //Prüfen ob Wette verdoppelt wurde
    boolean wurdeDoubleDown(){
        return doubleDown;
    }

    //Karte nach verdoppeln hinzufügen
    void doubleDownKarteHinzu(Karte karte){
        doubleDownKarte = karte;
        karteHinzu(karte);
    }

    //Karte die nach double down hinzugefügt wurde ausgeben

    Karte getDoubleDownKarte(){
        return doubleDownKarte;
    }

    //Anzahl Karten in der Hand ausgeben
    int getGroesse(){
        return hand.size();
    }

    //Karte an bestimmter stelle der Liste ausgeben
    Karte getKarte(int index){
        return hand.get(index);
    }

    //Alle Karten aus der Hand entfernen
    void handLeeren(){
        hand.clear();
    }
}
