import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //Attribute
    private int serverPort;
    private int anzSpieler;
    private int startGeld;
    private int minWette;
    private int anzDecks;
    private int minMischeln;

    //Konstruktor
    private Server(int serverPort, int anzSpieler, int startGeld, int minWette, int anzDecks, int minMischeln){
       this.serverPort = serverPort;
       this.anzSpieler = anzSpieler;
       this.startGeld = startGeld;
       this.minWette = minWette;
       this.anzDecks = anzDecks;
       this.minMischeln = minMischeln;
    }

    //server starten und clients mit tisch verbinden
    private void serverStart(){
        System.out.println("Server wird gestartet");
        System.out.println("Port: " + serverPort);
        System.out.println("Spieler pro Tisch: " + anzSpieler);
        System.out.println("Anzahl Decks: " + anzDecks);
        System.out.println("Startguthaben: " + startGeld);
        System.out.println("Mindesteinsatz: " + minWette);
        System.out.println("Mischeln ab Karten: " + minMischeln);
        ServerSocket serverSocket = null;
        try{
            System.out.println("ServerSocket wird angelegt");
            serverSocket = new ServerSocket(serverPort);
        } catch(IOException e){
            System.out.println("Fehler bei starten des Servers");
            System.exit(1);
        }
        try {
            System.out.println("Port "+serverPort+" geoeffnet");
            System.out.println("Server erfolgreich gestartet");
            do {
                Tisch tisch = new Tisch(minWette, anzDecks, minMischeln);
                Thread tischThread = new Thread(tisch);
                for (int i = 0; i < anzSpieler; i++) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Verbindung aufgebaut mit:  " + socket.getPort());
                    Spieler spieler = new Spieler(socket, tisch, startGeld);
                    tisch.spielerHinzu(spieler);
                    Thread spielerThread = new Thread(spieler);
                    spielerThread.start();
                }
                tischThread.start();
            } while (true);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    //Main Methode mit möglichkeit servereinstellungen zu ändern
    public static void main(String[] args){
        int serverPort = 1337;
        int anzSpieler = 1;
        int anzGeld = 1000;
        int anzDecks = 6;
        int minMischeln = 78;
        int minWette = 100;

        for (int i = 0; i< args.length; i+=2){
            String einstellung = args[i];
            String argument = null;
            try{
                argument = args[i + 1];
            } catch (ArrayIndexOutOfBoundsException e){
                System.err.println("Einstellungen: -p Port; -t Spieler pro Tisch; -g Startguthaben; -w minimum Wette; -d Anzahl der Decks; -m Mindest Anzahl Karten vor Mischeln");
                System.exit(1);
            }
            switch (einstellung) {
                case "-p":
                    try{
                        serverPort = Integer.parseInt(argument);
                    } catch (NumberFormatException e){
                        System.err.println("Port muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                case "-t":
                    try{
                        anzSpieler = Integer.parseInt(argument);
                    } catch(NumberFormatException e){
                        System.err.println("Anzahl Spieler muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                case "-g":
                    try{
                        anzGeld = Integer.parseInt(argument);
                    } catch(NumberFormatException e){
                        System.err.println("Startguthaben muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                case "-w":
                    try{
                        minWette = Integer.parseInt(argument);
                    } catch(NumberFormatException e){
                        System.err.println("Mindesteinsatz muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                case "-d":
                    try{
                        anzDecks = Integer.parseInt(argument);
                    } catch(NumberFormatException e){
                        System.err.println("Anzahl Decks muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                case "-m":
                    try{
                        minMischeln = Integer.parseInt(argument);
                    } catch(NumberFormatException e){
                        System.err.println("Anzahl Karten vor Mischeln muss eine ganze positive Zahl sein");
                        System.exit(1);
                    }
                    break;
                default:
                    System.err.println("Einstellungen: -p Port; -t Spieler pro Tisch; -g Startguthaben; -w minimum Wette; -d Anzahl der Decks; -m Mindest Anzahl Karten vor Mischeln");
                    System.exit(0);
                    break;
            }
        }
        if (anzSpieler < 1){
            System.err.println("Anzahl Spieler muss mindestens 1 sein");
            System.exit(1);
        } else if (anzGeld < minWette){
            System.err.println("Startguthaben muss groesser als Mindesteinsatz sein");
            System.exit(1);
        } else if (anzDecks < 1){
            System.err.println("Es muss mindestens 1 Deck geben");
            System.exit(1);
        } else if (minMischeln < 0){
            System.err.println("Mindestanzahl Karten vor dem mischeln darf nicht weniger als 0 sein");
            System.exit(1);
        }
        Server server = new Server(serverPort, anzSpieler, anzGeld, minWette, anzDecks, minMischeln);
        server.serverStart();
    }

}
