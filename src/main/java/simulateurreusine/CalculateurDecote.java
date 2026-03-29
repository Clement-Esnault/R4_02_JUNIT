package simulateurreusine;

/**
 * Étape 5 — Calcule la décote et l'impôt net final.
 *
 * La décote est appliquée lorsque l'impôt avant décote est inférieur au seuil
 * propre au type de foyer (seul ou couple).
 */
public class CalculateurDecote {

	private static final double SEUIL_DECOTE_SEUL = 1929.0;
	private static final double SEUIL_DECOTE_COUPLE = 3191.0;
	private static final double DECOTE_MAX_SEUL = 873.0;
	private static final double DECOTE_MAX_COUPLE = 1444.0;
	private static final double TAUX_DECOTE = 0.4525;

	// Résultats
	private double decote;
	private double impotNet;

	/**
	 * Calcule la décote et l'impôt net.
	 *
	 * @param impotAvantDecote  impôt issu du plafonnement du quotient familial
	 * @param nbPartsDeclarants 1.0 pour un foyer seul, 2.0 pour un couple
	 */
	public void calculer(double impotAvantDecote, double nbPartsDeclarants) {
		boolean estCouple = (nbPartsDeclarants == 2.0);
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

	public int getDecote() {
		return (int) Math.round(decote);
	}

	public int getImpotNet() {
		return (int) Math.round(impotNet);
	}
}