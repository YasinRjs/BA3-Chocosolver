import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class ProblemeCavalier {
	
	private int size;
	private int totalPosition;
    private  Model model = new Model("ProblemeCavalier");
    private IntVar totalCavalier;
    private int copyTotalCavalier;
    private String limitTime;
	
	ProblemeCavalier(int size, String limitTime) {
		this.size = size;
		totalPosition = size*size;
		this.limitTime = limitTime;
	}
	
	public void start() {
        System.out.println("--- Début de la recherche pour une matrice de taille : " + size + "*" + size + " ---");
		dominationCavalier();
		System.out.println("");
        System.out.println("---------------------------------------------");
        System.out.println("Solution : " + copyTotalCavalier + " cavalier(s) nécessaire(s).");
        System.out.println("---------------------------------------------");
	}
	
	/**
	 * Permet de lancer la domination des cavaliers
	 */
	public void dominationCavalier() {
		
        ArrayList<Constraint> constraintCavalier = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintCavalier = new ArrayList<Constraint>();
        
        totalCavalier = model.intVar("Nombre de cavaliers",1,totalPosition);
        model.setObjective(Model.MINIMIZE, totalCavalier);

        Cavalier cavalier = new Cavalier();
        
        int[] deplacementI = cavalier.getDeplacementI();
        int[] deplacementJ = cavalier.getDeplacementJ();
              
        IntVar[][] matrix = model.intVarMatrix("Plateau", size, size, 0, 1);          
        
        for (int k = 0; k < size; ++k) {
        	for (int l = 0; l < size; ++l) {
        		//Constraint occuped = model.arithm(matrix[k][l], "=", 1);
        		
        		for (int index = 0; index < deplacementI.length; ++index) {
        			int checkPositionI = k + deplacementI[index];
        			int checkPositionJ = l + deplacementJ[index];
        			if (checkMatrixRange(checkPositionI, checkPositionJ)) {
	        			Constraint deplacement = model.arithm(matrix[checkPositionI][checkPositionJ],"=",1);
	        			constraintCavalier.add(deplacement);   
        			}
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
        	
        }
        if (!allConstraintCavalier.isEmpty()){
    		int second = 1;
    		Constraint X = allConstraintCavalier.get(0);
    		while (second < allConstraintCavalier.size()) {
            	X = model.and(X,allConstraintCavalier.get(second));
            	second+=1;
    		}
    		X.post();
    		allConstraintCavalier.clear();
    		IntVar[] totalSumArray = model.intVarArray(size, 0, size);
    		for (int i = 0; i < size; i++) {
    			model.sum(matrix[i],"=", totalSumArray[i]).post();
    		}
    		IntVar totalSum = model.intVar(0,totalPosition);
    		model.sum(totalSumArray, "=", totalSum).post();
    		model.arithm(totalSum, "-", totalCavalier,"=",0).post();
        }
        
        int[][] copyMatrix = new int[size][size];
        
        Solver solver = model.getSolver();
        solver.limitTime(limitTime);
        while(model.getSolver().solve()) {
            System.out.println("Une solution a été trouvée avec: "+totalCavalier.getValue());
        	System.out.println("En cours d'optimisation ...");
            for (int i = 0; i < size; ++i) {
            	for (int j = 0; j < size; ++j) {
            		copyMatrix[i][j] = matrix[i][j].getValue();
            	}
            }
            copyTotalCavalier = totalCavalier.getValue();
            
        }
        
        for (int i = 0; i < size; ++i) {
        	for (int j = 0; j < size; ++j) {
        		if (copyMatrix[i][j] == 1) {
        			System.out.print("C ");
        		}
        		else {
        			System.out.print("* ");
        		}
        	}
        	System.out.println();
        }
	}

	/**
	 * Vérifie si les positions sont à l'intérieur du plateau (matrice)
	 * @param posI
	 * 		Position I de la matrice
	 * @param posJ
	 * 		Position J de la matrice
	 * @return	boolean
	 */
	public boolean checkMatrixRange(int posI, int posJ) {
		return (posI < size && posI >= 0 && posJ < size && posJ >=0);
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
