package simulateurreusine;

import Calculateur.SituationFamiliale;

/**
 * Orchestrateur principal.
 *
 * Coordonne les cinq calculateurs dans l'ordre légal et expose les mêmes
 * getters que l'ancienne classe monolithique, sans modifier le comportement des
 * calculs.
 */
public final class Simulateur {

	// Paramètres d'entrée (conservés pour compatibilité des setters existants)
	private final FoyerFiscal foyer = new FoyerFiscal();

	// Calculateurs — instanciés une fois, réutilisables via les getters
	private final CalculateurAbattement calculateurAbattement = new CalculateurAbattement();
	private final CalculateurQuotient calculateurQuotient = new CalculateurQuotient();
	private final CalculateurBareme calculateurBareme = new CalculateurBareme();
	private final CalculateurPlafonnement calculateurPlafonnement = new CalculateurPlafonnement();
	private final CalculateurDecote calculateurDecote = new CalculateurDecote();

	// -------------------------------------------------------------------------
	// Setters (API publique inchangée)
	// -------------------------------------------------------------------------

	public void setRevenusNet(int revenuNet) {
		foyer.setRevenuNet(revenuNet);
	}

	public void setSituationFamiliale(SituationFamiliale sf) {
		foyer.setSituationFamiliale(sf);
	}

	public void setNbEnfantsACharge(int nbEnfantsACharge) {
		foyer.setNbEnfants(nbEnfantsACharge);
	}

	public void setNbEnfantsSituationHandicap(int nbEnfantsSituationH) {
		foyer.setNbEnfantsHandicapes(nbEnfantsSituationH);
	}

	public void setParentIsole(boolean parentIsole) {
		foyer.setParentIsole(parentIsole);
	}

	// -------------------------------------------------------------------------
	// Calcul principal
	// -------------------------------------------------------------------------

	/**
	 * Lance le calcul complet de l'impôt sur le revenu net. Les résultats sont
	 * ensuite accessibles via les getters. Retourne void pour être conforme à
	 * ICalculateurImpot.
	 *
	 * @throws IllegalArgumentException si la situation familiale est nulle (ERR-01)
	 */
	public void calculImpotSurRevenuNet() {
		if (foyer.getSituationFamiliale() == null) {
			throw new IllegalArgumentException("La situation familiale doit être renseignée.");
		}

		// Étape 1 — abattement
		calculateurAbattement.calculer(foyer);

		// Étape 2 — quotient familial
		calculateurQuotient.calculer(foyer);

		// Étape 3 — barème progressif (deux calculs nécessaires pour le plafonnement)
		double impotDeclarants = calculateurBareme.calculer(calculateurAbattement.getRevenuFiscalReference(),
				calculateurQuotient.getNbPartsDeclarants());

		double impotFoyer = calculateurBareme.calculer(calculateurAbattement.getRevenuFiscalReference(),
				calculateurQuotient.getNbPartsFoyerFiscal());

		// Étape 4 — plafonnement du quotient familial
		calculateurPlafonnement.calculer(impotDeclarants, impotFoyer, calculateurQuotient.getNbPartsDeclarants(),
				calculateurQuotient.getNbPartsFoyerFiscal());

		// Étape 5 — décote
		calculateurDecote.calculer(calculateurPlafonnement.getImpotAvantDecote(),
				calculateurQuotient.getNbPartsDeclarants());
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	public int getRevenuFiscalReference() {
		return calculateurAbattement.getRevenuFiscalReference();
	}

	public int getAbattement() {
		return calculateurAbattement.getAbattement();
	}

	public double getNbPartsFoyerFiscal() {
		return calculateurQuotient.getNbPartsFoyerFiscal();
	}

	public int getImpotAvantDecote() {
		return (int) Math.round(calculateurPlafonnement.getImpotAvantDecote());
	}

	public int getDecote() {
		return calculateurDecote.getDecote();
	}

	public int getImpotSurRevenuNet() {
		return calculateurDecote.getImpotNet();
	}
}