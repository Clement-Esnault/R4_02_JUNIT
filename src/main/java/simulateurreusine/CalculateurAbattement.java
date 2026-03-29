package simulateurreusine;

/**
 * Étape 1 — Calcule l'abattement forfaitaire de 10 % et le revenu fiscal de
 * référence.
 */
public class CalculateurAbattement {

	private static final double TAUX_ABATTEMENT = 0.10;
	private static final int ABATTEMENT_MINIMUM = 495;
	private static final int ABATTEMENT_MAXIMUM = 14171;

	// Résultats
	private int abattement;
	private int revenuFiscalReference;

	/**
	 * Calcule l'abattement à partir du foyer fiscal.
	 *
	 * @param foyer le foyer fiscal (revenu net)
	 */
	public void calculer(FoyerFiscal foyer) {
		int revenuEffectif = Math.max(foyer.getRevenuNet(), 0); // ERR-04 : revenu négatif → 0
		double abattementBrut = revenuEffectif * TAUX_ABATTEMENT;
		abattement = (int) Math.min(Math.max(abattementBrut, ABATTEMENT_MINIMUM), ABATTEMENT_MAXIMUM);
		revenuFiscalReference = Math.max(revenuEffectif - abattement, 0);
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	public int getAbattement() {
		return abattement;
	}

	public int getRevenuFiscalReference() {
		return revenuFiscalReference;
	}
}