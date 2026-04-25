package simulateurreusine;

import Calculateur.SituationFamiliale;

import Calculateur.ICalculateurImpot;

public final class AdaptateurCodeReusine implements ICalculateurImpot {

	private Simulateur simulateurReusine = new Simulateur();

	@Override
	public void setRevenusNet(int rn) {
		simulateurReusine.setRevenusNet(rn);
	}

	@Override
	public void setSituationFamiliale(SituationFamiliale sf) {
		simulateurReusine.setSituationFamiliale(sf);
	}

	@Override
	public void setNbEnfantsACharge(int nbe) {
		simulateurReusine.setNbEnfantsACharge(nbe);
	}

	@Override
	public void setNbEnfantsSituationHandicap(int nbesh) {
		simulateurReusine.setNbEnfantsSituationHandicap(nbesh);
	}

	@Override
	public void setParentIsole(boolean pi) {
		simulateurReusine.setParentIsole(pi);
	}

	@Override
	public void calculImpotSurRevenuNet() {
		simulateurReusine.calculImpotSurRevenuNet();
	}

	@Override
	public int getRevenuFiscalReference() {
		return simulateurReusine.getRevenuFiscalReference();
	}

	@Override
	public int getAbattement() {
		return simulateurReusine.getAbattement();
	}

	@Override
	public double getNbPartsFoyerFiscal() {
		return simulateurReusine.getNbPartsFoyerFiscal();
	}

	@Override
	public int getImpotAvantDecote() {
		return simulateurReusine.getImpotAvantDecote();
	}

	@Override
	public int getDecote() {
		return simulateurReusine.getDecote();
	}

	@Override
	public int getImpotSurRevenuNet() {
		return simulateurReusine.getImpotSurRevenuNet();
	}
}