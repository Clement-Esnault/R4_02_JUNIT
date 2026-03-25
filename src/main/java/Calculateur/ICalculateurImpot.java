package Calculateur;



public interface ICalculateurImpot {

	// Setter
	public void setRevenusNet(int rn);

	public void setSituationFamiliale(SituationFamiliale sf);

	public void setNbEnfantsACharge(int nbe);

	public void setNbEnfantsSituationHandicap(int nbesh);

	public void setParentIsole(boolean pi);

	// Final
	public void calculImpotSurRevenuNet();

	// Getter
	public int getRevenuFiscalReference();

	public int getAbattement();

	public double getNbPartsFoyerFiscal();

	public int getImpotAvantDecote();

	public int getDecote();

	public int getImpotSurRevenuNet();

}
