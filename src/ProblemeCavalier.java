import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class ProblemeCavalier {
	
	int size;
	int totalPosition;
    public String CAVALIER = "C";
    public Model model = new Model("ProblemeCavalier");
    public int nbDeplacementCavalier = 8;
    public int totalCavalier = 1;
	
	ProblemeCavalier(int size) {
		this.size = size;
		totalPosition = size*size;
	}
	
	public void start() {
        System.out.println("--- Début de la recherche pour une matrice de taille : " + size + "*" + size + " ---");
		dominationCavalier();
		System.out.println("");
        System.out.println("---------------------------------------------");
        System.out.println("Solution : " + totalCavalier + " cavalier(s) nécessaire(s).");
        System.out.println("---------------------------------------------");
	}
	
	public void dominationCavalier() {
		int[] deplacementPossibleCavalierI = new int[nbDeplacementCavalier+1]; // +1 car il reste sur place
		int[] deplacementPossibleCavalierJ = new int[nbDeplacementCavalier+1];
		
		deplacementPossibleCavalierI[0] = -2;
		deplacementPossibleCavalierI[1] = -2;
		deplacementPossibleCavalierI[2] = -1;
		deplacementPossibleCavalierI[3] = 1;
		deplacementPossibleCavalierI[4] = 2;
		deplacementPossibleCavalierI[5] = 2;
		deplacementPossibleCavalierI[6] = 1;
		deplacementPossibleCavalierI[7] = -1;
		deplacementPossibleCavalierI[8] = 0;
		deplacementPossibleCavalierJ[0] = -1;
		deplacementPossibleCavalierJ[1] = 1;
		deplacementPossibleCavalierJ[2] = 2;
		deplacementPossibleCavalierJ[3] = 2;
		deplacementPossibleCavalierJ[4] = 1;
		deplacementPossibleCavalierJ[5] = -1;
		deplacementPossibleCavalierJ[6] = -2;
		deplacementPossibleCavalierJ[7] = -2;
		deplacementPossibleCavalierJ[8] = 0;
		
		boolean foundSolution = false;
        ArrayList<Constraint> constraintCavalier = new ArrayList<Constraint>();
        ArrayList<Constraint> globalConstraint = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintCavalier = new ArrayList<Constraint>();
		// Au départ on essaye avec un seul cavalier
		while (!foundSolution) {
			model = new Model("ProblemeCavalier");
			IntVar[] cavalierI = new IntVar[totalCavalier];
	        IntVar[] cavalierJ = new IntVar[totalCavalier];
	        for (int j = 0; j < totalCavalier; j++) {
	        	cavalierI[j] = model.intVar(0, size-1);
	        	cavalierJ[j] = model.intVar(0, size-1);
	        }
	        
	        for (int k=0; k<size; ++k) {
	        	for (int l=0; l<size; ++l) {
	        		//CAVALIER
	        		for (int m=0; m<totalCavalier; ++m) {
	    				for (int index = 0; index < nbDeplacementCavalier+1; ++index) {
	    					Constraint a = model.and(model.arithm(cavalierI[m], "=", deplacementPossibleCavalierI[index]+k), model.arithm(cavalierJ[m], "=", deplacementPossibleCavalierJ[index]+l));
	    					constraintCavalier.add(a);
	    				}
	            		int next = 1;
	            		Constraint Z = constraintCavalier.get(0);
	            		while (next < constraintCavalier.size()) {
	                    	Z = model.or(Z,constraintCavalier.get(next));
	                    	next+=1;
	            		}
	            		allConstraintCavalier.add(Z);
	            		constraintCavalier.clear();
	        		}
	        		if (!allConstraintCavalier.isEmpty()) {
	            		int suivant = 1;
	            		Constraint W = allConstraintCavalier.get(0);
	            		while (suivant < allConstraintCavalier.size()) {
	                    	W = model.or(W,allConstraintCavalier.get(suivant));
	                    	suivant+=1;
	            		}
	            		globalConstraint.add(W); 	
	            		allConstraintCavalier.clear();
	        		}
	        	}
	        }
	        // Superposition de cavalier x cavalier
	        for (int i=0; i<totalCavalier; ++i) {
	        	for (int j=i+1; j<totalCavalier; ++j) {
	        		model.or(model.arithm(cavalierI[i], "!=", cavalierI[j]), model.arithm(cavalierJ[i], "!=", cavalierJ[j])).post();
	        	}
	        }
	        if (!globalConstraint.isEmpty()){
	    		int second = 1;
	    		Constraint X = globalConstraint.get(0);
	    		while (second < globalConstraint.size()) {
	            	X = model.and(X,globalConstraint.get(second));
	            	second+=1;
	    		}
	    		X.post();
	    		globalConstraint.clear();
	        }
			foundSolution = model.getSolver().solve();
			if (!foundSolution) {
				totalCavalier += 1;
			}
			else {
				printMatrix(cavalierI, cavalierJ);
			}
		}
	}
	
	
	/**
	 * Affichage de la matrice
	 * @param cav
	 * @param fou
	 * @param tour
	 */
    public void printMatrix(IntVar[] cavI, IntVar[] cavJ) {
    	String[][] matrix = new String[size][size];
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			matrix[i][j] = "--";
    		}
    	}

    	for (int i=0; i<cavI.length;++i) {
    		matrix[cavI[i].getValue()][cavJ[i].getValue()] = CAVALIER+(i+1);
    	}
    	
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println("");
    	}
    }
    
	
}
