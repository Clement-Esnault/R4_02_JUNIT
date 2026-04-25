package simulateurreusine;

/**
 * Applique le barème progressif de l'impôt sur le revenu pour un revenu fiscal
 * de référence et un nombre de parts donné.
 *
 * Cette classe est sans état : elle peut être réutilisée pour calculer
 * successivement l'impôt des déclarants seuls puis celui du foyer complet.
 */
public final class CalculateurBareme {

	private static final int[] LIMITES_TRANCHES = {
		0, 11_294, 28_797, 82_341, 177_106, Integer.MAX_VALUE
	};
	private static final double[] TAUX_TRANCHES = { 0.00, 0.11, 0.30, 0.41, 0.45 };

	/**
	 * Calcule l'impôt brut pour un nombre de parts donné.
	 *
	 * @param revenuFiscalReference revenu fiscal de référence (après abattement)
	 * @param nbParts               nombre de parts à appliquer
	 * @return montant de l'impôt brut arrondi à l'euro
	 */
	public double calculer(int revenuFiscalReference, double nbParts) {
		double revenuParPart = revenuFiscalReference / nbParts;
		double impot = 0.0;
		for (int i = 0; i < TAUX_TRANCHES.length; i++) {
			if (revenuParPart <= LIMITES_TRANCHES[i]) {
				break;
			}
			double limiteHaute = LIMITES_TRANCHES[i + 1];
			double base = Math.min(revenuParPart, limiteHaute) - LIMITES_TRANCHES[i];
			impot += base * TAUX_TRANCHES[i];
		}
		return Math.round(impot * nbParts);
	}
}