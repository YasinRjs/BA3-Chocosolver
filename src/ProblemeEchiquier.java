import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

public class ProblemeEchiquier {
	// 1. Create a Model
	private int size = 3;
    private int totalTour = 2;
    private int totalFou = 3;
    private int totalCavalier = 1;
    
    private String INDEPENDANCE = "-i";
    private String DOMINATION = "-d";
    private String SIZE = "-n";
    private String argCAVALIER = "-c";
    private String argTOUR = "-t";
    private String argFOU = "-f";
    private String argCustom = "-custom";
    private boolean custom = false;
    private Model model = new Model("ProblemeEchiquier");
    private String chosen = "notOK";
    private ArrayList<Constraint> constraintPiece = new ArrayList<Constraint>();
    private ArrayList<Constraint> constraintDeplacements = new ArrayList<Constraint>();
    private ArrayList<Constraint> constraintPosition = new ArrayList<Constraint>();
    private ArrayList<Constraint> allConstraintPieces = new ArrayList<Constraint>();
    private ArrayList<Constraint> allConstraint = new ArrayList<Constraint>();
	List<Piece> allPieces = new ArrayList<Piece>();
    
	
	

	
    public ProblemeEchiquier() {
    	parseArguments();
    	start();
    }
    
    public void parseArguments() {
    	int index;
    	boolean finished = false;
    	while (!finished) {
    		System.out.println("Veuillez entrer vos arguments comme demandé dans le PDF:");
    		Scanner in = new Scanner(System.in);
    		String[] args = in.nextLine().split(" ");
	    	try{
	    		index = findInStringArray(args, DOMINATION);
	    		if (index == 100) {
	    			index = findInStringArray(args, INDEPENDANCE);
	    		}
	    		chosen = args[index];
	    		index = findInStringArray(args, SIZE);
	    		size = Integer.parseInt(args[index+1]);
	    		index = findInStringArray(args, argTOUR);
	    		totalTour = Integer.parseInt(args[index+1]);
	    		index = findInStringArray(args, argCAVALIER);
	    		totalCavalier = Integer.parseInt(args[index+1]);
	    		index = findInStringArray(args, argFOU);
	    		totalFou = Integer.parseInt(args[index+1]);
	    		index = findInStringArray(args, argCustom);
	    		if (index != 100) {
	    			custom = true;
	    		}
	    		finished = true;
	    		
	    	}
	    	catch (Exception e) {
	    		System.out.println("Erreur dans les arguments. ");
	    	}
    	}
    }
    
    public void start() {
    	createAllPieces();
    	
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
    
    public void addCavaliers() {
    	for (int i = 0; i < totalCavalier; i++) {
        	allPieces.add(new Cavalier(model.intVar(0, size-1), model.intVar(0, size-1)));
    	}
    }
    
    public void addFous() {
        for (int i = 0; i < totalFou; i++) {
        	allPieces.add(new Fou(model.intVar(0, size-1), model.intVar(0, size-1), size));
        }
    }
    
    public void addTours() {
        for (int i = 0; i < totalTour; i++) {
        	allPieces.add(new Tour(model.intVar(0, size-1), model.intVar(0, size-1), size));
        }
    }
    
    public void createAllPieces() {
    	addCavaliers();
    	addFous();
    	addTours();
    	if (custom) {
    		addCustomPieces();
    	}
    }
    
    public void addCustomPieces() {
    	
    	boolean stop = false;
    	
    	while (!stop){
        	System.out.print("Quelles lettre voulez-vous donner: ");
        	Scanner in = new Scanner(System.in);
        	String letter = Character.toString(in.next().charAt(0));
        	System.out.print("De combien de cases peut-il attaquer en ligne droite: ");
        	int droit = in.nextInt();
        	System.out.print("De combien de cases peut-il attaquer en diagonale: ");
        	int diagonale = in.nextInt();
        	String answer = "";
        	while (!answer.toLowerCase().equals("oui") && !answer.toLowerCase().equals("non")) {
            	System.out.print("Peut-il attaquer en L (oui ou non): ");
            	answer = in.next();
        	}
        	boolean L = answer.toLowerCase().equals("oui") ? true : false;
        	System.out.print("Combien de pièces de ce type voulez-vous: ");
        	int quantite = in.nextInt();
        	
        	droit = droit > size ? size-1 : droit;
        	droit = droit < 0 ? 0 : droit;
        	diagonale = diagonale > size ? size-1 : diagonale;
        	diagonale = diagonale < 0 ? 0 : diagonale;
        	for (int i = 0; i < quantite; ++i) {
        		PieceQuelconque pieceQuelconque = new PieceQuelconque(model.intVar(0, size-1), model.intVar(0, size-1),letter,diagonale,droit,L);
        		allPieces.add(pieceQuelconque);
        	}
        	System.out.println("=======================");
        	System.out.println("Piece " + letter + " créée avec succès !");
        	System.out.println("=======================");
        	answer = "";
        	while (!answer.toLowerCase().equals("oui") && !answer.toLowerCase().equals("non")) {
            	System.out.print("Voulez-vous encore rajouter une pièce (oui ou non): ");
            	answer = in.next();
        	}
        	stop = answer.toLowerCase().equals("non");
    	}
    	

    }
    
    public void doDomination() {
    	addDominationConstraints();
        addSuperpositionConstraint();
        postFinalConstraints();
        checkSolutionAndPrint();
    }
    public void doIndependance() {
    	addIndependanceConstraints();
        checkSolutionAndPrint();
    }
    
    public void addIndependanceConstraints() {
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
    }
    
    public void addConstraintDeplacements() {
		int next = 1;
		Constraint D = constraintPosition.get(0);
		while (next < constraintPosition.size()) {
			D = model.and(D,constraintPosition.get(next));
			++next;
		}
		constraintDeplacements.add(D);
		constraintPosition.clear();
    }
    
    public void addConstraintPosition(int p, int newPosI, int newPosJ) {

		for (int q = 0; q < allPieces.size(); ++q) { // Toutes les autres pièces ne sont pas dans la case
			if (p != q) {
				Piece nextPiece = allPieces.get(q);
				Constraint b = model.or(model.arithm(nextPiece.getI(), "!=", newPosI),model.arithm(nextPiece.getI(), "!=", newPosI));
				constraintPosition.add(b);
			}
		}
		addConstraintDeplacements();
    }
    
    public void addConstraintPiece() {
		int next = 1;
		Constraint P = constraintDeplacements.get(0);
		while (next < constraintDeplacements.size()) {
			P = model.and(P, constraintDeplacements.get(next));
			++next;
		}
		constraintPiece.add(P);
		constraintDeplacements.clear();
    }
    
    public void addConstraintAllPieces() {
		int next = 1;
		Constraint Z = constraintPiece.get(0);
		while (next < constraintPiece.size()) {
        	Z = model.or(Z,constraintPiece.get(next));
        	next+=1;
		}
		allConstraintPieces.add(Z);
		constraintPiece.clear();
    }
    
    
    public void addConstraintLigneDroite(int deplacementI, int deplacementJ, int p, int k, int l) {
		if (deplacementI > 1) { // on va vers le bas
			for (int i = 1; i < deplacementI; ++i){		// Différence de position
				addConstraintPosition(p, k+i, l);
			}
		}
		if (deplacementI < -1) { // on va vers le haut
			for (int i = -1; i > deplacementI; --i){	
				addConstraintPosition(p, k+i, l);
			}
		}
		if (deplacementJ > 1) { // on va vers la droite
			for (int j = 1; j < deplacementJ; ++j){		// Différence de position
				addConstraintPosition(p, k, l+j);
			}
		}
		if (deplacementJ < -1) { // on va vers la gauche
			for (int j = -1; j > deplacementJ; --j){		// Différence de position
				addConstraintPosition(p, k, l+j);
			}
		}    						

    }
    
    public void addConstraintDiagonale(int deplacementI, int deplacementJ, int p, int k, int l) {
        if (deplacementI > 1 && deplacementJ > 1) { // EN BAS A DROITE
            for (int i = 1; i < deplacementI; ++i) {
            	addConstraintPosition(p, k+i, l+i);
            }
        }
        else if (deplacementI < -1 && deplacementJ < -1) { // EN HAUT A GAUCHE
            for (int i = -1; i > deplacementI; --i) {
            	addConstraintPosition(p, k+i, l+i);
            }                           
        }
        else if (deplacementI > 1 && deplacementJ < -1) { // EN BAS A GAUCHE
            for (int i = 1; i < deplacementI; ++i) {
            	addConstraintPosition(p, k+i, l-i);
            }
        
        }
        else if (deplacementI < -1 && deplacementJ > 1) { // EN HAUT A DROITE
            for (int i = 1; i < deplacementJ; ++i) {
            	addConstraintPosition(p, k-i, l+i);
            }                                
        }                                
    }
    
    
    public void regroupAllConstraints() {
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
    
    public void addSuperpositionConstraint() {
        for (int i=0; i<allPieces.size(); ++i) {
        	for (int j=i+1; j<allPieces.size(); ++j) {
        		Piece currentPiece = allPieces.get(i);
        		Piece nextPiece = allPieces.get(j);
        		model.or(model.arithm(currentPiece.getI() ,"!=", nextPiece.getI()), model.arithm(currentPiece.getJ(), "!=", nextPiece.getJ())).post();
        	}
        }
    }

    public void postFinalConstraints() {
        if (!allConstraint.isEmpty()){
    		int second = 1;
    		Constraint X = allConstraint.get(0);
    		while (second < allConstraint.size()) {
            	X = model.and(X,allConstraint.get(second));
            	second+=1;
    		}
    		X.post();
        }
    }
    
    public void addDominationConstraints() {
        for (int k=0; k<size; ++k) {
        	for (int l=0; l<size; ++l) {
        		for (int p = 0; p < allPieces.size(); ++p) {
       				Piece currentPiece = allPieces.get(p);	        				
        			for (int index = 0; index < currentPiece.getDeplacementI().length; ++index) {
        				int deplacementI = currentPiece.getDeplacementI()[index];
        				int deplacementJ = currentPiece.getDeplacementJ()[index];
        				if (checkRange(k+deplacementI,l+deplacementJ)) {
        					Constraint a = model.and(model.arithm(currentPiece.getI(), "=", deplacementI+k), model.arithm(currentPiece.getJ(), "=", deplacementJ+l));
        					constraintDeplacements.add(a);
        					if (deplacementI == 0 || deplacementJ ==0) {	
        						addConstraintLigneDroite(deplacementI,deplacementJ,p,k,l);
        						// DEPLACEMENT LIGNE DROITE
        					}
                            else if (Math.abs(deplacementI) == Math.abs(deplacementJ)) { // DIAGONALE
                            	addConstraintDiagonale(deplacementI,deplacementJ,p,k,l);
                            }
        					addConstraintPiece();
        				}
        			}
        			addConstraintAllPieces();
        		}
        		regroupAllConstraints();
        	}
        }
    }
    
    public boolean checkRange(int posI, int posJ) {
    	return (posI < size && posI >= 0 && posJ < size && posJ >= 0);
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
    public void printMatrix() {
    	String[][] matrix = new String[size][size];
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			matrix[i][j] = "*";
    		}
    	}

    	for (int i=0; i<allPieces.size(); ++i) {
    		Piece currentPiece = allPieces.get(i);
    		matrix[currentPiece.getI().getValue()][currentPiece.getJ().getValue()] = currentPiece.getLetter();
    	}
    	
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println("");
    	}
    }
    
    public void checkSolutionAndPrint() {
    	System.out.println("Début de la résolution ... ");
		if (model.getSolver().solve()){
			System.out.println("Voici une solution possible : ");
			printMatrix();
		}
		else {
			System.out.println("Nous n'avons pas trouvé de solution.");
		}
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