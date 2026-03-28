package simulateurreusine;

/**
 * Étape 4 — Plafonnement du quotient familial.
 *
 * L'avantage fiscal procuré par les parts supplémentaires (enfants, majorations)
 * est plafonné à {@code PLAFOND_DEMI_PART} euros par demi-part.
 */
public class CalculateurPlafonnement {

    private static final double PLAFOND_DEMI_PART = 1759.0;

    // Résultat
    private double impotAvantDecote;

    /**
     * Calcule l'impôt après plafonnement du quotient familial.
     *
     * @param impotDeclarants   impôt calculé sur la base des seules parts des déclarants
     * @param impotFoyer        impôt calculé sur la base du nombre total de parts du foyer
     * @param nbPartsDeclarants nombre de parts des déclarants (1 ou 2)
     * @param nbPartsFoyer      nombre total de parts du foyer fiscal
     */
    public void calculer(double impotDeclarants, double impotFoyer,
                         double nbPartsDeclarants, double nbPartsFoyer) {
        double demiPartsSup = nbPartsFoyer - nbPartsDeclarants;
        double plafond = (demiPartsSup / 0.5) * PLAFOND_DEMI_PART;
        double reduction = impotDeclarants - impotFoyer;
        impotAvantDecote = (reduction > plafond) ? impotDeclarants - plafond : impotFoyer;
    }

    // -------------------------------------------------------------------------
    // Getter
    // -------------------------------------------------------------------------

    public double getImpotAvantDecote() {
        return impotAvantDecote;
    }
}