package com.kerware.simulateur;

import Calculateur.ICalculateurImpot;
import Calculateur.SituationFamiliale;
import simulateurreusine.AdaptateurCodeReusine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSimulateurImpot {

	public static final int CODE_HERITE = 1;
	public static final int CODE_REUSINE = 2;
	public static final int TYPE_CODE = CODE_REUSINE;

	// ICalculateur dans son propre package pour faire les bascules
	// AdaptateurCodeHerite / AdaptateurCodeReusine
	private static ICalculateurImpot calculateurImpot;

	@BeforeAll
	public static void setCalculateurImpot() {
		switch (TYPE_CODE) {
		case CODE_HERITE -> calculateurImpot = new AdaptateurCodeHerite();
		case CODE_REUSINE -> calculateurImpot = new AdaptateurCodeReusine();
		}
	}

	private void configurer(int revenu, SituationFamiliale sf, int nbEnf, int nbEnfH, boolean parentIsole) {
		calculateurImpot.setRevenusNet(revenu);
		calculateurImpot.setSituationFamiliale(sf);
		calculateurImpot.setNbEnfantsACharge(nbEnf);
		calculateurImpot.setNbEnfantsSituationHandicap(nbEnfH);
		calculateurImpot.setParentIsole(parentIsole);
		calculateurImpot.calculImpotSurRevenuNet();
	}

	// -------------------------------------------------------------------------
	// Tests nominaux
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Test nominal : célibataire 200 000 € sans enfant")
	void testCalculImpotAvecUnCelibataireSansEnfant() {
		configurer(200_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertEquals(60768, calculateurImpot.getImpotSurRevenuNet());
		assertEquals(1, calculateurImpot.getNbPartsFoyerFiscal());
	}

	// EXG-001 : situation familiale CELIBATAIRE → 1 part
	@Test
	@DisplayName("EXG-001 : Célibataire, 30 000 €, sans enfant")
	void testCelibataireSansEnfant() {
		configurer(30_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertEquals(1.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() > 0);
	}

	// EXG-002 : situation familiale MARIE → 2 parts
	@Test
	@DisplayName("EXG-002 : Marié, 65 000 €, sans enfant")
	void testMarieSansEnfant() {
		configurer(65_000, SituationFamiliale.MARIE, 0, 0, false);
		assertEquals(2.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// EXG-003 : 3 enfants → +1 part (2 premiers = 0.5 chacun, 3ème = 1 part)
	@Test
	@DisplayName("EXG-003 : Marié, 65 000 €, 3 enfants")
	void testMarieAvec3Enfants() {
		configurer(65_000, SituationFamiliale.MARIE, 3, 0, false);
		assertEquals(4.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// EXG-004 : enfant handicapé → +0.5 part supplémentaire
	@Test
	@DisplayName("EXG-004 : Marié, 65 000 €, 3 enfants dont 1 handicapé")
	void testMarieAvec3EnfantsDont1Handicape() {
		configurer(65_000, SituationFamiliale.MARIE, 3, 1, false);
		assertEquals(4.5, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// EXG-005 : parent isolé + 1 enfant → +0.5 part supplémentaire
	@Test
	@DisplayName("EXG-005 : Divorcé, 35 000 €, 1 enfant, parent isolé")
	void testDivorceParentIsole1Enfant() {
		configurer(35_000, SituationFamiliale.DIVORCE, 1, 0, true);
		assertEquals(2.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// EXG-006 : parent isolé + 2 enfants
	@Test
	@DisplayName("EXG-006 : Divorcé, 35 000 €, 2 enfants, parent isolé")
	void testDivorceParentIsole2Enfants() {
		configurer(35_000, SituationFamiliale.DIVORCE, 2, 0, true);
		assertEquals(2.5, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
	}

	// EXG-007 : parent isolé + 3 enfants
	@Test
	@DisplayName("EXG-007 : Divorcé, 50 000 €, 3 enfants, parent isolé")
	void testDivorceParentIsole3Enfants() {
		configurer(50_000, SituationFamiliale.DIVORCE, 3, 0, true);
		assertEquals(3.5, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
	}

	// EXG-008 : parent isolé + 3 enfants dont 1 handicapé
	@Test
	@DisplayName("EXG-008 : Divorcé, 50 000 €, 3 enfants dont 1 handicapé, parent isolé")
	void testDivorceParentIsole3EnfantsDont1Handicape() {
		configurer(50_000, SituationFamiliale.DIVORCE, 3, 1, true);
		assertEquals(4.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
	}

	// EXG-009 : VEUF sans enfant → 1 part
	@Test
	@DisplayName("EXG-009 : Veuf, 40 000 €, sans enfant")
	void testVeufSansEnfant() {
		configurer(40_000, SituationFamiliale.VEUF, 0, 0, false);
		assertEquals(1.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
	}

	// EXG-010 : VEUF avec enfants → 1 part déclarant + parts enfants
	@Test
	@DisplayName("EXG-010 : Veuf, 40 000 €, 2 enfants")
	void testVeufAvec2Enfants() {
		configurer(40_000, SituationFamiliale.VEUF, 2, 0, false);
		assertEquals(2.0, calculateurImpot.getNbPartsFoyerFiscal(), 0.01);
	}

	// EXG-011 : abattement plancher 495 € si 10% < 495 €
	@Test
	@DisplayName("EXG-011 : Abattement minimum appliqué (revenu 2 000 €)")
	void testAbattementMinimum() {
		configurer(2_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertEquals(495, calculateurImpot.getAbattement());
	}

	// EXG-012 : abattement plafonné à 14 171 € si 10% > 14 171 €
	@Test
	@DisplayName("EXG-012 : Abattement maximum appliqué (revenu 200 000 €)")
	void testAbattementMaximum() {
		configurer(200_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertEquals(14_171, calculateurImpot.getAbattement());
	}

	// EXG-013 : revenu fiscal de référence = revenu net - abattement
	@Test
	@DisplayName("EXG-013 : Revenu fiscal de référence = revenu - abattement")
	void testRevenuFiscalReference() {
		configurer(50_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertEquals(50_000 - calculateurImpot.getAbattement(), calculateurImpot.getRevenuFiscalReference());
	}

	// EXG-014 : la décote ne peut pas augmenter l'impôt
	@Test
	@DisplayName("EXG-014 : Impôt avant décote >= impôt net")
	void testImpotAvantDecoteSuperieurOuEgal() {
		configurer(25_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertTrue(calculateurImpot.getImpotAvantDecote() >= calculateurImpot.getImpotSurRevenuNet());
	}

	// EXG-015 : décote appliquée si impôt < seuil (1 929 € pour célibataire)
	@Test
	@DisplayName("EXG-015 : Décote appliquée pour impôt faible (célibataire, 20 000 €)")
	void testDecoteCelibataire() {
		configurer(20_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertTrue(calculateurImpot.getDecote() > 0);
	}

	// EXG-016 : revenu nul → impôt >= 0 (pas d'impôt négatif)
	@Test
	@DisplayName("EXG-016 : Revenu nul → impôt >= 0")
	void testRevenuNul() {
		configurer(0, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// EXG-017 : le quotient conjugal (2 parts) réduit l'impôt vs célibataire
	@Test
	@DisplayName("EXG-017 : Marié paie moins qu'un célibataire à revenu égal")
	void testMariePaiesMoinsQueCelibataire() {
		configurer(80_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		int impotCelibataire = calculateurImpot.getImpotSurRevenuNet();
		configurer(80_000, SituationFamiliale.MARIE, 0, 0, false);
		int impotMarie = calculateurImpot.getImpotSurRevenuNet();
		assertTrue(impotMarie < impotCelibataire);
	}

	// EXG-018 : les parts enfants réduisent ou maintiennent l'impôt
	@Test
	@DisplayName("EXG-018 : Plus d'enfants → impôt réduit")
	void testEnfantsReduisentImpot() {
		configurer(60_000, SituationFamiliale.MARIE, 0, 0, false);
		int impotSansEnfant = calculateurImpot.getImpotSurRevenuNet();
		configurer(60_000, SituationFamiliale.MARIE, 2, 0, false);
		int impotAvecEnfants = calculateurImpot.getImpotSurRevenuNet();
		assertTrue(impotAvecEnfants <= impotSansEnfant);
	}

	// EXG-019 : l'impôt net est toujours >= 0 quelle que soit la configuration
	@Test
	@DisplayName("EXG-019 : L'impôt net est toujours >= 0")
	void testImpotToujoursPositif() {
		configurer(15_000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
		configurer(15_000, SituationFamiliale.MARIE, 3, 2, true);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// -------------------------------------------------------------------------
	// Tests négatifs (cas d'erreur)
	// -------------------------------------------------------------------------

	// ERR-01 : situation familiale null → exception obligatoire
	@Test
	@DisplayName("ERR-01 : Situation familiale nulle → exception")
	void testSituationFamilialeNulle() {
		assertThrows(Exception.class, () -> configurer(30_000, null, 0, 0, false));
	}

	// ERR-02 : nbEnfants négatif → pas d'exception, impôt >= 0
	@Test
	@DisplayName("ERR-02 : Nombre d'enfants négatif → impôt >= 0")
	void testNbEnfantNegatif() {
		configurer(30_000, SituationFamiliale.MARIE, -1, 0, false);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// ERR-03 : nbEnfantsHandicapes > nbEnfants → pas d'exception, impôt >= 0
	@Test
	@DisplayName("ERR-03 : Enfants handicapés > enfants à charge → impôt >= 0")
	void testNbEnfantsHandicapesSuperieurEnfantsACharge() {
		configurer(30_000, SituationFamiliale.MARIE, 1, 2, false);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}

	// ERR-04 : revenu négatif → pas d'exception, impôt >= 0
	@Test
	@DisplayName("ERR-04 : Revenu négatif → impôt >= 0")
	void testRevenuNegatif() {
		configurer(-1000, SituationFamiliale.CELIBATAIRE, 0, 0, false);
		assertTrue(calculateurImpot.getImpotSurRevenuNet() >= 0);
	}
}