import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public class Cavalier extends Piece {
	
	private static final int diagonale = 0;
	private static final int droite = 0;
	private static final boolean L = true;
	private static final String letter = "C";

	private static int[] deplacementCavalierI = null;
	private static int[] deplacementCavalierJ = null;
	
	Cavalier(IntVar valuesI, IntVar valuesJ) {
		this.positionI = valuesI;
		this.positionJ = valuesJ;
		if (deplacementCavalierI == null) {
			setDeplacement(diagonale, droite, L);
		}
	}
	
	@Override
	public int[] getDeplacementI() {
		return deplacementCavalierI;
	}
	
	@Override
	public int[] getDeplacementJ() {
		return deplacementCavalierJ;
	}
	@Override
	public String getLetter() {
		return letter;
	}
	
	public void setDeplacement(int diagonale, int droite, boolean L) {
		List<int[]> allDeplacement = createDeplacement(diagonale, droite, L);
		
		deplacementCavalierI = allDeplacement.get(0);
		deplacementCavalierJ = allDeplacement.get(1);
		
	}
}
