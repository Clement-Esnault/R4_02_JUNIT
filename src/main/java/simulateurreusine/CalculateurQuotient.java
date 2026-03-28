package simulateurreusine;

import Calculateur.SituationFamiliale;

/**
 * Étape 2 — Calcule le nombre de parts du foyer fiscal
 * (déclarants + enfants + majorations).
 */
public class CalculateurQuotient {

    // Résultats
    private double nbPartsDeclarants;
    private double nbPartsFoyerFiscal;

    /**
     * Calcule le quotient familial à partir du foyer fiscal.
     *
     * @param foyer le foyer fiscal
     */
    public void calculer(FoyerFiscal foyer) {
        nbPartsDeclarants = partsDeclarants(foyer.getSituationFamiliale());
        nbPartsFoyerFiscal = nbPartsDeclarants
                + partsEnfants(foyer.getNbEnfants())
                + partsMajorationParentIsole(foyer.isParentIsole(), foyer.getNbEnfants())
                + partsEnfantsHandicapes(foyer.getNbEnfantsHandicapes(), foyer.getNbEnfants());
    }

    // -------------------------------------------------------------------------
    // Calculs internes
    // -------------------------------------------------------------------------

    /** 1 part pour célibataire/divorcé/veuf, 2 parts pour marié/pacsé. */
    private double partsDeclarants(SituationFamiliale sf) {
        return switch (sf) {
            case CELIBATAIRE, DIVORCE, VEUF -> 1.0;
            case MARIE, PACSE               -> 2.0;
        };
    }

    /** 0,5 part par enfant jusqu'au 2e, 1 part entière à partir du 3e. */
    private double partsEnfants(int nbEnfants) {
        int n = Math.max(nbEnfants, 0); // ERR-02 : valeur négative → 0
        return (n <= 2) ? n * 0.5 : 1.0 + (n - 2);
    }

    /** +0,5 part si parent isolé avec au moins 1 enfant. */
    private double partsMajorationParentIsole(boolean parentIsole, int nbEnfants) {
        return (parentIsole && Math.max(nbEnfants, 0) > 0) ? 0.5 : 0.0;
    }

    /** +0,5 part par enfant handicapé, plafonné au nombre d'enfants réels. */
    private double partsEnfantsHandicapes(int nbHandicapes, int nbEnfants) {
        int n = Math.min(Math.max(nbHandicapes, 0), Math.max(nbEnfants, 0)); // ERR-03
        return n * 0.5;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public double getNbPartsDeclarants() {
        return nbPartsDeclarants;
    }

    public double getNbPartsFoyerFiscal() {
        return nbPartsFoyerFiscal;
    }
}