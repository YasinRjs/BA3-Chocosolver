import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class ProblemeEchiquier {
	// 1. Create a Model
    public int size = 3;
    public int totalPosition = size*size;
    public int totalTour = 2;
    public int totalFou = 3;
    public int totalCavalier = 1;
    
    public String CAVALIER = "C";
    public String TOUR = "T";
    public String FOU = "F";
    
    public String INDEPENDANCE = "-i";
    public String DOMINATION = "-d";
    public String SIZE = "-n";
    public String argCAVALIER = "-c";
    public String argTOUR = "-t";
    public String argFOU = "-f";
    public Model model = new Model("ProblemeEchiquier");
    public String chosen = "notOK";
    public int nbDeplacementCavalier = 8;

    public ProblemeEchiquier(String[] args) {
    	int index;
    	try{
    		index = findInStringArray(args, DOMINATION);
    		if (index == 100) {
    			index = findInStringArray(args, INDEPENDANCE);
    		}
    		chosen = args[index];
    		index = findInStringArray(args, SIZE);
    		size = Integer.parseInt(args[index+1]);
    		totalPosition = size*size;
    		index = findInStringArray(args, argTOUR);
    		totalTour = Integer.parseInt(args[index+1]);
    		index = findInStringArray(args, argCAVALIER);
    		totalCavalier = Integer.parseInt(args[index+1]);
    		index = findInStringArray(args, argFOU);
    		totalFou = Integer.parseInt(args[index+1]);
    	}
    	catch (Exception e) {
    		System.out.println("Erreur dans les arguments. ");
    		System.exit(1);
    	}

    }
    
    public void start() {
        if (chosen.equals(INDEPENDANCE)) {
        	doIndependance();
        }
        else if (chosen.equals(DOMINATION)) {
        	doDomination();
        }
        else {
        	System.out.println("Veuillez spécifiez le type d'exécution ( -i ) pour INDEPENDANCE et ( -d ) pour DOMINATION.");
        }
    }
    
    public List<Piece> createAllPieces() {
    	List<Piece> allPieces = new ArrayList<Piece>();
        for (int i = 0; i < totalCavalier; i++) {
        	allPieces.add(new Cavalier(model.intVar(0, size-1), model.intVar(0, size-1)));
        }
        for (int i = 0; i < totalFou; i++) {
        	allPieces.add(new Fou(model.intVar(0, size-1), model.intVar(0, size-1), size));
        }
        for (int i = 0; i < totalTour; i++) {
        	allPieces.add(new Tour(model.intVar(0, size-1), model.intVar(0, size-1), size));
        }
        return allPieces;
    }
    
    public void doDomination() {
    	
    	List<Piece> allPieces = createAllPieces();

        ArrayList<Constraint> constraintPiece = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintPieces = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraint = new ArrayList<Constraint>();
        for (int k=0; k<size; ++k) {
        	for (int l=0; l<size; ++l) {
        		for (Piece currentPiece : allPieces) {
        			for (int index = 0; index < currentPiece.getDeplacementI().length; ++index) {
    					Constraint a = model.and(model.arithm(currentPiece.getI(), "=", currentPiece.getDeplacementI()[index]+k), model.arithm(currentPiece.getJ(), "=", currentPiece.getDeplacementJ()[index]+l));
    					constraintPiece.add(a);
        			}
            		int next = 1;
            		Constraint Z = constraintPiece.get(0);
            		while (next < constraintPiece.size()) {
                    	Z = model.or(Z,constraintPiece.get(next));
                    	next+=1;
            		}
            		allConstraintPieces.add(Z);
            		constraintPiece.clear();
        		}
        		if (!allConstraintPieces.isEmpty()) {
            		int suivant = 1;
            		Constraint W = allConstraintPieces.get(0);
            		while (suivant < allConstraintPieces.size()) {
                    	W = model.or(W,allConstraintPieces.get(suivant));
                    	suivant+=1;
            		}
            		allConstraint.add(W); 	
            		allConstraintPieces.clear();
        		}
        	}
        }
        for (int i=0; i<allPieces.size(); ++i) {
        	for (int j=i+1; j<allPieces.size(); ++j) {
        		Piece currentPiece = allPieces.get(i);
        		Piece nextPiece = allPieces.get(j);
        		model.or(model.arithm(currentPiece.getI() ,"!=", nextPiece.getI()), model.arithm(currentPiece.getJ(), "!=", nextPiece.getJ())).post();
        	}
        }
        if (!allConstraint.isEmpty()){
        	System.out.println("ici");
    		int second = 1;
    		Constraint X = allConstraint.get(0);
    		while (second < allConstraint.size()) {
            	X = model.and(X,allConstraint.get(second));
            	second+=1;
    		}
    		X.post();
        }
        
        checkSolutionAndPrint(allPieces);
    }
    
    public void doIndependance() {

    	List<Piece> allPieces = createAllPieces();
    	
    	for (int i=0; i<allPieces.size(); ++i) {
    		for (int j=0; j<allPieces.size(); ++j) {
    			if (i!=j){
        			Piece currentPiece = allPieces.get(i);
        			Piece nextPiece = allPieces.get(j);
        			for (int index = 0; index < currentPiece.getDeplacementI().length; ++index) {
    					model.or(model.arithm(currentPiece.getI(), "-", nextPiece.getI(), "!=", currentPiece.getDeplacementI()[index]), model.arithm(currentPiece.getJ(), "-", nextPiece.getJ(), "!=", currentPiece.getDeplacementJ()[index])).post();
        			}
    			}
    		}
    	}
		System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			printMatrix(allPieces);
		}
		System.out.println("Stop");    	
    }
    
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
    				// Début d'affichage de la matrice
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
	
	/**
	 * Affichage de la matrice
	 * @param cav
	 * @param fou
	 * @param tour
	 */
    public void printMatrix(List<Piece> allPieces) {
    	String[][] matrix = new String[size][size];
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			matrix[i][j] = "--";
    		}
    	}

    	for (int i=0; i<allPieces.size(); ++i) {
    		Piece currentPiece = allPieces.get(i);
    		matrix[currentPiece.getI().getValue()][currentPiece.getJ().getValue()] = currentPiece.getLetter()+(i+1);
    	}
    	
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println("");
    	}
    }
    
    public void checkSolutionAndPrint(List<Piece> allPieces) {
        System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			printMatrix(allPieces);
		}
		System.out.println("Stop");
    }

    public int findInStringArray(String[] array, String value) {
    	boolean notFound = true;
    	int i=0;
    	while (notFound && i<array.length) {
    		if (array[i].equals(value)) {
    			notFound = false;
    		}
    		else {
    			++i;
    		}
    	}
    	if (notFound) {
    		i=100;
    	}
    	return i;
    }
    	
}