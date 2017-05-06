import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Main {

	// 1. Create a Model
    public static int size = 3;
    public static int totalPosition = size*size;
    public static int totalTour = 2;
    public static int totalFou = 3;
    public static int totalCavalier = 1;
    
    public static String CAVALIER = "C";
    public static String TOUR = "T";
    public static String FOU = "F";
    
    public static String INDEPENDANCE = "-i";
    public static String DOMINATION = "-d";
    public static String SIZE = "-n";
    public static String argCAVALIER = "-c";
    public static String argTOUR = "-t";
    public static String argFOU = "-f";
    public static Model model = new Model("ProblèmePremièrePartie");
    
    public static int nbDeplacementCavalier = 8;
    
    public static void main(String[] args) {

    	String chosen = "notOK";
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
    
    public static void doDomination() {
    	// A METTRE A JOUR
        // 2. Create variables
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

		IntVar[] cavalierI = new IntVar[totalCavalier];
        IntVar[] cavalierJ = new IntVar[totalCavalier];
        for (int i = 0; i < totalCavalier; i++) {
        	cavalierI[i] = model.intVar(0, size-1);
        	cavalierJ[i] = model.intVar(0, size-1);
        }
        IntVar[] fouI = new IntVar[totalFou];
        IntVar[] fouJ = new IntVar[totalFou];
        for (int i = 0; i < totalFou; i++) {
        	fouI[i] = model.intVar(0, size-1);
        	fouJ[i] = model.intVar(0, size-1);
        }
		
        IntVar[] tourI = new IntVar[totalTour];
        IntVar[] tourJ = new IntVar[totalTour];
        for (int i = 0; i < totalTour; i++) {
        	tourI[i] = model.intVar(0, size-1);
        	tourJ[i] = model.intVar(0, size-1);
        }
        ArrayList<Constraint> constraintTour = new ArrayList<Constraint>();
        ArrayList<Constraint> constraintFou = new ArrayList<Constraint>();
        ArrayList<Constraint> constraintCavalier = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintFou = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintCavalier = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintPiece = new ArrayList<Constraint>();
        ArrayList<Constraint> globalConstraint = new ArrayList<Constraint>();
		int[] movement = new int[size];
		for (int k=0;k<size;++k) {
			movement[k] = k;
		}

        for (int k=0; k<size; ++k) {
        	for (int l=0; l<size; ++l) {
        		for (int m=0;m<totalTour;++m) {
        			constraintTour.add(model.or(model.arithm(tourI[m], "=", k), model.arithm(tourJ[m], "=", l)));
        		}
        		if (!constraintTour.isEmpty()) {
            		int second = 1;
            		Constraint Y = constraintTour.get(0);
            		while (second < constraintTour.size()) {
                    	Y = model.or(Y,constraintTour.get(second));
                    	second+=1;
            		}
            		allConstraintPiece.add(Y); 	// ENCORE FAIRE DES OU AVEC LES AUTRES PIECES
            		constraintTour.clear();
        		}
        		//ICI YA UN AND POUR TOUT LES PROCHAINS

        		for (int m=0;m<totalFou;++m) {
        			for (int deplacementFou : movement) {
        				Constraint a = model.and(model.arithm(fouI[m], "=", deplacementFou+k), model.arithm(fouJ[m], "=", deplacementFou+l));
        				Constraint b = model.and(model.arithm(fouI[m], "=", deplacementFou+k), model.arithm(fouJ[m], "=", -deplacementFou+l));
        				Constraint c = model.and(model.arithm(fouI[m], "=", -deplacementFou+k), model.arithm(fouJ[m], "=", deplacementFou+l));
       					Constraint d = model.and(model.arithm(fouI[m], "=", -deplacementFou+k), model.arithm(fouJ[m], "=", -deplacementFou+l));
       					constraintFou.add(model.or(a,b,c,d));
        			}
            		int next = 1;
            		Constraint Z = constraintFou.get(0);
            		while (next < constraintFou.size()) {
                    	Z = model.or(Z,constraintFou.get(next));
                    	next+=1;
            		}
            		allConstraintFou.add(Z);
            		constraintFou.clear();
        		}
        		if (!allConstraintFou.isEmpty()) {
            		int suivant = 1;
            		Constraint W = allConstraintFou.get(0);
            		while (suivant < allConstraintFou.size()) {
                    	W = model.or(W,allConstraintFou.get(suivant));
                    	suivant+=1;
            		}
            		allConstraintPiece.add(W); 	
            		allConstraintFou.clear();
        		}
        		
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
            		allConstraintPiece.add(W); 	
            		allConstraintCavalier.clear();
        		}
        		
        		
        		// OR POUR TOUTES LES PIECES
        		if (!allConstraintPiece.isEmpty()) {
        			int next = 1;
        			Constraint W = allConstraintPiece.get(0);
            		while (next < allConstraintPiece.size()) {
                    	W = model.or(W,allConstraintPiece.get(next));
                    	next+=1;
            		}
            		globalConstraint.add(W); 	
            		allConstraintPiece.clear();
        		}
        	}
        }


        // Superposition de Tour x Tour
        for (int i=0; i<totalTour; ++i) {
        	for (int j=i+1;j<totalTour;++j) {
        		model.or(model.arithm(tourI[i] ,"!=", tourI[j]), model.arithm(tourJ[i], "!=", tourJ[j])).post();
        	}
        }
        // Superposition de fou x fou
        for (int i=0; i<totalFou; ++i) {
        	for (int j=i+1; j<totalFou; ++j) {
        		model.or(model.arithm(fouI[i], "!=", fouI[j]), model.arithm(fouJ[i], "!=", fouJ[j])).post();
        	}
        }
        // Superposition de cavalier x cavalier
        for (int i=0; i<totalCavalier; ++i) {
        	for (int j=i+1; j<totalCavalier; ++j) {
        		model.or(model.arithm(cavalierI[i], "!=", cavalierI[j]), model.arithm(cavalierJ[i], "!=", cavalierJ[j])).post();
        	}
        }
        // Superposition de fou x Tour
        for (int i=0; i<totalFou; ++i) {
        	for (int j=0; j<totalTour; ++j) {
        		model.or(model.arithm(fouI[i], "!=", tourI[j]), model.arithm(fouJ[i], "!=", tourJ[j])).post();
        	}
        }
        // Superposition de fou x Cavalier
        for (int i=0; i<totalFou; ++i) {
        	for (int j=0; j<totalCavalier; ++j) {
        		model.or(model.arithm(fouI[i], "!=", cavalierI[j]), model.arithm(fouJ[i], "!=", cavalierJ[j])).post();
        	}
        }
        // Superposition de cavalier x Tour
        for (int i=0; i<totalCavalier; ++i) {
        	for (int j=0; j<totalTour; ++j) {
        		model.or(model.arithm(cavalierI[i], "!=", tourI[j]), model.arithm(cavalierJ[i], "!=", tourJ[j])).post();
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
        }

        System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			printMatrix(cavalierI, cavalierJ, fouI, fouJ, tourI, tourJ);
		}
		System.out.println("Stop");    	
        
    }
    
    public static void doIndependance() {
        // 2. Create variables
        IntVar[] cavalierI = new IntVar[totalCavalier];
        IntVar[] cavalierJ = new IntVar[totalCavalier];
        for (int i = 0; i < totalCavalier; i++) {
        	cavalierI[i] = model.intVar(0, size-1);
        	cavalierJ[i] = model.intVar(0, size-1);
        }
        IntVar[] fouI = new IntVar[totalFou];
        IntVar[] fouJ = new IntVar[totalFou];
        for (int i = 0; i < totalFou; i++) {
        	fouI[i] = model.intVar(0, size-1);
        	fouJ[i] = model.intVar(0, size-1);
        }
        IntVar[] tourI = new IntVar[totalTour];
        IntVar[] tourJ = new IntVar[totalTour];
        for (int i = 0; i < totalTour; i++) {
        	tourI[i] = model.intVar(0, size-1);
        	tourJ[i] = model.intVar(0, size-1);
        }
        
		for (int k=0;k<totalTour;++k) {
			//Tour x Tour
			for (int l=k+1;l<totalTour;++l) {
				model.arithm(tourI[k], "!=", tourI[l]).post();
				model.arithm(tourJ[k], "!=", tourJ[l]).post();
			}
			//Tour x Fou
			for (int l=0;l<totalFou;++l) {
				model.arithm(tourI[k], "!=", fouI[l]).post();
				model.arithm(tourJ[k], "!=", fouJ[l]).post();
			}
			//Tour x Cavalier
			for (int l=0;l<totalCavalier;++l) {
				model.arithm(tourI[k], "!=", cavalierI[l]).post();
				model.arithm(tourJ[k], "!=", cavalierJ[l]).post();
			}
		}
		
		int[] movement = new int[size];
		for (int k=0;k<size;++k) {
			movement[k] = k;
		}

		for (int k=0;k<totalFou;++k) {
			for (int l=k+1;l<totalFou;++l) {
				for (int deplacementFou : movement) {
					model.or(model.arithm(fouI[k], "-", fouI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", fouJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", fouI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", fouJ[l], "!=", -deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", fouI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", fouJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", fouI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", fouJ[l], "!=", -deplacementFou)).post();
				}
			}
			for (int l=0;l<totalTour;++l) {
				for (int deplacementFou : movement) {
					model.or(model.arithm(fouI[k], "-", tourI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", tourJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", tourI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", tourJ[l], "!=", -deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", tourI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", tourJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", tourI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", tourJ[l], "!=", -deplacementFou)).post();
				}
			}
			for (int l=0;l<totalCavalier;++l) {
				for (int deplacementFou : movement) {
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", -deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", -deplacementFou)).post();
				}
			}
		}
		
		
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
		
		for (int k=0; k<totalCavalier;++k) {
			for (int l=0;l<totalFou;++l) {
				for (int index = 0; index < nbDeplacementCavalier+1; ++index) {
					model.or(model.arithm(cavalierI[k], "-", fouI[l], "!=", deplacementPossibleCavalierI[index]), model.arithm(cavalierJ[k], "-", fouJ[l], "!=", deplacementPossibleCavalierJ[index])).post();
				}
				
			}
			for (int l=0;l<totalTour;++l) {
				for (int index = 0; index < nbDeplacementCavalier+1; ++index) {
					model.or(model.arithm(cavalierI[k], "-", tourI[l], "!=", deplacementPossibleCavalierI[index]), model.arithm(cavalierJ[k], "-", tourJ[l], "!=", deplacementPossibleCavalierJ[index])).post();
				}
			}
			
			for (int l=k+1;l<totalCavalier;++l) {
				for (int index = 0; index < nbDeplacementCavalier+1; ++index) {
					model.or(model.arithm(cavalierI[k], "-", cavalierI[l], "!=", deplacementPossibleCavalierI[index]), model.arithm(cavalierJ[k], "-", cavalierJ[l], "!=", deplacementPossibleCavalierJ[index])).post();
				}
			}			
		}
		System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			printMatrix(cavalierI, cavalierJ, fouI, fouJ, tourI, tourJ);
		}
		System.out.println("Stop");    	
    }

	
	/**
	 * Surement possible de faire plus efficace
	 * Pour les conversions entre INT[], ArrayList et IntVar;
	 * @param positionTour
	 * @return
	 */
	public static IntVar allDeplacementTour(int positionTour, Model model) {
		List<Integer> deplacement = new ArrayList<Integer>();
		int row = findRow(positionTour);
		int column = findColumn(positionTour);
		
		for (int i=0; i<totalPosition; ++i) {
			if (findRow(i) == row || findColumn(i) == column ) {
				deplacement.add(i);
			}
		}
		int[] possiblePosition = new int[deplacement.size()];
		for (int i=0; i<deplacement.size(); ++i) {
			possiblePosition[i] = deplacement.get(i);
		}
		
		IntVar solution = model.intVar(possiblePosition);
		
		return solution;
		
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
    public static void printMatrix(IntVar[] cavI, IntVar[] cavJ, IntVar[] fouI, IntVar[] fouJ, IntVar[] tourI, IntVar[] tourJ) {
    	String[][] matrix = new String[size][size];
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			matrix[i][j] = "--";
    		}
    	}

    	for (int i=0; i<cavI.length;++i) {
    		matrix[cavI[i].getValue()][cavJ[i].getValue()] = CAVALIER+(i+1);
    	}
    	for (int i=0; i<tourI.length;++i ) {
    		matrix[tourI[i].getValue()][tourJ[i].getValue()] = TOUR+(i+1);
    	}
    	for (int i=0; i<fouI.length;++i ) {
    		matrix[fouI[i].getValue()][fouJ[i].getValue()] = FOU+(i+1);
    	}
    	
    	for (int i=0; i<size; i++) {
    		for (int j=0; j<size; j++) {
    			System.out.print(matrix[i][j] + " ");
    		}
    		System.out.println("");
    	}
    }
    
    public static int findRow(int position) {
    	return position/size;
    }
    
    public static int findColumn(int position) {
    	return (position)%size;
    }
    
    public static int findInStringArray(String[] array, String value) {
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
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
    				// Fin d'affichage de la matrice
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------