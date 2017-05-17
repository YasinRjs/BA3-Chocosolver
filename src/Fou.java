import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public class Fou extends Piece {
	
	private static final int droite = 0;
	private static final boolean L = false;
	private static int diagonale;
	private static final String letter = "F";
	
	private static int[] deplacementI = null;
	private static int[] deplacementJ = null;

	Fou(IntVar valuesI, IntVar valuesJ, int size) {
		this.positionI = valuesI;
		this.positionJ = valuesJ;
		if (deplacementI == null) {
			diagonale = size-1;
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
