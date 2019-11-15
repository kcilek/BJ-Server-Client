import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame implements ActionListener{

    //Attribute
    private static Color hintergrundFarbe = new Color(52,101,0);
    private Client client;

    //willkommen panel attribute
    private JLabel willkommenWartenLabel;

    //einsatz panel attribute
    private JLabel minimumEinsatzLabel;
    private JTextField einsatzField;
    private JButton einsatzButton;
    private JLabel einsatzGeldLabel;
    private JLabel einsatzNachrichtLabel;
    private JLabel einsatzWartenLabel;

    //spielzug panel attribute
    private JPanel dealerHandPanel;
    private JLabel dealerHandWertLabel;
    private JPanel spielerHandPanel;
    private JLabel nachrichtLabel;
    private JButton jaButton;
    private JButton neinButton;
    private JLabel blackjackLabel;
    private JLabel spielzugGeldLabel;
    private JLabel insuranceWetteWartenLabel;
    private JLabel spielzugWartenLabel;

    //weiterspielen panel attribute
    private JLabel weiterspielenNachrichtLabel;
    private JLabel weiterspielenGeldLabel;
    private JLabel weiterspielenWartenLabel;



    //Namen der einzelnen Panele
    private enum PanelNamen{
        willkommenPanel, einsatzPanel, spielzugPanel, weiterspielenPanel
    }

    //Konstruktor
    public GUI(Client client){
        this.client = client;
        erstelleWindowListener(this.client);
        erstelleFrame();
        erstellePanels();
        erstelleActionListener();
    }

    //Window Listener
    private void erstelleWindowListener(Client client){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                int antwort = JOptionPane.showConfirmDialog(null, "Möchten Sie wirklich aufhören zu spielen?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(antwort == JOptionPane.YES_OPTION){
                    client.spielBeenden();
                    System.exit(0);
                }
            }
        });
    }

    //erstelle Frame
    private void erstelleFrame(){
        setTitle("Blackjack");
        setMinimumSize(new Dimension(960, 600));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        aktualisieren();
    }

    //erstelle Panele
    private void erstellePanels(){
        erstelleWillkommenPanel();
        erstelleEinsatzPanel();
        erstelleSpielzugPanel();
        erstelleWeiterspielenPanel();
    }

    //erstelle action listener
    private void erstelleActionListener(){
        einsatzField.addActionListener(this);
        einsatzButton.addActionListener(this);
        jaButton.addActionListener(this);
        neinButton.addActionListener(this);
    }

    //zeige ui änderungen
    private void aktualisieren(){
        revalidate();
        repaint();
        setVisible(true);
    }

    //erstelle willkommen panel
    private void erstelleWillkommenPanel(){
        JPanel willkommenPanel = new JPanel(new GridBagLayout());
        willkommenPanel.setBackground(hintergrundFarbe);
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel willkommenLabel = new JLabel("Willkommen bei Blackjack!");
        willkommenLabel.setForeground(Color.WHITE);
        willkommenLabel.setFont(willkommenLabel.getFont().deriveFont(24.0f));
        constraints.gridy = 0;
        constraints.gridx = 0;
        willkommenPanel.add(willkommenLabel, constraints);
        willkommenWartenLabel = new JLabel("Warte auf weitere Spieler...");
        willkommenWartenLabel.setForeground(Color.WHITE);
        willkommenWartenLabel.setVisible(false);
        constraints.gridy = 1;
        willkommenPanel.add(willkommenWartenLabel, constraints);
        add(willkommenPanel, PanelNamen.willkommenPanel.toString());
    }

    //willkommen warten label an aus
    public void setWillkommenWartenLabel(Boolean b){
        willkommenWartenLabel.setVisible(b);
        aktualisieren();
    }

    //erstelle einsatzpanel
    private void erstelleEinsatzPanel(){
        JPanel einsatzPanel = new JPanel(new GridBagLayout());
        einsatzPanel.setBackground(hintergrundFarbe);
        GridBagConstraints constraints = new GridBagConstraints();
        minimumEinsatzLabel = new JLabel();
        minimumEinsatzLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 0;
        einsatzPanel.add(minimumEinsatzLabel, constraints);
        einsatzField = new JTextField(5);
        constraints.gridy = 1;
        einsatzPanel.add(einsatzField, constraints);
        einsatzNachrichtLabel = new JLabel();
        einsatzNachrichtLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        einsatzPanel.add(einsatzNachrichtLabel, constraints);
        einsatzButton = new JButton("Einsatz platzieren");
        einsatzButton.setPreferredSize(new Dimension(110, 25));
        constraints.gridy = 3;
        einsatzPanel.add(einsatzButton, constraints);
        einsatzGeldLabel = new JLabel();
        einsatzGeldLabel.setForeground(Color.WHITE);
        constraints.gridy = 4;
        einsatzPanel.add(einsatzGeldLabel, constraints);
        einsatzWartenLabel = new JLabel("Warte auf die Einsätze der anderen Spieler...");
        einsatzWartenLabel.setForeground(Color.WHITE);
        einsatzWartenLabel.setVisible(false);
        constraints.gridy = 5;
        einsatzPanel.add(einsatzWartenLabel, constraints);
        add(einsatzPanel, PanelNamen.einsatzPanel.toString());
    }

    //setze minimumeinsatz label
    public void setMinimumEinsatzLabel(String minEinsatz){
        minimumEinsatzLabel.setText("Der Mindesteinsatz beträgt an diesem Tisch €" + minEinsatz + ". Wählen Sie bitte Ihren Einsatz");
        aktualisieren();
    }

    //setze einsatz nachricht zu fehler
    public void einsatzFehler(String fehlerNachricht) {
        einsatzNachrichtLabel.setText(fehlerNachricht);
        aktiviereEinsatzButton(true);
        aktiviereEinsatzField(true);
        aktualisieren();
    }

    //entferne einsatz nachricht text nach erfolgreicher übertrageung
    public void einsatzErfolg() {
        einsatzNachrichtLabel.setText("");
        aktualisieren();
    }

    //setzt vorhandenes geld in einsatz geld lbel
    public void setEinsatzGeldLabel(String geld){
        einsatzGeldLabel.setText("Guthaben: €" + geld);
        aktualisieren();
    }

    //aktiviert / deaktiviert einsatz warten label
    public void setEinsatzWartenLabel(Boolean b){
        einsatzWartenLabel.setVisible(b);
        aktualisieren();
    }

    //erstellt spielzugPanel
    private void erstelleSpielzugPanel() {
        JPanel spielzugPanel = new JPanel(new GridBagLayout());
        spielzugPanel.setBackground(hintergrundFarbe);
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel dealerHandLabel = new JLabel("Dealer's Hand:");
        dealerHandLabel.setForeground(Color.WHITE);
        dealerHandLabel.setFont(dealerHandLabel.getFont().deriveFont(18.0f));
        constraints.gridx = 0;
        constraints.gridy = 0;
        spielzugPanel.add(dealerHandLabel, constraints);
        dealerHandPanel = new JPanel();
        dealerHandPanel.setBackground(hintergrundFarbe);
        JScrollPane dealerHandScrollPane = new JScrollPane(dealerHandPanel);
        dealerHandScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dealerHandScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        dealerHandScrollPane.setPreferredSize(new Dimension(930, 170));
        dealerHandScrollPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.gridy = 1;
        spielzugPanel.add(dealerHandScrollPane, constraints);
        dealerHandWertLabel = new JLabel();
        dealerHandWertLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        spielzugPanel.add(dealerHandWertLabel, constraints);
        JLabel playerHandsLabel = new JLabel("Your Hands:");
        playerHandsLabel.setForeground(Color.WHITE);
        playerHandsLabel.setFont(playerHandsLabel.getFont().deriveFont(18.0f));
        constraints.gridy = 3;
        spielzugPanel.add(playerHandsLabel, constraints);
        spielerHandPanel = new JPanel();
        spielerHandPanel.setBackground(hintergrundFarbe);
        JScrollPane playerHandsScrollPane = new JScrollPane(spielerHandPanel);
        playerHandsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        playerHandsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        playerHandsScrollPane.setPreferredSize(new Dimension(930, 265));
        playerHandsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.gridy = 4;
        spielzugPanel.add(playerHandsScrollPane, constraints);
        nachrichtLabel = new JLabel();
        nachrichtLabel.setForeground(Color.WHITE);
        constraints.gridy = 5;
        spielzugPanel.add(nachrichtLabel, constraints);
        JPanel insuranceBetButtonsPanel = new JPanel();
        insuranceBetButtonsPanel.setBackground(hintergrundFarbe);
        constraints.gridy = 6;
        spielzugPanel.add(insuranceBetButtonsPanel, constraints);
        jaButton = new JButton("Ja");
        jaButton.setPreferredSize(new Dimension(110, 25));
        aktiviereJaButton(false);
        neinButton = new JButton("Nein");
        neinButton.setPreferredSize(new Dimension(110, 25));
        aktiviereNeinButton(false);
        insuranceBetButtonsPanel.add(jaButton);
        insuranceBetButtonsPanel.add(neinButton);
        blackjackLabel = new JLabel();
        blackjackLabel.setForeground(Color.WHITE);
        constraints.gridy = 7;
        spielzugPanel.add(blackjackLabel, constraints);
        spielzugGeldLabel = new JLabel();
        spielzugGeldLabel.setForeground(Color.WHITE);
        constraints.gridy = 8;
        spielzugPanel.add(spielzugGeldLabel, constraints);
        insuranceWetteWartenLabel = new JLabel("Warte auf die Insurance Bets der anderen Spieler...");
        insuranceWetteWartenLabel.setForeground(Color.WHITE);
        insuranceWetteWartenLabel.setVisible(false);
        constraints.gridy = 9;
        spielzugPanel.add(insuranceWetteWartenLabel, constraints);
        spielzugWartenLabel = new JLabel("Warte auf die Spielzüge der anderen Spieler...");
        spielzugWartenLabel.setForeground(Color.WHITE);
        spielzugWartenLabel.setVisible(false);
        constraints.gridy = 10;
        spielzugPanel.add(spielzugWartenLabel, constraints);
        add(spielzugPanel, PanelNamen.spielzugPanel.toString());
    }

    //Dealerkarte hinzufügen
    public void dealerKarteHinzu(JLabel kartenLabel){
        dealerHandPanel.add(kartenLabel);
        aktualisieren();
    }

    //verdeckte dealer karte entfernen
    public void entferneVerdeckteDealerKarte(){
        dealerHandPanel.remove(dealerHandPanel.getComponent(1));
        aktualisieren();
    }

    //setze dealer hand wert
    public void setDealerHandWertLabel(String dealerHandWert){
        dealerHandWertLabel.setText("Wert der Hand: " + dealerHandWert);
        aktualisieren();
    }

    //handpanel zu spieler hinzufügen
    public void spielerHandPanelHinzu(HandPanel spielerhandpanelx, int i){
        spielerHandPanel.add(spielerhandpanelx, i);
        aktualisieren();
    }

    //handpanel von spieler entfernen
    public void spielerHandPanelEntfernen(HandPanel handPanel){
        spielerHandPanel.remove(handPanel);
        aktualisieren();
    }

    //setze spielzug geld label
    public void setSpielzugGeldLabel(String geld){
        spielzugGeldLabel.setText("Guthaben: €" + geld);
        aktualisieren();
    }

    //setze nachrichten label
    public void setNachrichtLabel(String nachricht){
       nachrichtLabel.setText(nachricht);
       aktualisieren();
    }

    //aktiviere insurance bets
    public void aktiviereInsuranceWette(){
        setNachrichtLabel("Möchten Sie eine Insurance Bet spielen?");
        aktiviereJaButton(true);
        aktiviereNeinButton(true);
        aktualisieren();
    }

    //falls fehler bei isnurance bet passier
    public void insuranceWetteFehler(){
        setNachrichtLabel("FEHLER");
        aktiviereJaButton(true);
        aktiviereNeinButton(true);
        aktualisieren();
    }

    //deaktiviere ja nein button wenn insurance bet erfolgreich abgeschlossen wurde
    public void insuranceWetteErfolg(){
        aktiviereJaButton(false);
        aktiviereNeinButton(false);
        aktualisieren();
    }

    //nachrichten label leeren
    public void entferneInsuranceInfo(){
        nachrichtLabel.setText("");
        aktualisieren();
    }

    //aktiviere weiterspielen dialog
    public void aktiviereWeiterspielen(){
        setNachrichtLabel("Würden Sie gerne eine weitere Runde spielen?");
        aktiviereJaButton(true);
        aktiviereNeinButton(true);
        aktualisieren();
    }

    //falls fehler bei weiterspielen passiert
    public void weiterspielenFehler(){
        setNachrichtLabel("FEHLER");
        aktiviereJaButton(true);
        aktiviereNeinButton(true);
        aktualisieren();
    }

    //setze nachricht in blackjacklabel
    public void setBlackjackLabel(String nachricht){
        blackjackLabel.setText(nachricht);
        aktualisieren();
    }

    //zeige insurance warten
    public void setInsuranceWetteWartenLabel(Boolean b){
        insuranceWetteWartenLabel.setVisible(b);
        aktualisieren();
    }

    //zeige spielzug warten
    public void setSpielzugWartenLabel(Boolean b){
        spielzugWartenLabel.setVisible(b);
        aktualisieren();
    }

    //erstelle weiterspielen panel
    public void erstelleWeiterspielenPanel(){
        JPanel continuePlayingPanel = new JPanel(new GridBagLayout());
        continuePlayingPanel.setBackground(hintergrundFarbe);
        GridBagConstraints constraints = new GridBagConstraints();
        weiterspielenNachrichtLabel = new JLabel();
        weiterspielenNachrichtLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 0;
        continuePlayingPanel.add(weiterspielenNachrichtLabel, constraints);
        JPanel continuePlayingButtonsPanel = new JPanel();
        continuePlayingButtonsPanel.setBackground(hintergrundFarbe);
        constraints.gridy = 1;
        continuePlayingPanel.add(continuePlayingButtonsPanel, constraints);
        weiterspielenGeldLabel = new JLabel();
        weiterspielenGeldLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        continuePlayingPanel.add(weiterspielenGeldLabel, constraints);
        weiterspielenWartenLabel = new JLabel("Warte auf den Beitritt weiterer Spieler");
        weiterspielenWartenLabel.setForeground(Color.WHITE);
        weiterspielenWartenLabel.setVisible(false);
        constraints.gridy = 3;
        continuePlayingPanel.add(weiterspielenWartenLabel, constraints);
        add(continuePlayingPanel, PanelNamen.weiterspielenPanel.toString());
    }

    //setze weiterspielen geld labe
    public void setWeiterspielenGeldLabel(String geld){
        weiterspielenGeldLabel.setText("Guthaben: €" + geld);
        aktualisieren();
    }

    //weiterspielen anchricht zu spiel vorbei
    public void spielVorbei(){
        weiterspielenNachrichtLabel.setText("Uns gehört jetzt Ihr ganzes Geld. Vielen Dank.");
        aktualisieren();
    }

    //zeige weiterspielen warten label
    public void setWeiterspielenWartenLabel(Boolean b){
        weiterspielenWartenLabel.setVisible(b);
        aktualisieren();
    }

    //aktiviere / deaktiviere einsatz feld
    private void aktiviereEinsatzField(Boolean b){
        einsatzField.setEnabled(b);
        aktualisieren();
    }

    //aktiviere / eaktiviere einsatz knöpfchen
    private void aktiviereEinsatzButton(Boolean b){
        einsatzButton.setEnabled(b);
        einsatzButton.setVisible(b);
        aktualisieren();
    }

    //aktiviere / deaktiviere JA Button
    private void aktiviereJaButton(Boolean b){
        jaButton.setEnabled(b);
        jaButton.setVisible(b);
        aktualisieren();
    }

    //aktiviere / deaktiviere nein button
    private void aktiviereNeinButton(Boolean b){
        neinButton.setEnabled(b);
        neinButton.setVisible(b);
        aktualisieren();
    }

    //zeigt panel mit bestimmtem namen
    private void zeigePanel(PanelNamen panelname){
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), panelname.toString());
        aktualisieren();
    }

    //zeigt willkommen panel
    public void zeigeWillkommenPanel(){
        zeigePanel(PanelNamen.willkommenPanel);
    }

    //zeigt einsatzpanel
    public void zeigeEinsatzPanel(){
        zeigePanel(PanelNamen.einsatzPanel);
    }

    //zeigt spielzugpanel
    public void zeigeSpielzugPanel(){
        zeigePanel(PanelNamen.spielzugPanel);
    }

    //zeigt weiterspielenpanel
    public void zeigeWeiterspielenPanel(){
        zeigePanel(PanelNamen.weiterspielenPanel);
    }

    //setze gui zurück
    public void zuruecksetzen(){
        erstellePanels();
        erstelleActionListener();
        zeigeWeiterspielenPanel();
    }

    //action performed
    @Override
    public void actionPerformed(ActionEvent e){
        Object target = e.getSource();
        if (target == einsatzField || target == einsatzButton) {
            client.sendeClientNachricht(einsatzField.getText());
            aktiviereEinsatzField(false);
            aktiviereEinsatzButton(false);
        } else if (target == jaButton){
            client.sendeClientNachricht(jaButton.getText());
            aktiviereJaButton(false);
            aktiviereNeinButton(false);
        } else if (target == neinButton){
            client.sendeClientNachricht(neinButton.getText());
            aktiviereNeinButton(false);
            aktiviereJaButton(false);
        }

    }
}

