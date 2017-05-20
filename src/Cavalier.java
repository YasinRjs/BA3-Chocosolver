import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public class Cavalier extends Piece {
	
	private static final int diagonale = 0;
	private static final int droite = 0;
	private static final boolean L = true;
	private static final String letter = "C";

	private static int[] deplacementI = null;
	private static int[] deplacementJ = null;
	
	Cavalier(){
		if (deplacementI == null) {
			setDeplacement(diagonale, droite, L);
		}
	}
	
	Cavalier(IntVar valuesI, IntVar valuesJ) {
		this.positionI = valuesI;
		this.positionJ = valuesJ;
		if (deplacementI == null) {
			setDeplacement(diagonale, droite, L);
		}
	}
	
	@Override
	public int[] getDeplacementI() {
		return deplacementI;
	}
	
	@Override
	public int[] getDeplacementJ() {
		return deplacementJ;
	}
	@Override
	public String getLetter() {
		return letter;
	}
	
	public void setDeplacement(int diagonale, int droite, boolean L) {
		List<int[]> allDeplacement = createDeplacement(diagonale, droite, L);
		
		deplacementI = allDeplacement.get(0);
		deplacementJ = allDeplacement.get(1);
		
	}
}
