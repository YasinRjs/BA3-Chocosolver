import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.variables.IntVar;

public abstract class Piece {
	
	protected IntVar positionI;
	protected IntVar positionJ;

	public IntVar getI() {
		return positionI;
	}
	public IntVar getJ() {
		return positionJ;
	}
	
	protected abstract String getLetter();
	
	protected abstract int[] getDeplacementI();
	protected abstract int[] getDeplacementJ();
	
    protected List<int[]> createDeplacement(int diagonale, int droit, boolean L) {
    	List<int[]> allDeplacement = new ArrayList<int[]>();
    	int totalDeplacementI = L ? 8 : 0; 
    	int totalDeplacementJ = L ? 8 : 0;
    	totalDeplacementI += diagonale*4 + droit*4 + 1; // +1 car on prend en compte le fait qu'il reste sur place
    	totalDeplacementJ += diagonale*4 + droit*4 + 1;	
    	int[] deplacementI = new int[totalDeplacementI];
    	int[] deplacementJ = new int[totalDeplacementJ];
    	
    	deplacementI[0] = 0;	// Ce qui empêche la superposition d'une pièce dans le problème d'indépendance
    	deplacementJ[0] = 0;	// et permet également de "marquer" la case où se trouve la pièce dans le problèm de domination
    	int current = 1;
    	if (L) {
    		deplacementI[current] = -2;
    		deplacementI[current+1] = -2;
    		deplacementI[current+2] = -1;
    		deplacementI[current+3] = 1;
    		deplacementI[current+4] = 2;
    		deplacementI[current+5] = 2;
    		deplacementI[current+6] = 1;
    		deplacementI[current+7] = -1;
    		deplacementJ[current] = -1;
    		deplacementJ[current+1] = 1;
    		deplacementJ[current+2] = 2;
    		deplacementJ[current+3] = 2;
    		deplacementJ[current+4] = 1;
    		deplacementJ[current+5] = -1;
    		deplacementJ[current+6] = -2;
    		deplacementJ[current+7] = -2;
    		current += 8;
    	}
    	for (int i = 1; i < diagonale+1; ++i) {
    		deplacementI[current] = -i;
    		deplacementJ[current] = -i;
    		deplacementI[current+1] = -i;
    		deplacementJ[current+1] = i;
    		deplacementI[current+2] = i;
    		deplacementJ[current+2] = -i;
    		deplacementI[current+3] = -i;
    		deplacementJ[current+3] = -i;
    		current += 4;
    	}
    	for (int i = 1; i < droit+1; ++i) {
    		deplacementI[current] = -i;
    		deplacementJ[current] = 0;
    		deplacementI[current+1] = 0;
    		deplacementJ[current+1] = i;
    		deplacementI[current+2] = i;
    		deplacementJ[current+2] = 0;
    		deplacementI[current+3] = 0;
    		deplacementJ[current+3] = -i;
    		current += 4;
    	}
    	allDeplacement.add(deplacementI);
    	allDeplacement.add(deplacementJ);
    	
    	return allDeplacement;
    }

}
