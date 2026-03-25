package simulateurreusine;

import Calculateur.SituationFamiliale;

/**
 * Calcule l'impôt sur le revenu 2024 (revenus 2023) — art. 197 CGI.
 *

 */
public class Simulateur {

	// -------------------------------------------------------------------------
	// Barème progressif  
	// -------------------------------------------------------------------------

	private static final int[] LIMITES_TRANCHES = { 0, 11_294, 28_797, 82_341, 177_106, Integer.MAX_VALUE };
	private static final double[] TAUX_TRANCHES = { 0.00, 0.11, 0.30, 0.41, 0.45 };

	// -------------------------------------------------------------------------
	// Abattement forfaitaire 10 % 
	// -------------------------------------------------------------------------

	private static final double TAUX_ABATTEMENT = 0.10;
	private static final int ABATTEMENT_MINIMUM = 495;
	private static final int ABATTEMENT_MAXIMUM = 14_171;

	// -------------------------------------------------------------------------
	// Plafonnement du quotient familial 
	// -------------------------------------------------------------------------

	private static final double PLAFOND_DEMI_PART = 1_759.0;

	// -------------------------------------------------------------------------
	// Décote 
	// -------------------------------------------------------------------------

	private static final double SEUIL_DECOTE_SEUL = 1_929.0;
	private static final double SEUIL_DECOTE_COUPLE = 3_191.0;
	private static final double DECOTE_MAX_SEUL = 873.0;
	private static final double DECOTE_MAX_COUPLE = 1_444.0;
	private static final double TAUX_DECOTE = 0.4525;

	// -------------------------------------------------------------------------
	// Paramètres 
	// -------------------------------------------------------------------------

	private int revenuNet;
	private SituationFamiliale situationFamiliale;
	private int nbEnfants;
	private int nbEnfantsHandicapes;
	private boolean parentIsole;

	// -------------------------------------------------------------------------
	// Résultats intermédiaires exposés via les getters
	// -------------------------------------------------------------------------

	private int abattement;
	private int revenuFiscalReference;
	private double nbPartsDeclarants;
	private double nbPartsFoyerFiscal;
	private double impotAvantDecote;
	private double decote;
	private double impotNet;

	// -------------------------------------------------------------------------
	// Setters
	// -------------------------------------------------------------------------

	public void setRevenusNet(int revenuNet) {
		this.revenuNet = revenuNet;
	}

	public void setSituationFamiliale(SituationFamiliale sf) {
		this.situationFamiliale = sf;
	}

	public void setNbEnfantsACharge(int nbEnfantsACharge) {
		this.nbEnfants = nbEnfantsACharge;
	}

	public void setNbEnfantsSituationHandicap(int nbEnfantsSituationH) {
		this.nbEnfantsHandicapes = nbEnfantsSituationH;
	}

	public void setParentIsole(boolean parentIsole) {
		this.parentIsole = parentIsole;
	}

	// -------------------------------------------------------------------------
	// Calcul principal
	// -------------------------------------------------------------------------

	/**
	 * Lance le calcul complet de l'impôt sur le revenu net. Les résultats sont
	 * ensuite accessibles via les getters.
	 *
	 * @throws IllegalArgumentException si la situation familiale est nulle (ERR-01)
	 */
	public void calculImpotSurRevenuNet() {
		if (situationFamiliale == null) {
			throw new IllegalArgumentException("La situation familiale doit être renseignée.");
		}
		calculerAbattement(); 
		calculerQuotientFamilial(); 
		double impotDeclarants = calculerImpotPourNbParts(nbPartsDeclarants);
		double impotFoyer = calculerImpotPourNbParts(nbPartsFoyerFiscal);
		appliquerPlafonnement(impotDeclarants, impotFoyer); // art. 197 CGI
		appliquerDecote(); // art. 197 CGI
	}

	// -------------------------------------------------------------------------
	// Étape 1 — Abattement forfaitaire 
	// -------------------------------------------------------------------------

	
	// ABATTEMENT_MAXIMUM
	private void calculerAbattement() {
		int revenuEffectif = Math.max(revenuNet, 0); // ERR-04 : revenu négatif traité comme 0
		double abattementBrut = revenuEffectif * TAUX_ABATTEMENT;
		abattement = (int) Math.round(Math.min(Math.max(abattementBrut, ABATTEMENT_MINIMUM), ABATTEMENT_MAXIMUM));
		revenuFiscalReference = Math.max(revenuEffectif - abattement, 0);
	}

	// -------------------------------------------------------------------------
	// Étape 2 — Quotient familial 
	// -------------------------------------------------------------------------

	private void calculerQuotientFamilial() {
		nbPartsDeclarants = partsDeclarants();
		nbPartsFoyerFiscal = nbPartsDeclarants + partsEnfants() + partsMajorationParentIsole()
				+ partsEnfantsHandicapes();
	}

	// part pour célibataire/divorcé/veuf, 2 parts pour marié/pacsé
	private double partsDeclarants() {
		return switch (situationFamiliale) {
		case CELIBATAIRE, DIVORCE, VEUF -> 1.0;
		case MARIE, PACSE -> 2.0;
		};
	}

	// 0.5 part par enfant jusqu'au 2ème, 1 part entière à partir du 3ème
	private double partsEnfants() {
		int n = Math.max(nbEnfants, 0); // ERR-02 : enfants négatifs traités comme 0
		return (n <= 2) ? n * 0.5 : 1.0 + (n - 2);
	}

	//  +0.5 part si parent isolé avec au moins 1 enfant
	private double partsMajorationParentIsole() {
		return (parentIsole && Math.max(nbEnfants, 0) > 0) ? 0.5 : 0.0;
	}

	// +0.5 part par enfant handicapé, plafonné au nb d'enfants réels
	private double partsEnfantsHandicapes() {
		int n = Math.min(Math.max(nbEnfantsHandicapes, 0), Math.max(nbEnfants, 0)); // ERR-03
		return n * 0.5;
	}

	// -------------------------------------------------------------------------
	// Barème progressif 
	// -------------------------------------------------------------------------

	private double calculerImpotPourNbParts(double nbParts) {
		double revenuParPart = revenuFiscalReference / nbParts;
		double impot = 0.0;
		for (int i = 0; i < TAUX_TRANCHES.length; i++) {
			if (revenuParPart <= LIMITES_TRANCHES[i])
				break;
			double base = Math.min(revenuParPart, LIMITES_TRANCHES[i + 1]) - LIMITES_TRANCHES[i];
			impot += base * TAUX_TRANCHES[i];
		}
		return Math.round(impot * nbParts);
	}

	// -------------------------------------------------------------------------
	//Plafonnement du quotient familial 
	// -------------------------------------------------------------------------

	// l'avantage des parts supplémentaires est plafonné
	private void appliquerPlafonnement(double impotDeclarants, double impotFoyer) {
		double demiPartsSup = nbPartsFoyerFiscal - nbPartsDeclarants;
		double plafond = (demiPartsSup / 0.5) * PLAFOND_DEMI_PART;
		double reduction = impotDeclarants - impotFoyer;
		impotAvantDecote = (reduction > plafond) ? impotDeclarants - plafond : impotFoyer;
	}

	// -------------------------------------------------------------------------
	//  Décote 
	// -------------------------------------------------------------------------

	// décote appliquée si impôt < seuil selon type de foyer
	private void appliquerDecote() {
		boolean estCouple = (nbPartsDeclarants == 2);
		double seuilDecote = estCouple ? SEUIL_DECOTE_COUPLE : SEUIL_DECOTE_SEUL;
		double decoteMax = estCouple ? DECOTE_MAX_COUPLE : DECOTE_MAX_SEUL;
		decote = (impotAvantDecote < seuilDecote)
				? Math.min(Math.round(decoteMax - impotAvantDecote * TAUX_DECOTE), impotAvantDecote)
				: 0.0;
		impotNet = Math.round(impotAvantDecote - decote);
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	public int getRevenuFiscalReference() {
		return revenuFiscalReference;
	}

	public int getAbattement() {
		return abattement;
	}

	public double getNbPartsFoyerFiscal() {
		return nbPartsFoyerFiscal;
	}

	public int getImpotAvantDecote() {
		return (int) Math.round(impotAvantDecote);
	}

	public int getDecote() {
		return (int) Math.round(decote);
	}

	public int getImpotSurRevenuNet() {
		return (int) Math.round(impotNet);
	}
}