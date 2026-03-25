package com.kerware.simulateur;

import Calculateur.ICalculateurImpot;
import Calculateur.SituationFamiliale;

public class AdaptateurCodeHerite implements ICalculateurImpot {

	Simulateur simulateurHerite = new Simulateur();

	@Override
	public void setRevenusNet(int rn) {
		simulateurHerite.setRevenusNet(rn);
	}

	@Override
	public void setSituationFamiliale(SituationFamiliale sf) {
		simulateurHerite.setSituationFamiliale(sf);

	}

	@Override
	public void setNbEnfantsACharge(int nbe) {
		simulateurHerite.setNbEnfantsACharge(nbe);
	}

	@Override
	public void setNbEnfantsSituationHandicap(int nbesh) {
		simulateurHerite.setNbEnfantsSituationHandicap(nbesh);
	}

	@Override
	public void setParentIsole(boolean pi) {
		simulateurHerite.setParentIsole(pi);
	}

	@Override
	public void calculImpotSurRevenuNet() {
		simulateurHerite.calculImpotSurRevenuNet();
	}

	@Override
	public int getRevenuFiscalReference() {
		return simulateurHerite.getRevenuFiscalReference();
	}

	@Override
	public int getAbattement() {
		return simulateurHerite.getAbattement();
	}

	@Override
	public double getNbPartsFoyerFiscal() {
		return simulateurHerite.getNbPartsFoyerFiscal();
	}

	@Override
	public int getImpotAvantDecote() {
		return simulateurHerite.getImpotAvantDecote();
	}

	@Override
	public int getDecote() {
		return simulateurHerite.getDecote();
	}

	// Impot net à payer par le contribuable
	@Override
	public int getImpotSurRevenuNet() {
		return simulateurHerite.getImpotSurRevenuNet();
	}
}
