package simulateurreusine;

import Calculateur.SituationFamiliale;

/**
 * Contient toutes les données décrivant le foyer fiscal. Transmis en entrée à
 * chaque calculateur.
 */
public final class FoyerFiscal {

	private int revenuNet;
	private SituationFamiliale situationFamiliale;
	private int nbEnfants;
	private int nbEnfantsHandicapes;
	private boolean parentIsole;

	// -------------------------------------------------------------------------
	// Setters
	// -------------------------------------------------------------------------

	public void setRevenuNet(int valeur) {
		this.revenuNet = valeur;
	}

	public void setSituationFamiliale(SituationFamiliale valeur) {
		this.situationFamiliale = valeur;
	}

	public void setNbEnfants(int valeur) {
		this.nbEnfants = valeur;
	}

	public void setNbEnfantsHandicapes(int valeur) {
		this.nbEnfantsHandicapes = valeur;
	}

	public void setParentIsole(boolean valeur) {
		this.parentIsole = valeur;
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