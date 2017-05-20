import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public class PieceQuelconque extends Piece {
	
	
	private String letter;

	
	private  int[] deplacementI = null;
	private  int[] deplacementJ = null;

	
	PieceQuelconque(IntVar valuesI, IntVar valuesJ, String letter, int diagonale, int droite, boolean L) {
		this.positionI = valuesI;
		this.positionJ = valuesJ;

		this.letter = letter;
		setDeplacement(diagonale,droite,L);
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
