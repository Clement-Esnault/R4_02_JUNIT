package simulateurreusine;

import Calculateur.SituationFamiliale;

/**
 * Contient toutes les données décrivant le foyer fiscal.
 * Transmis en entrée à chaque calculateur.
 */
public class FoyerFiscal {

    private int revenuNet;
    private SituationFamiliale situationFamiliale;
    private int nbEnfants;
    private int nbEnfantsHandicapes;
    private boolean parentIsole;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setRevenuNet(int revenuNet) {
        this.revenuNet = revenuNet;
    }

    public void setSituationFamiliale(SituationFamiliale situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }

    public void setNbEnfants(int nbEnfants) {
        this.nbEnfants = nbEnfants;
    }

    public void setNbEnfantsHandicapes(int nbEnfantsHandicapes) {
        this.nbEnfantsHandicapes = nbEnfantsHandicapes;
    }

    public void setParentIsole(boolean parentIsole) {
        this.parentIsole = parentIsole;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public int getRevenuNet() {
        return revenuNet;
    }

    public SituationFamiliale getSituationFamiliale() {
        return situationFamiliale;
    }

    public int getNbEnfants() {
        return nbEnfants;
    }

    public int getNbEnfantsHandicapes() {
        return nbEnfantsHandicapes;
    }

    public boolean isParentIsole() {
        return parentIsole;
    }
}