import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Info {

    //Attribute
    private static int nachrichtWartezeit = 500;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<HandPanel> spielerHandPanele = new ArrayList<>();

    //Konstruktor
    public Info(String serverAdresse, int serverPort){
        try{
            socket = new Socket(serverAdresse, serverPort);
        } catch (IOException e){
            System.out.println("Kein Server auf Port " + serverPort);
            System.exit(1);
        }
        try{
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Servernachricht empfangen
    public String getServerNachricht(){
        String serverNachricht = null;
        try{
            Thread.sleep(nachrichtWartezeit);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        while (serverNachricht == null){
            try{
                serverNachricht = in.readLine();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return serverNachricht;
    }

    //Clientnachricht senden
    public void sendeClientNachricht(String clientNachricht){
        out.println(clientNachricht);
    }

    //HandPanel der Liste hinzufügen
    public void handPanelHinzu(int index, HandPanel handpanel){
        spielerHandPanele.add(index, handpanel);
    }

    //Handpanel aus der Liste ausgeben
    public HandPanel getHandPanel(int index){
        return spielerHandPanele.get(index);
    }

    //Handpanel aus der Liste entfernen
    public void entferneHandPanel(int index){
        spielerHandPanele.remove(index);
    }

    //HandPanel Liste zurücksetzen
    public void zuruecksetzen(){
        spielerHandPanele.clear();
    }

    //jLabel mit Spielkarte Bild ausgeben
    public JLabel getKarteLabel(String kartenName){
        JLabel karteLabel = null;
        try{
            karteLabel = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream(kartenName + ".png"))));
        }catch (IOException e){
            e.printStackTrace();
        }
        return karteLabel;
    }

    //Spiel beenden
    public void spielBeenden(){
        sendeClientNachricht("clientnachricht--spielbeenden");
        try{
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
