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
    public void doDomination() {
    	
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

        System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			printMatrix(allPieces);
		}
		System.out.println("Stop");
        
    }
    
    public void doIndependance() {
        // 2. Create variables
    	List<int[]> allDeplacements;
    	
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
        
        allDeplacements = getDeplacement(0,size-1,false);
        int[] deplacementsTourI = allDeplacements.get(0);
        int[] deplacementsTourJ = allDeplacements.get(1);
        
		for (int k=0;k<totalTour;++k) {
			//Tour x Tour
			for (int l=k+1;l<totalTour;++l) {
				for (int index = 0; index < deplacementsTourI.length; ++index) {
					model.or(model.arithm(tourI[k], "-", tourI[l], "!=", deplacementsTourI[index]), model.arithm(tourJ[k], "-", tourJ[l], "!=", deplacementsTourJ[index])).post();
				}
			}
			//Tour x Fou
			for (int l=0;l<totalFou;++l) {
				for (int index = 0; index < deplacementsTourI.length; ++index) {
					model.or(model.arithm(tourI[k], "-", fouI[l], "!=", deplacementsTourI[index]), model.arithm(tourJ[k], "-", fouJ[l], "!=", deplacementsTourJ[index])).post();
				}			
			}
			//Tour x Cavalier
			for (int l=0;l<totalCavalier;++l) {
				for (int index = 0; index < deplacementsTourI.length; ++index) {
					model.or(model.arithm(tourI[k], "-", cavalierI[l], "!=", deplacementsTourI[index]), model.arithm(tourJ[k], "-", cavalierJ[l], "!=", deplacementsTourJ[index])).post();
				}
			}
		}
		
		allDeplacements = getDeplacement(size-1,0,false);
		int[] deplacementsFouI = allDeplacements.get(0); 
		int[] deplacementsFouJ = allDeplacements.get(1);
		

		for (int k=0;k<totalFou;++k) {
			for (int l=k+1;l<totalFou;++l) {
				for (int index = 0; index < deplacementsFouI.length; ++index) {
					model.or(model.arithm(fouI[k], "-", fouI[l], "!=", deplacementsFouI[index]), model.arithm(fouJ[k], "-", fouJ[l], "!=", deplacementsFouJ[index])).post();
				}
			}
			for (int l=0;l<totalTour;++l) {
				for (int index = 0; index < deplacementsFouI.length; ++index) {
					model.or(model.arithm(fouI[k], "-", tourI[l], "!=", deplacementsFouI[index]), model.arithm(fouJ[k], "-", tourJ[l], "!=", deplacementsFouJ[index])).post();
				}
			}
			for (int l=0;l<totalCavalier;++l) {
				for (int index = 0; index < deplacementsFouI.length; ++index) {
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", deplacementsFouI[index]), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", deplacementsFouJ[index])).post();
				}			}
		}
		
		allDeplacements = getDeplacement(0,0,true);
		int[] deplacementsCavalierI = allDeplacements.get(0); 
		int[] deplacementsCavalierJ = allDeplacements.get(1);
		
		
		for (int k=0; k<totalCavalier;++k) {
			for (int l=0;l<totalFou;++l) {
				for (int index = 0; index < deplacementsCavalierI.length; ++index) {
					model.or(model.arithm(cavalierI[k], "-", fouI[l], "!=", deplacementsCavalierI[index]), model.arithm(cavalierJ[k], "-", fouJ[l], "!=", deplacementsCavalierJ[index])).post();
				}
				
			}
			for (int l=0;l<totalTour;++l) {
				for (int index = 0; index < deplacementsCavalierI.length; ++index) {
					model.or(model.arithm(cavalierI[k], "-", tourI[l], "!=", deplacementsCavalierI[index]), model.arithm(cavalierJ[k], "-", tourJ[l], "!=", deplacementsCavalierJ[index])).post();
				}
			}
			
			for (int l=k+1;l<totalCavalier;++l) {
				for (int index = 0; index < deplacementsCavalierI.length; ++index) {
					model.or(model.arithm(cavalierI[k], "-", cavalierI[l], "!=", deplacementsCavalierI[index]), model.arithm(cavalierJ[k], "-", cavalierJ[l], "!=", deplacementsCavalierJ[index])).post();
				}
			}			
		}
		System.out.println("Start");
		while (model.getSolver().solve()){
			System.out.println("VOICI UNE SOLUTION POSSIBLE : ");
			//printMatrix(cavalierI, cavalierJ, fouI, fouJ, tourI, tourJ);
		}
		System.out.println("Stop");    	
    }

	
    /**
     * Retourne tous les déplacements possibles pour une pièce selon les paramètres donnés
     * 
     * @param diagonale
     * 			Le nombre de case que la pièce peut se déplacer en diagonale
     * @param droit
     * 			Le nombre de case que la pièce peut se déplacer sur une ligne
     * @param L
     * 			Booléen pour savoir si oui ou non la pièce peut se déplacer en L
     * @return
     * 		Une liste d'arrays de déplacement en I et en J
     */
    public List<int[]> getDeplacement(int diagonale, int droit, boolean L) {
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
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
    				// Fin d'affichage de la matrice
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
