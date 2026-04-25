package com.kerware.simulateur;

import Calculateur.ICalculateurImpot;
import Calculateur.SituationFamiliale;
import simulateurreusine.AdaptateurCodeReusine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite de tests fonctionnels du simulateur d'impôt sur le revenu 2024.
 *
 * <p>
 * Les tests couvrent les exigences EXG-001 à EXG-020 (cas nominaux) et ERR-01
 * à ERR-04 (cas d'erreur / robustesse).
 * </p>
 *
 * <p>
 * Le basculement entre code hérité et code réusiné s'effectue uniquement via
 * la constante {@code TYPE_CODE}.
 * </p>
 */
class TestSimulateurImpot {

    // -------------------------------------------------------------------------
    // Sélection de l'implémentation testée
    // -------------------------------------------------------------------------

    /** Identifiant du code hérité. */
    public static final int CODE_HERITE = 1;

    /** Identifiant du code réusiné. */
    public static final int CODE_REUSINE = 2;

    /** Code actuellement testé — modifier pour basculer d'implémentation. */
    public static final int TYPE_CODE = CODE_HERITE;

    private ICalculateurImpot calculateurImpot;

    @BeforeEach
    @DisplayName("Sélection de l'implémentation testée")
    void setCalculateurImpot() {
        if (TYPE_CODE == CODE_REUSINE) {
            calculateurImpot = new AdaptateurCodeReusine();
        } else if (TYPE_CODE == CODE_HERITE) {
            calculateurImpot = new AdaptateurCodeHerite();
        }
    }

    // -------------------------------------------------------------------------
    // Revenus utilisés dans les tests (en euros)
    // -------------------------------------------------------------------------

    /** Revenu bas pour les tests de seuil et d'abattement minimum. */
    private static final int REVENU_TRES_BAS = 2000;

    /** Revenu sous le seuil d'imposition. */
    private static final int REVENU_NON_IMPOSABLE = 10000;

    /** Revenu bas pour décote maximale. */
    private static final int REVENU_BAS_DECOTE = 20000;

    /** Revenu faible pour tests de décote partielle. */
    private static final int REVENU_FAIBLE = 25000;

    /** Revenu modeste pour foyer isolé. */
    private static final int REVENU_MODESTE = 30000;

    /** Revenu moyen pour foyer isolé avec enfants. */
    private static final int REVENU_MOYEN_ISOLE = 35000;

    /** Revenu moyen pour tests de parts et décote couple. */
    private static final int REVENU_MOYEN = 40000;

    /** Revenu intermédiaire pour tests de quotient familial. */
    private static final int REVENU_INTERMEDIAIRE = 50000;

    /** Revenu confortable pour tests mariés avec enfants. */
    private static final int REVENU_CONFORTABLE = 60000;

    /** Revenu élevé pour comparaison célibataire / marié. */
    private static final int REVENU_ELEVE = 65000;

    /** Revenu haut pour tests de quotient conjugal. */
    private static final int REVENU_HAUT = 80000;

    /** Revenu très élevé pour tests tranche maximale. */
    private static final int REVENU_TRES_ELEVE = 200000;

    /** Revenu négatif pour test de robustesse ERR-04. */
    private static final int REVENU_NEGATIF = -1000;

    // -------------------------------------------------------------------------
    // Résultats attendus (en euros)
    // -------------------------------------------------------------------------

    /** Impôt attendu : célibataire 30 000 €. */
    private static final int IMPOT_CELIB_30K = 1637;

    /** Impôt attendu : marié sans enfant 65 000 €. */
    private static final int IMPOT_MARIE_65K = 4122;

    /** Impôt attendu : marié 3 enfants 65 000 €. */
    private static final int IMPOT_MARIE_3ENF = 685;

    /** Impôt attendu : divorcé isolé 1 enfant 35 000 €. */
    private static final int IMPOT_DIVORCE_ISO_1ENF = 550;

    /** Impôt attendu : divorcé isolé 3 enfants 50 000 €. */
    private static final int IMPOT_DIVORCE_ISO_3ENF = 1;

    /** Impôt attendu : veuf sans enfant 40 000 €. */
    private static final int IMPOT_VEUF_0ENF = 4086;

    /** Impôt attendu : veuf 2 enfants 40 000 € (3.0 parts → impôt nul). */
    private static final int IMPOT_VEUF_2ENF = 0;

    /** Revenu fiscal de référence attendu pour 50 000 € net. */
    private static final int RFR_50K = 45000;

    /** Décote attendue pour célibataire à 20 000 €. */
    private static final int DECOTE_CELIB_20K = 539;

    /** Impôt attendu après décote : célibataire 20 000 €. */
    private static final int IMPOT_CELIB_20K_APRES_DECOTE = 199;

    /** Impôt attendu : célibataire 200 000 € (tranche max). */
    private static final int IMPOT_CELIB_200K = 60768;

    /** Impôt attendu : célibataire 80 000 €. */
    private static final int IMPOT_CELIB_80K = 14886;

    /** Impôt attendu : marié 80 000 € (quotient conjugal). */
    private static final int IMPOT_MARIE_80K = 8172;

    /** Impôt attendu : marié 60 000 € sans enfant. */
    private static final int IMPOT_MARIE_60K_SANS_ENF = 3455;

    /** Impôt attendu : marié 60 000 € avec 2 enfants. */
    private static final int IMPOT_MARIE_60K_2ENF = 1770;

    /** Seuil de décote couple (EXG_IMPOT_06). */
    private static final int SEUIL_DECOTE_COUPLE = 3191;

    // -------------------------------------------------------------------------
    // Méthode utilitaire
    // -------------------------------------------------------------------------

    /**
     * Configure le calculateur et déclenche le calcul.
     *
     * @param pRevenu      revenu net en euros
     * @param pSf          situation familiale
     * @param pNbEnf       nombre d'enfants à charge
     * @param pNbEnfH      nombre d'enfants handicapés
     * @param pParentIsole statut parent isolé
     */
    private void configurer(
            final int pRevenu, final SituationFamiliale pSf,
            final int pNbEnf, final int pNbEnfH, final boolean pParentIsole) {
        calculateurImpot.setRevenusNet(pRevenu);
        calculateurImpot.setSituationFamiliale(pSf);
        calculateurImpot.setNbEnfantsACharge(pNbEnf);
        calculateurImpot.setNbEnfantsSituationHandicap(pNbEnfH);
        calculateurImpot.setParentIsole(pParentIsole);
        calculateurImpot.calculImpotSurRevenuNet();
    }

    // -------------------------------------------------------------------------
    // Tests nominaux — impôt final EXG-001 à EXG-010
    // -------------------------------------------------------------------------

    /** EXG-001 : célibataire, 30 000 €, sans enfant. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-001 : célibataire, 30 000 €, sans enfant")
    void testCelibataireSansEnfant() {
        configurer(REVENU_MODESTE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(1.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_CELIB_30K, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-002 : marié, 65 000 €, sans enfant. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-002 : marié, 65 000 €, sans enfant")
    void testMarieSansEnfant() {
        configurer(REVENU_ELEVE, SituationFamiliale.MARIE, 0, 0, false);
        assertEquals(2.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_MARIE_65K, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-003 : marié, 65 000 €, 3 enfants. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-003 : marié, 65 000 €, 3 enfants")
    void testMarieAvec3Enfants() {
        configurer(REVENU_ELEVE, SituationFamiliale.MARIE, 3, 0, false);
        assertEquals(4.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_MARIE_3ENF, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-004 : marié, 65 000 €, 3 enfants dont 1 handicapé. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-004 : marié, 65 000 €, 3 enfants dont 1 handicapé")
    void testMarieAvec3EnfantsDont1Handicape() {
        configurer(REVENU_ELEVE, SituationFamiliale.MARIE, 3, 1, false);
        assertEquals(4.5, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(0, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-005 : divorcé, 35 000 €, 1 enfant, parent isolé. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-005 : divorcé, 35 000 €, 1 enfant, parent isolé")
    void testDivorceParentIsole1Enfant() {
        configurer(REVENU_MOYEN_ISOLE, SituationFamiliale.DIVORCE, 1, 0, true);
        assertEquals(2.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_DIVORCE_ISO_1ENF, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-006 : divorcé, 35 000 €, 2 enfants, parent isolé. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-006 : divorcé, 35 000 €, 2 enfants, parent isolé")
    void testDivorceParentIsole2Enfants() {
        configurer(REVENU_MOYEN_ISOLE, SituationFamiliale.DIVORCE, 2, 0, true);
        assertEquals(2.5, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(0, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-007 : divorcé, 50 000 €, 3 enfants, parent isolé. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-007 : divorcé, 50 000 €, 3 enfants, parent isolé")
    void testDivorceParentIsole3Enfants() {
        configurer(REVENU_INTERMEDIAIRE, SituationFamiliale.DIVORCE, 3, 0, true);
        assertEquals(3.5, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_DIVORCE_ISO_3ENF, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-008 : divorcé, 50 000 €, 3 enfants dont 1 handicapé, parent isolé. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-008 : divorcé, 50 000 €, 3 enfants dont 1 handicapé, parent isolé")
    void testDivorceParentIsole3EnfantsDont1Handicape() {
        configurer(REVENU_INTERMEDIAIRE, SituationFamiliale.DIVORCE, 3, 1, true);
        assertEquals(4.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(0, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-009 : veuf, 40 000 €, sans enfant. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-009 : veuf, 40 000 €, sans enfant")
    void testVeufSansEnfant() {
        configurer(REVENU_MOYEN, SituationFamiliale.VEUF, 0, 0, false);
        assertEquals(1.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_VEUF_0ENF, calculateurImpot.getImpotSurRevenuNet());
    }

    /** EXG-010 : veuf, 40 000 €, 2 enfants. */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-010 : veuf, 40 000 €, 2 enfants")
    void testVeufAvec2Enfants() {
        configurer(REVENU_MOYEN, SituationFamiliale.VEUF, 2, 0, false);
        assertEquals(3.0, calculateurImpot.getNbPartsFoyerFiscal());
        assertEquals(IMPOT_VEUF_2ENF, calculateurImpot.getImpotSurRevenuNet());
    }

    // -------------------------------------------------------------------------
    // Tests paramétrés — abattement EXG-011/012 (EXG_IMPOT_02)
    // -------------------------------------------------------------------------

    /**
     * EXG-011/012 : abattement de 10 % avec plancher à 495 € et plafond à 14 171 €.
     * Couvre EXG_IMPOT_02.
     */
    @ParameterizedTest(name = "revenu={0} € → abattement attendu={1} €")
    @Tag("Nominal")
    @DisplayName("EXG-011/012 : abattement (plancher, normal, plafond)")
    @CsvSource({
        "2000,   495",    // plancher : 10% = 200 < 495 → 495
        "10000, 1000",    // cas normal : 10% = 1000
        "50000, 5000",    // cas normal : 10% = 5000
        "200000, 14171"   // plafond : 10% = 20000 > 14171 → 14171
    })
    void testAbattement(final int revenu, final int abattementAttendu) {
        configurer(revenu, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(abattementAttendu, calculateurImpot.getAbattement());
    }

    // -------------------------------------------------------------------------
    // Tests paramétrés — nombre de parts EXG_IMPOT_03
    // -------------------------------------------------------------------------

    /**
     * EXG-parts : vérification du nombre de parts pour toutes les configurations
     * de foyer. Couvre EXG_IMPOT_03.
     */
    @ParameterizedTest(name = "{0} {1}enf {2}h isolé={3} → {4} parts")
    @Tag("Nominal")
    @DisplayName("EXG-parts : quotient familial (toutes configurations)")
    @CsvSource({
        "CELIBATAIRE, 0, 0, false, 1.0",   // célibataire seul
        "MARIE,       0, 0, false, 2.0",   // couple sans enfant
        "MARIE,       1, 0, false, 2.5",   // couple 1 enfant
        "MARIE,       2, 0, false, 3.0",   // couple 2 enfants
        "MARIE,       3, 0, false, 4.0",   // couple 3 enfants
        "MARIE,       3, 1, false, 4.5",   // couple 3 enfants dont 1 handicapé
        "DIVORCE,     1, 0, true,  2.0",   // isolé 1 enfant
        "DIVORCE,     2, 0, true,  2.5",   // isolé 2 enfants
        "DIVORCE,     3, 0, true,  3.5",   // isolé 3 enfants
        "VEUF,        2, 0, false, 3.0"    // veuf 2 enfants : 1 + 1 (conjoint) + 1 (2×0.5)
    })
    void testNbParts(
            final SituationFamiliale sf, final int nbEnf, final int nbH,
            final boolean isole, final double partsAttendues) {
        configurer(REVENU_MOYEN, sf, nbEnf, nbH, isole);
        assertEquals(partsAttendues, calculateurImpot.getNbPartsFoyerFiscal());
    }

    // -------------------------------------------------------------------------
    // Tests nominaux — revenu fiscal, décote, quotient EXG-013 à EXG-020
    // -------------------------------------------------------------------------

    /**
     * EXG-013 : revenu fiscal de référence = 45 000 € pour revenu net de 50 000 €.
     * Couvre EXG_IMPOT_02.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-013 : revenu fiscal de référence")
    void testRevenuFiscalReference() {
        configurer(REVENU_INTERMEDIAIRE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(RFR_50K, calculateurImpot.getRevenuFiscalReference());
    }

    /**
     * EXG-014 : l'impôt avant décote est toujours supérieur ou égal à l'impôt net.
     * Couvre EXG_IMPOT_06.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-014 : décote non négative")
    void testImpotAvantDecoteSuperieurOuEgal() {
        configurer(REVENU_FAIBLE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertTrue(
            calculateurImpot.getImpotAvantDecote() >= calculateurImpot.getImpotSurRevenuNet()
        );
    }

    /**
     * EXG-015 : décote et impôt après décote pour célibataire à 20 000 €.
     * Couvre EXG_IMPOT_06.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-015 : application de la décote (célibataire)")
    void testDecoteCelibataire() {
        configurer(REVENU_BAS_DECOTE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(DECOTE_CELIB_20K, calculateurImpot.getDecote());
        assertEquals(IMPOT_CELIB_20K_APRES_DECOTE, calculateurImpot.getImpotSurRevenuNet());
    }

    /**
     * EXG-016 : la décote couple s'applique bien quand l'impôt est sous le seuil
     * de 3 191 €. Couvre EXG_IMPOT_06.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-016 : application de la décote (couple marié 40 000 €)")
    void testDecoteCouple() {
        // À 40 000 €, le couple a un impôt avant décote positif mais sous 3 191 €
        configurer(REVENU_MOYEN, SituationFamiliale.MARIE, 0, 0, false);
        assertTrue(calculateurImpot.getImpotAvantDecote() < SEUIL_DECOTE_COUPLE);
        assertTrue(calculateurImpot.getDecote() > 0);
        assertTrue(
            calculateurImpot.getImpotSurRevenuNet() < calculateurImpot.getImpotAvantDecote()
        );
    }

    /**
     * EXG-017 : un marié paie moins d'impôt qu'un célibataire à revenus égaux.
     * Couvre EXG_IMPOT_04 (quotient conjugal).
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-017 : quotient conjugal (marié vs célibataire)")
    void testMariePaiesMoinsQueCelibataire() {
        configurer(REVENU_HAUT, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        final int impotCelib = calculateurImpot.getImpotSurRevenuNet();
        assertEquals(IMPOT_CELIB_80K, impotCelib);

        configurer(REVENU_HAUT, SituationFamiliale.MARIE, 0, 0, false);
        final int impotMarie = calculateurImpot.getImpotSurRevenuNet();
        assertEquals(IMPOT_MARIE_80K, impotMarie);

        assertTrue(impotMarie < impotCelib);
    }

    /**
     * EXG-018 : avoir des enfants réduit l'impôt d'un couple.
     * Couvre EXG_IMPOT_05 (plafonnement quotient familial).
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-018 : impact des parts enfants")
    void testEnfantsReduisentImpot() {
        configurer(REVENU_CONFORTABLE, SituationFamiliale.MARIE, 0, 0, false);
        assertEquals(IMPOT_MARIE_60K_SANS_ENF, calculateurImpot.getImpotSurRevenuNet());

        configurer(REVENU_CONFORTABLE, SituationFamiliale.MARIE, 2, 0, false);
        assertEquals(IMPOT_MARIE_60K_2ENF, calculateurImpot.getImpotSurRevenuNet());

        assertTrue(IMPOT_MARIE_60K_2ENF < IMPOT_MARIE_60K_SANS_ENF);
    }

    /**
     * EXG-019 : l'impôt est toujours nul ou positif sous le seuil d'imposition.
     * Couvre EXG_IMPOT_04.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-019 : impôt toujours positif ou nul")
    void testImpotToujoursPositif() {
        configurer(REVENU_NON_IMPOSABLE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(0, calculateurImpot.getImpotSurRevenuNet());
    }

    /**
     * EXG-020 : haut revenu — tranche marginale à 45 %.
     * Couvre EXG_IMPOT_04.
     */
    @Test
    @Tag("Nominal")
    @DisplayName("EXG-020 : test nominal haut revenu (tranche 45 %)")
    void testHautRevenu() {
        configurer(REVENU_TRES_ELEVE, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(IMPOT_CELIB_200K, calculateurImpot.getImpotSurRevenuNet());
    }

    // -------------------------------------------------------------------------
    // Tests négatifs ERR-01 à ERR-04
    // -------------------------------------------------------------------------

    /**
     * ERR-01 : situation familiale nulle doit lever une exception.
     * Couvre EXG_IMPOT_03 (précondition).
     */
    @Test
    @Tag("Negative")
    @DisplayName("ERR-01 : situation familiale nulle → exception")
    void testSituationFamilialeNulle() {
        assertThrows(Exception.class, () -> configurer(REVENU_MODESTE, null, 0, 0, false));
    }

    /**
     * ERR-02 : nbEnfants négatif → l'impôt reste positif ou nul.
     * Le comportement exact dépend de l'implémentation (non spécifié).
     * Couvre EXG_IMPOT_03 (robustesse).
     */
    @Test
    @Tag("Negative")
    @DisplayName("ERR-02 : nbEnfants négatif → impôt >= 0")
    void testNbEnfantsNegatif() {
        configurer(REVENU_MODESTE, SituationFamiliale.MARIE, -1, 0, false);
        assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
    }

    /**
     * ERR-03 : nbEnfantsHandicapés > nbEnfants → plafonné au nombre d'enfants réels.
     * Couvre EXG_IMPOT_03 (robustesse).
     */
    @Test
    @Tag("Negative")
    @DisplayName("ERR-03 : enfants handicapés > enfants → plafonné à nbEnfants")
    void testNbEnfantsHandicapesSuperieurEnfantsACharge() {
        // 1 enfant handicapé sur 1 enfant = même résultat que nbHandicapés=1, nbEnfants=1
        configurer(REVENU_MODESTE, SituationFamiliale.MARIE, 1, 1, false);
        final int impotReference = calculateurImpot.getImpotSurRevenuNet();

        // 2 handicapés déclarés pour 1 enfant → doit être plafonné à 1
        configurer(REVENU_MODESTE, SituationFamiliale.MARIE, 1, 2, false);
        assertEquals(impotReference, calculateurImpot.getImpotSurRevenuNet());
    }

    /**
     * ERR-04 : revenu négatif → normalisé à 0, même résultat qu'avec revenu = 0.
     * Couvre EXG_IMPOT_02 (robustesse).
     */
    @Test
    @Tag("Negative")
    @DisplayName("ERR-04 : revenu négatif → traité comme revenu 0")
    void testRevenuNegatif() {
        configurer(0, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        final int impotRevenuZero = calculateurImpot.getImpotSurRevenuNet();

        configurer(REVENU_NEGATIF, SituationFamiliale.CELIBATAIRE, 0, 0, false);
        assertEquals(impotRevenuZero, calculateurImpot.getImpotSurRevenuNet());
    }
}