import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

public class HandPanel extends JPanel implements ActionListener{

    //Attribute
    private static Color hintergrundFarbe = new Color(52, 101, 0);
    private static Dimension buttonDimension = new Dimension(110,25);
    private Client client;
    private JPanel kartenPanel;
    private JLabel handWertLabel;
    private JLabel handEinsatzLabel;
    private JLabel handNachrichtLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton splitPairsButton;
    private JButton doubleDownButton;

    //Konstruktor
    public HandPanel(Client client){
        this.client = client;
        bauePanel();
        baueActionListeners();
    }

    //Panel basteln
    private void bauePanel(){
        setBackground(hintergrundFarbe);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        kartenPanel = new JPanel();
        kartenPanel.setBackground(hintergrundFarbe);
        constraints.gridy = 0;
        constraints.gridx = 0;
        add(kartenPanel, constraints);
        handWertLabel = new JLabel();
        handWertLabel.setForeground(Color.WHITE);
        constraints.gridy = 1;
        add(handWertLabel, constraints);
        handEinsatzLabel = new JLabel();
        handEinsatzLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        add(handEinsatzLabel, constraints);
        handNachrichtLabel = new JLabel();
        handNachrichtLabel.setForeground(Color.WHITE);
        constraints.gridy = 3;
        add(handNachrichtLabel, constraints);
        JPanel hitStandButtonsPanel = new JPanel();
        hitStandButtonsPanel.setBackground(hintergrundFarbe);
        hitButton = new JButton("Hit");
        hitButton.setPreferredSize(buttonDimension);
        hitButton.setEnabled(false);
        hitButton.setVisible(false);
        standButton = new JButton("Stand");
        standButton.setPreferredSize(buttonDimension);
        standButton.setEnabled(false);
        standButton.setVisible(false);
        hitStandButtonsPanel.add(hitButton);
        hitStandButtonsPanel.add(standButton);
        constraints.gridy = 4;
        add(hitStandButtonsPanel, constraints);
        JPanel jaNeinButtonsPanel = new JPanel();
        jaNeinButtonsPanel.setBackground(hintergrundFarbe);
        splitPairsButton = new JButton("Split Pairs");
        splitPairsButton.setPreferredSize(buttonDimension);
        splitPairsButton.setEnabled(false);
        splitPairsButton.setVisible(false);
        doubleDownButton = new JButton("Double Down");
        doubleDownButton.setPreferredSize(buttonDimension);
        doubleDownButton.setEnabled(false);
        doubleDownButton.setVisible(false);
        jaNeinButtonsPanel.add(splitPairsButton);
        jaNeinButtonsPanel.add(doubleDownButton);
        constraints.gridy = 5;
        add(jaNeinButtonsPanel, constraints);
    }

    //action listeners
    private void baueActionListeners(){
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        splitPairsButton.addActionListener(this);
        doubleDownButton.addActionListener(this);
    }

    //zeige änderungen
    private void aktualisieren(){
        revalidate();
        repaint();
        setVisible(true);
    }

    //setze handwertlabel
    public void setHandWertLabel(String handwert){
        handWertLabel.setText("Wert Ihrer Hand: " + handwert);
        aktualisieren();
    }

    //setze handeinsatzlabel
    public void setHandEinsatzLabel(String handeinsatz){
        handEinsatzLabel.setText("Einsatz: €" + handeinsatz);
        aktualisieren();
    }

    //setze hand nachricht label
    public void setHandNachrichtLabel(String handnachricht){
        handNachrichtLabel.setText(handnachricht);
        aktualisieren();
    }

    //setze hand nachricht zu fehler
    public void spielzugFehler(){
        setHandNachrichtLabel("Fehler");
        aktualisieren();
    }

    //aktiviere hitstand buttons
    public void aktiviereHitStand(){
        aktiviereHitButton(true);
        aktiviereStandButton(true);
        aktualisieren();
    }

    //karte hinzu
    public void karteHinzu(JLabel kartenLabel){
        kartenPanel.add(kartenLabel);
        aktualisieren();
    }

    //bust nachricht
    public void bust(){
        setHandNachrichtLabel("Sie haben die 21 überschritten. Bust.");
        aktualisieren();
    }

    //aktivier splitpairs button
    public void aktiviereSplitPairs(){
        aktiviereSplitPairsButton(true);
        aktualisieren();
    }

    //aktivier double down button
    public void aktiviereDoubleDown(){
        aktiviereDoubleDownButton(true);
        aktualisieren();
    }

    //nachricht zu double down erfolgreich
    public void doubleDownErfolg(){
        setHandNachrichtLabel("Ihr Einsatz auf diese Hand wurde verdoppelt. Double Down.");
        aktualisieren();
    }

    //entfernt umgedrehte double down karte
    public void entferneUmgedrehteDoubleDownKarte(){
        kartenPanel.remove(kartenPanel.getComponent(2));
        aktualisieren();
    }

    //aktiviere hit button
    private void aktiviereHitButton(Boolean b){
        hitButton.setEnabled(b);
        hitButton.setVisible(b);
        aktualisieren();
    }

    //aktiviere stand button
    private void aktiviereStandButton(Boolean b){
        standButton.setEnabled(b);
        standButton.setVisible(b);
        aktualisieren();
    }

    //aktiviere splitpairs button
    public void aktiviereSplitPairsButton(Boolean b){
        splitPairsButton.setEnabled(b);
        splitPairsButton.setVisible(b);
        aktualisieren();
    }

    //aktiviere double down button
    public void aktiviereDoubleDownButton(Boolean b){
        doubleDownButton.setEnabled(b);
        doubleDownButton.setVisible(b);
        aktualisieren();
    }
    //wenn knöpfe gedrückt werden
    @Override
    public void actionPerformed(ActionEvent e){
        Object target = e.getSource();
        if(target == hitButton) {
            client.sendeClientNachricht(hitButton.getText());
        } else if (target == standButton) {
            client.sendeClientNachricht(standButton.getText());
        } else if (target == splitPairsButton) {
            client.sendeClientNachricht(splitPairsButton.getText());
        } else if (target == doubleDownButton) {
            client.sendeClientNachricht(doubleDownButton.getText());
        }
        aktiviereHitButton(false);
        aktiviereStandButton(false);
        aktiviereSplitPairsButton(false);
        aktiviereDoubleDownButton(false);
    }

}
