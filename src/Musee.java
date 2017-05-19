import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Musee {

	private Model model;
	private ArrayList<ArrayList<IntVar>> matrix = new ArrayList<ArrayList<IntVar>>();
	private char MURFILE = '*';
	private int MUR = 0;
	private int VIDE = 1;
	private int NOMBRE_CAPTEURS = 4;
	private int CAPTEURS_ET_VIDE = 5;
	private IntVar totalCapteur;
	private int totalPositionVide = 0;
	private int sizeI = 0;
	private int sizeJ = 0;
	
	private char NORD = 'N';
	private char SUD = 'S';
	private char EST = 'E';
	private char OUEST = 'O';
	
	private int CAPTEUR_NORD = 2;
	private int CAPTEUR_SUD = 3;
	private int CAPTEUR_EST = 4;
	private int CAPTEUR_OUEST = 5;
	
	

	Musee(){
		model = new Model("Probleme du Musée");
		parseMuseeFile();
        for (int i = 0; i < sizeI; ++i) {
        	for (int j = 0; j < matrix.get(i).size(); ++j) {
        		if (getPositionValue(i,j) == 0) {
            		System.out.print("* ");
        		}
        		else {
        			System.out.print("  ");
        		}
        	}
        	System.out.println();
        }
        System.out.println("Matrice de taille : " + sizeI + " * " + sizeJ);

	}
	
	public void parseMuseeFile() {
		
		File file = new File("/home/yarslan/Bureau/Workspace/InfoFond/src/Musee.txt");
		try {
			Scanner input = new Scanner(file);
			while (input.hasNext()) {
				sizeI += 1;
				String nextToken = input.nextLine();
				ArrayList<IntVar> currentLine = new ArrayList<IntVar>();
				for (int i=0; i<nextToken.length(); ++i) {
					if (nextToken.charAt(i) == MURFILE) {
						IntVar values = model.intVar(MUR);
						currentLine.add(values);
					}
					else {
						IntVar values = model.intVar(VIDE, CAPTEURS_ET_VIDE);
						currentLine.add(values);
						totalPositionVide += 1;
					}
				}
				matrix.add(currentLine);
			}
			sizeJ = matrix.get(0).size();
		} catch (FileNotFoundException e) {
			System.out.println("Problème Parser");
			System.exit(1);
		}
	}
	
	public void solveMusee() {
        totalCapteur = model.intVar("Nombre de Capteur",0,totalPositionVide);
        model.setObjective(Model.MINIMIZE, totalCapteur);

        ArrayList<Constraint> constraintCapteur = new ArrayList<Constraint>();
        ArrayList<Constraint> allConstraintCapteurs = new ArrayList<Constraint>();

        for (int k = 0; k < sizeI; ++k) {
        	for (int l = 0; l < matrix.get(k).size(); ++l) {
    			//IF PAS UN MUR
        		if (!isMur(k, l)) {
        			Constraint occuped = model.arithm(getPosition(k,l), ">", 1);
        			allConstraintCapteurs.add(occuped);

        			int deplacementNORD = deplacementJusqueMur(k,l,NORD);
        			int deplacementSUD = deplacementJusqueMur(k,l,SUD);
        			int deplacementEST = deplacementJusqueMur(k,l,EST);
        			int deplacementOUEST = deplacementJusqueMur(k,l,OUEST);
        			
        			for (int m = 1; m < deplacementNORD+1; ++m) {
        				for (int n = 1; n < m; ++n) {
        					constraintCapteur.add(model.arithm(getPosition(k-n,l), "=", VIDE));
        					//LES TRUCS OU TU DOIS FAIRE AND
        				}
        				constraintCapteur.add(model.arithm(getPosition(k-m,l), "=", CAPTEUR_SUD));
                		int next = 1;
                		Constraint Z = constraintCapteur.get(0);
                		while (next < constraintCapteur.size()) {
                        	Z = model.and(Z,constraintCapteur.get(next));
                        	next+=1;
                		}
        				// LE TRUC DU OR
                		allConstraintCapteurs.add(Z);
                		constraintCapteur.clear();       		
        			}

        			for (int m = 1; m < deplacementSUD+1; ++m) {
        				for (int n = 1; n < m; ++n) {
        					constraintCapteur.add(model.arithm(getPosition(k+n,l), "=", VIDE));
        					//LES TRUCS OU TU DOIS FAIRE AND
        				}
        				constraintCapteur.add(model.arithm(getPosition(k+m,l), "=", CAPTEUR_NORD));
                		int next = 1;
                		Constraint Z = constraintCapteur.get(0);
                		while (next < constraintCapteur.size()) {
                        	Z = model.and(Z,constraintCapteur.get(next));
                        	next+=1;
                		}
        				// LE TRUC DU OR
                		allConstraintCapteurs.add(Z);
                		constraintCapteur.clear();       		
        			}

        			for (int m = 1; m < deplacementEST+1; ++m) {
        				for (int n = 1; n < m; ++n) {
        					constraintCapteur.add(model.arithm(getPosition(k,l+n), "=", VIDE));
        					//LES TRUCS OU TU DOIS FAIRE AND
        				}
        				constraintCapteur.add(model.arithm(getPosition(k,l+m), "=", CAPTEUR_OUEST));
                		int next = 1;
                		Constraint Z = constraintCapteur.get(0);
                		while (next < constraintCapteur.size()) {
                        	Z = model.and(Z,constraintCapteur.get(next));
                        	next+=1;
                		}
        				// LE TRUC DU OR
                		allConstraintCapteurs.add(Z);
                		constraintCapteur.clear();       		
        			}

        			for (int m = 1; m < deplacementOUEST+1; ++m) {
        				for (int n = 1; n < m; ++n) {
        					constraintCapteur.add(model.arithm(getPosition(k,l-n), "=", VIDE));
        					//LES TRUCS OU TU DOIS FAIRE AND
        				}
        				constraintCapteur.add(model.arithm(getPosition(k,l-m), "=", CAPTEUR_EST));
                		int next = 1;
                		Constraint Z = constraintCapteur.get(0);
                		while (next < constraintCapteur.size()) {
                        	Z = model.and(Z,constraintCapteur.get(next));
                        	next+=1;
                		}
        				// LE TRUC DU OR
                		allConstraintCapteurs.add(Z);
                		constraintCapteur.clear();       		        				
        			}

        			
    				// LE TRUC DU OR
        	        if (!allConstraintCapteurs.isEmpty()){
        	    		int second = 1;
        	    		Constraint X = allConstraintCapteurs.get(0);
        	    		while (second < allConstraintCapteurs.size()) {
        	            	X = model.or(X,allConstraintCapteurs.get(second));
        	            	second+=1;
        	    		}
        	    		X.post();
        	    		allConstraintCapteurs.clear();
          	        }

        		}
        
        	}
        }
        IntVar[] linesCounter = model.intVarArray(sizeI, 0, sizeJ);
        for (int lineNb = 0; lineNb < sizeI; ++lineNb) {
            IntVar[] sommeCapteurs = model.intVarArray(NOMBRE_CAPTEURS, 0, sizeJ);
        	IntVar[] list = new IntVar[sizeJ];
        	for (int i = 0; i < sizeJ; ++i) {
        		list[i] = getPosition(lineNb, i);
        	}
        	model.count(CAPTEUR_NORD, list, sommeCapteurs[0]).post();
        	model.count(CAPTEUR_SUD, list, sommeCapteurs[1]).post();
        	model.count(CAPTEUR_EST, list, sommeCapteurs[2]).post();
        	model.count(CAPTEUR_OUEST, list, sommeCapteurs[3]).post();
        	
        	model.sum(sommeCapteurs, "=", linesCounter[lineNb]).post();
        }
        
		model.sum(linesCounter, "=", totalCapteur).post();

        int[][] copyMatrix = new int[sizeI][sizeJ];
        Solver solver = model.getSolver();
        int copyTotalCapteur;
        while(model.getSolver().solve()) {
            System.out.println("Une solution a été trouvée avec: "+totalCapteur.getValue());
        	System.out.println("En cours d'optimisation ...");
            for (int i = 0; i < sizeI; ++i) {
            	for (int j = 0; j < sizeJ; ++j) {
            		copyMatrix[i][j] = getPositionValue(i,j);
            	}
            }
            copyTotalCapteur = totalCapteur.getValue();
        }
        
        for (int i = 0; i < sizeI; ++i) {
        	for (int j = 0; j < sizeJ; ++j) {
        		int value = copyMatrix[i][j];
        		if (value == 0) {
        			System.out.print("* ");
        		}
        		else if (value == 1) {
        			System.out.print("  ");
        		}
        		else if (value == 2) {
        			System.out.print("N ");
        		}
        		else if (value == 3) {
        			System.out.print("S ");
        		}
        		else if (value == 4) {
        			System.out.print("E ");
        		}
        		else {
        			System.out.print("O ");
        		}
        	}
        	System.out.println();
        }
        
		
		
/*    	IntVar[] totalSumArray = model.intVarArray(size, 0, size);
    	for (int i = 0; i < size; i++) {
    		model.sum(matrix[i],"=", totalSumArray[i]).post();
    	}
    	IntVar totalSum = model.intVar(0,totalPosition);
    	model.sum(totalSumArray, "=", totalSum).post();
    	model.arithm(totalSum, "-", totalCavalier,"=",0).post();
*/
    }
	
	public boolean isMur(int i, int j) {
		return getPositionValue(i,j) == 0;
	}
	
	public int deplacementJusqueMur(int i, int j, char direction) {
		int total = 0;
		int k;
		boolean notMur = true;
		if (direction == NORD) {
			k = i-1;
			while (k > 0 && notMur) {
				if (!isMur(k,j)) {
					total += 1;
					--k;
				}
				else {
					notMur = false;
				}
			}
		}
		else if (direction == SUD) {
			k = i+1;
			while (k < sizeI && notMur) {
				if (!isMur(k,j)) {
					total += 1;
					++k;
				}
				else {
					notMur = false;
				}
			}
		}
		else if (direction == EST) {
			k = j+1;
			while (k < sizeJ && notMur) {
				if (!isMur(i,k)) {
					total += 1;
					++k;
				}
				else {
					notMur = false;
				}
			}
		}
		else {
			k = j-1;
			while (k > 0 && notMur) {
				if (!isMur(i,k)) {
					total += 1;
					--k;
				}
				else {
					notMur = false;
				}
			}
		}
		
		return total;
		
	}
	
	public IntVar getPosition(int i, int j) {
		return matrix.get(i).get(j);
	}
	
	public int getPositionValue(int i, int j) {
		return getPosition(i,j).getValue();
	}
}
