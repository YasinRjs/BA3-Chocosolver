import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Main {

	// 1. Create a Model
    public static int size = 3;
    public static int totalPosition = size*size;
    public static int totalTour = 2;
    public static int totalFou = 0;
    public static int totalCavalier = 0;
    
    public static String CAVALIER = "C";
    public static String TOUR = "T";
    public static String FOU = "F";
    
    public static void main(String[] args) {
        Model model = new Model("my first problem");
		
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
				model.and(model.arithm(fouI[k], "!=", cavalierI[l]), model.arithm(fouJ[k], "!=", cavalierJ[l])).post();
				for (int deplacementFou : movement) {
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", -deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", deplacementFou)).post();
					model.or(model.arithm(fouI[k], "-", cavalierI[l], "!=", -deplacementFou), model.arithm(fouJ[k], "-", cavalierJ[l], "!=", -deplacementFou)).post();
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
    		matrix[cavI[i].getValue()][cavJ[i].getValue()] = CAVALIER+i;
    	}
    	for (int i=0; i<tourI.length;++i ) {
    		matrix[tourI[i].getValue()][tourJ[i].getValue()] = TOUR+i;
    	}
    	for (int i=0; i<fouI.length;++i ) {
    		matrix[fouI[i].getValue()][fouJ[i].getValue()] = FOU+i;
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
    	
}
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
    				// Fin d'affichage de la matrice
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
