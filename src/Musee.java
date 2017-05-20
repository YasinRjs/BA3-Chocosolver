import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Musee {

	private Model model = new Model("Problème du musée");
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
	int copyTotalCapteur = 0;
	int[][] copyMatrix;
	Map<Integer,String> dicoValues;
	ArrayList<Integer> listCapteurs = new ArrayList<Integer>();
	
	private String NORD = "N";
	private String SUD = "S";
	private String EST = "E";
	private String OUEST = "O";
	
	private int CAPTEUR_NORD = 2;
	private int CAPTEUR_SUD = 3;
	private int CAPTEUR_EST = 4;
	private int CAPTEUR_OUEST = 5;
	
    ArrayList<Constraint> constraintCapteur = new ArrayList<Constraint>();
    ArrayList<Constraint> allConstraintCapteurs = new ArrayList<Constraint>();
	
	Musee(){
		parseMuseeFile();
		printMusee();
		createDicoValuesAndListCapteurs();
        addConstraints();
        minimizeCapteur();
        solve();
        printSolution();
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
			input.close();
			sizeJ = matrix.get(0).size();
		} catch (FileNotFoundException e) {
			System.out.println("Problème Parser");
			System.exit(1);
		}
	}
	
	public void addConstraints() {
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
        			
        			searchCapteurInDirection(deplacementNORD, NORD, k, l);
        			searchCapteurInDirection(deplacementSUD, SUD, k, l);
        			searchCapteurInDirection(deplacementEST, EST, k, l);
        			searchCapteurInDirection(deplacementOUEST, OUEST, k, l);

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
    }
	
	public void createDicoValuesAndListCapteurs() {
		dicoValues = new HashMap<>();
		dicoValues.put(0, "*");
		dicoValues.put(1, " ");
		dicoValues.put(2, SUD);
		dicoValues.put(3, NORD);
		dicoValues.put(4, OUEST);
		dicoValues.put(5, EST);
		listCapteurs.add(CAPTEUR_NORD);
		listCapteurs.add(CAPTEUR_SUD);
		listCapteurs.add(CAPTEUR_EST);
		listCapteurs.add(CAPTEUR_OUEST);
	}

	public void solve() {
        copyMatrix = new int[sizeI][sizeJ];
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
	}
	
	public void printSolution() {
        if (copyTotalCapteur != 0){
            printMatrix(copyMatrix);
            System.out.println("Solution optimale trouvée avec : " + copyTotalCapteur + " capteurs.");
        }
        else {
        	System.out.println("Aucune solution trouvée !");
        }
	}
	
	public void minimizeCapteur() {
        totalCapteur = model.intVar("Nombre de Capteur",0,totalPositionVide);
        model.setObjective(Model.MINIMIZE, totalCapteur);
        IntVar[] linesCounter = model.intVarArray(sizeI, 0, sizeJ);
        for (int lineNb = 0; lineNb < sizeI; ++lineNb) {
            IntVar[] sommeCapteurs = model.intVarArray(NOMBRE_CAPTEURS, 0, sizeJ);
        	IntVar[] list = new IntVar[sizeJ];
        	for (int i = 0; i < sizeJ; ++i) {
        		list[i] = getPosition(lineNb, i);
        	}
        	
        	for (int i=0; i<listCapteurs.size() ; ++i) {
        		model.count(listCapteurs.get(i), list, sommeCapteurs[i]).post();
        	}

        	model.sum(sommeCapteurs, "=", linesCounter[lineNb]).post();
        }
        
		model.sum(linesCounter, "=", totalCapteur).post();
	}
	
	public void searchCapteurInDirection(int deplacement, String direction, int k, int l) {
		int ligne=0;
		int colonne=0;
		int currentCapteur;
		if (direction == NORD) {
			ligne = -1;
			currentCapteur = CAPTEUR_NORD;
		}
		else if (direction == SUD) {
			ligne = 1;
			currentCapteur = CAPTEUR_SUD;
		}
		else if (direction == EST) {
			colonne = 1;
			currentCapteur = CAPTEUR_EST;
		}
		else {
			colonne = -1;
			currentCapteur = CAPTEUR_OUEST;
		}
		
		for (int m = 1; m < deplacement+1; ++m) {
			for (int n = 1; n < m; ++n) {
				constraintCapteur.add(model.arithm(getPosition(k+ligne*n,l+colonne*n), "=", VIDE));
				//LES TRUCS OU TU DOIS FAIRE AND
			}
			constraintCapteur.add(model.arithm(getPosition(k+ligne*m,l+colonne*m), "=", currentCapteur));
    		int next = 1;
    		Constraint Z = constraintCapteur.get(0);
    		while (next < constraintCapteur.size()) {
            	Z = model.and(Z,constraintCapteur.get(next));
            	next+=1;
    		}
			//LE TRUC DU OR
    		allConstraintCapteurs.add(Z);
    		constraintCapteur.clear();       		
		}
	}

	public boolean isMur(int i, int j) {
		return getPositionValue(i,j) == 0;
	}
	
	public void printMatrix(int[][] copyMatrix) {
        for (int i = 0; i < sizeI; ++i) {
        	for (int j = 0; j < sizeJ; ++j) {
        		int value = copyMatrix[i][j];
        		String elem = getCharFromValue(value);
        		System.out.print(elem + " ");
        	}
            System.out.println();
        }
    }		
	
	public String getCharFromValue(int value) {
		return dicoValues.get(value);
	}
	
	public int deplacementJusqueMur(int i, int j, String direction) {
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
	
	public void printMusee() {
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
	
	public IntVar getPosition(int i, int j) {
		return matrix.get(i).get(j);
	}
	
	public int getPositionValue(int i, int j) {
		return getPosition(i,j).getValue();
	}
}
