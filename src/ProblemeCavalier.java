import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class ProblemeCavalier {
	
	private int size;
	private int totalPosition;
    private  Model model = new Model("ProblemeCavalier");
    private  int nbDeplacementCavalier = 8;
    private int totalCavalier = 1;
	private boolean foundSolution = false;
	
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
	
	/**
	 * Permet de lancer la domination des cavaliers
	 */
	public void dominationCavalier() {
		
        ArrayList<Constraint> constraintCavalier = new ArrayList<Constraint>();
        ArrayList<Constraint> globalConstraint = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintCavalier = new ArrayList<Constraint>();
		// Au départ on essaye avec un seul cavalier
		while (!foundSolution) {
			model = new Model("ProblemeCavalier");
			
			List<Cavalier> allCavaliers = new ArrayList<Cavalier>();
			
			for (int i=0; i<totalCavalier; ++i){
				allCavaliers.add(new Cavalier(model.intVar(0, size-1), model.intVar(0, size-1)));
			}
			
	        for (int k=0; k<size; ++k) {
	        	for (int l=0; l<size; ++l) {
	        		for (int m=0; m<totalCavalier; ++m) {
	        			Cavalier currentCavalier = allCavaliers.get(m);
	    				for (int index = 0; index < nbDeplacementCavalier+1; ++index) {
	    					Constraint a = model.and(model.arithm(currentCavalier.getI(), "=", currentCavalier.getDeplacementI()[index]+k), model.arithm(currentCavalier.getJ(), "=", currentCavalier.getDeplacementJ()[index]+l));
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
	        	Cavalier currentCavalier = allCavaliers.get(i);
	        	for (int j=i+1; j<totalCavalier; ++j) {
	        		Cavalier nextCavalier = allCavaliers.get(j);
	        		model.or(model.arithm(currentCavalier.getI(), "!=", nextCavalier.getI()), model.arithm(currentCavalier.getJ(), "!=", nextCavalier.getJ())).post();
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
				printMatrix(allCavaliers);
			}
		}
	}

	/**
	 * Affichage de la matrice
	 * @param allCavaliers
	 */
    public void printMatrix(List<Cavalier> allCavaliers) {
    	String[][] matrix = new String[size][size];
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			matrix[i][j] = "--";
    		}
    	}

    	for (int i=0; i<allCavaliers.size();++i) {
    		Cavalier currentCavalier = allCavaliers.get(i);
    		matrix[currentCavalier.getI().getValue()][currentCavalier.getJ().getValue()] = currentCavalier.getLetter()+(i+1);
    	}
    	
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println("");
    	}
    }
}
