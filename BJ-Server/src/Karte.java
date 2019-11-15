
public class Karte{

    //Attribute
    private Symbol dasSymbol;
    private Bild dasBild;

    //Konstruktor
    Karte(Bild einBild, Symbol einSymbol) {
        dasSymbol = einSymbol;
        dasBild = einBild;
    }

    //Definieren der möglichen Bilder
    public enum Bild {
        Ass(1),
        Zwei(2),
        Drei(3),
        Vier(4),
        Fuenf(5),
        Sechs(6),
        Sieben(7),
        Acht(8),
        Neun(9),
        Zehn(10),
        Bube(10),
        Dame(10),
        Koenig(10);

        private int wert;

        Bild(int wert) {
            this.wert = wert;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    //Definieren der möglichen Symbole
    public enum Symbol {
        Kreuz,
        Karo,
        Pik,
        Herz;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    //Wert der Karte ausgeben
    int getWert() {
        return dasBild.wert;
    }

    //Bild der Karte ausgeben
    Bild getBild(){
        return dasBild;
    }

    //Karte als String ausgeben
    public String toString(){
        return dasSymbol + "-" + dasBild;
    }

}