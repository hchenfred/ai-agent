package ravensproject;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
	boolean[] potentialAnswers;
	HashMap<String, Integer> figToObjectCount;
	HashMap<String, int[][]> figToArray;
	HashMap<String, Integer> figToPixelCount;
	double threshhold;
	int pixelA;
	int pixelB;
	int pixelC;
	int pixelD;
	int pixelE;
	int pixelF;
	int pixelG;
	int pixelH;
	
    public Agent() {
    	potentialAnswers = new boolean[9];
    	for (int i = 0; i < 9; i++) {
    		potentialAnswers[i] = true;
    	}
    	figToObjectCount = new HashMap<String, Integer>();  
    	figToArray = new HashMap<String, int[][]>();
    	figToPixelCount = new HashMap<String, Integer>();  
    	threshhold = 0.15;
    	pixelA = pixelB = pixelC = pixelD = pixelE = pixelF = pixelG = pixelH = 0;
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return an int representing its
     * answer to the question: 1, 2, 3, 4, 5, or 6. Strings of these ints 
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName(). Return a negative number to skip a problem.
     * 
     * Make sure to return your answer *as an integer* at the end of Solve().
     * Returning your answer as a string may cause your program to crash.
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
    	long startTime = System.currentTimeMillis();
    	for (int i = 0; i < 9; i++) {
    		potentialAnswers[i] = true;
    	}
    	try {
    		if (problem.hasVisual() && problem.getProblemType().equals("3x3")) {
    			System.out.println("************************The problem name is " + problem.getName() + "*********************");
    			if (problem.getName().equals("Basic Problem D-09	")) {
    				RavensFigure figA = problem.getFigures().get("A");
    				RavensFigure figB = problem.getFigures().get("B");
    				RavensFigure figC = problem.getFigures().get("C");
    				RavensFigure figD = problem.getFigures().get("D");
    				RavensFigure figE = problem.getFigures().get("E");
    				RavensFigure figF = problem.getFigures().get("F");
    				//Convert each Figure in a problem to a 2D array with only 1s and 0s
    				for (String figureName : problem.getFigures().keySet()) {
    					RavensFigure thisFigure = problem.getFigures().get(figureName);
    					figToArray.put(thisFigure.getName(), convertToArray(thisFigure));
    				}
    				
    				HashMap<String, Double> transformToScore = findTransform(figA, figB, figC, figD, figE, figF);
    				Double highestScore = 0.00;
    				String transform = null;
    				for (HashMap.Entry<String, Double> entry : transformToScore.entrySet()) {
    					if (entry.getValue() > highestScore) {
    						highestScore = entry.getValue();
    						transform = entry.getKey(); 
    					}
    				}
    				System.out.println("identified transform is " + transform);
    				int transformAnswer = selectAnswerBasedTransform(transform);
    				if (highestScore > 0.95) {
    					System.out.println("after identifing transform, the answer is " + transformAnswer);
    					return transformAnswer;
    				}
    				
    				
    				//Count the number of objects in each figure
    				for (String figureName : figToArray.keySet()) {
    					int num = numOfObjects(figToArray.get(figureName));
    					figToObjectCount.put(figureName, num);
    					System.out.println("In figure " + figureName + " there are " + num + " of objects");
    				}
    				selectAnswerBasedObjectCount(figToObjectCount, false);
    				System.out.println("after counting objects in each figure, potential remaining answers are ");
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						System.out.print(i + " ");
    					}
    				}
    				System.out.println("");
    				
    				int count = 0;
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == false) count++;
    				}
    				if (count == 8) {
    					for (int i = 1; i <= 8; i++) {
    						potentialAnswers[i] = true;
    					}
    				}
    				
    				//Count the number of pixels in each figure
    				for (String figureName : figToArray.keySet()) {
    					int num = numOfPixel(figToArray.get(figureName));
    					figToPixelCount.put(figureName, num);
    					System.out.println("In figure " + figureName + " there are " + num + " of pixels");
    				}
    				selectAnswerBasedPixelCount(figToPixelCount, false);
    				System.out.println("after counting pixels in each figure, potential remaining answers are ");
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						System.out.print(i + " ");
    					}
    				}
    				System.out.println("");
    				int potentialAnswerCount = 0;
    				int singleAnswer = 0;
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						potentialAnswerCount++;
    						singleAnswer = i;
    					}
    				}
    				if (potentialAnswerCount == 1) {
    					System.out.println("answer generated is " + singleAnswer);
    					long endTime = System.currentTimeMillis();
    					System.out.println("That took " + (endTime - startTime) + " milliseconds");
    					return singleAnswer;
    				}
    				
    				
    				System.out.println("reset answers, start checking diagonal");
    				for (int i = 1; i <= 8; i++) {
						potentialAnswers[i] = true;
					}
    				
    				selectAnswerBasedObjectCount(figToObjectCount, true);
    				System.out.println("after counting objects in each figure, potential remaining answers are ");
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						System.out.print(i + " ");
    					}
    				}
    				System.out.println("");
    				selectAnswerBasedPixelCount(figToPixelCount, true);
    				System.out.println("after counting pixels in each figure, potential remaining answers are ");
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						System.out.print(i + " ");
    					}
    				}
    				System.out.println("");
    				if (potentialAnswerCount == 1) {
    					System.out.println("answer generated is " + singleAnswer);
    					long endTime = System.currentTimeMillis();
    					System.out.println("That took " + (endTime - startTime) + " milliseconds");
    					return singleAnswer;
    				}
    				
    				potentialAnswerCount = 0;
    				for (int i = 1; i <= 8; i++) {
    					if (potentialAnswers[i] == true) {
    						potentialAnswerCount++;
    						singleAnswer = i;
    					}
    				}
    				if (potentialAnswerCount == 1) {
    					System.out.println("answer generated is " + singleAnswer);
    					long endTime = System.currentTimeMillis();
    					System.out.println("That took " + (endTime - startTime) + " milliseconds");
    					return singleAnswer;
    				}
    				
    				if (numOfAnswersLeft() > 1) {
    					System.out.println("couldn't figure out answer by looking at number of objects and number of pixels, use transform answer " + transformAnswer);
    					return transformAnswer;
    				}
    			}
    		}    		
    	} catch(Exception e) {
    		return -1;
    	}
    	return -1;
    }
    
    
    private int numOfAnswersLeft() {
    	int potentialAnswerCount = 0;
		for (int i = 1; i <= 8; i++) {
			if (potentialAnswers[i] == true) {
				potentialAnswerCount++;
			}
		}
		return potentialAnswerCount;
    }
    
    private void selectAnswerBasedObjectCount(HashMap<String, Integer> figToObjectCount, boolean isDiagonal) {
    	if (isDiagonal == false) {
    		int numOfObjectInAnswer = -1;
        	//check if the number of objects are equal between A and B, D and E
        	if ((figToObjectCount.get("A") == figToObjectCount.get("B")) && (figToObjectCount.get("B") == figToObjectCount.get("C")) && (figToObjectCount.get("D") == figToObjectCount.get("E")) && (figToObjectCount.get("G") == figToObjectCount.get("H"))) {
        		numOfObjectInAnswer = figToObjectCount.get("G");
        		for (int i = 1; i <=8; i++) {
        			if (figToObjectCount.get(Integer.toString(i)) != numOfObjectInAnswer) {
        				potentialAnswers[i] = false;
        			}
        		}
        		return;
        	}
        	
        	//check if the number objects increases by a fixed number
        	if ((figToObjectCount.get("B") - figToObjectCount.get("A")) == (figToObjectCount.get("C") - figToObjectCount.get("B"))) {
        		numOfObjectInAnswer = figToObjectCount.get("H") + figToObjectCount.get("H") - figToObjectCount.get("G");
        		for (int i = 1; i <=8; i++) {
        			if (figToObjectCount.get(Integer.toString(i)) != numOfObjectInAnswer) {
        				potentialAnswers[i] = false;
        			}
        		}
        		return;
        	}  	
        	
        	//overfitting for problem C-10
        	/*if ((figToObjectCount.get("C") == 2 * figToObjectCount.get("A")) && (figToObjectCount.get("F") == 2 * figToObjectCount.get("D"))) {
        		numOfObjectInAnswer = 2 * figToObjectCount.get("G");
        		for (int i = 1; i <=8; i++) {
        			if (figToObjectCount.get(Integer.toString(i)) != numOfObjectInAnswer) {
        				potentialAnswers[i] = false;
        			}
        		}
        		return;	
        	}*/
    	//check diagonal situation	
    	} else {
    		int numOfObjectInAnswer = -1;
        	//check if the number of objects are equal between A and B, D and E
        	if ((figToObjectCount.get("B") == figToObjectCount.get("F")) && (figToObjectCount.get("F") == figToObjectCount.get("G")) && (figToObjectCount.get("C") == figToObjectCount.get("D")) ) {
        		numOfObjectInAnswer = figToObjectCount.get("A");
        		for (int i = 1; i <=8; i++) {
        			if (figToObjectCount.get(Integer.toString(i)) != numOfObjectInAnswer) {
        				potentialAnswers[i] = false;
        			}
        		}
        		return;
        	}
        	
        	//check if the number objects increases by a fixed number
        	if ((figToObjectCount.get("F") - figToObjectCount.get("B")) == (figToObjectCount.get("G") - figToObjectCount.get("F"))) {
        		numOfObjectInAnswer = figToObjectCount.get("E") + figToObjectCount.get("E") - figToObjectCount.get("A");
        		for (int i = 1; i <=8; i++) {
        			if (figToObjectCount.get(Integer.toString(i)) != numOfObjectInAnswer) {
        				potentialAnswers[i] = false;
        			}
        		}
        		return;
        	}  	
    		
    	}
    
    }
    
    private void selectAnswerBasedPixelCount(HashMap<String, Integer> figToObjectCount, boolean isDiagonal) {
    	int numOfPixelsInAnswer = -1;
    	if (isDiagonal == false) {
    		pixelA = figToPixelCount.get("A");
        	pixelB = figToPixelCount.get("B");
        	pixelC = figToPixelCount.get("C");
        	pixelD = figToPixelCount.get("D");
        	pixelE = figToPixelCount.get("E");
        	pixelF = figToPixelCount.get("F");
        	pixelG = figToPixelCount.get("G");
        	pixelH = figToPixelCount.get("H");	
    	} else {
    		pixelA = figToPixelCount.get("B");
        	pixelB = figToPixelCount.get("F");
        	pixelC = figToPixelCount.get("G");
        	pixelD = figToPixelCount.get("C");
        	pixelE = figToPixelCount.get("D");
        	pixelF = figToPixelCount.get("H");
        	pixelG = figToPixelCount.get("A");
        	pixelH = figToPixelCount.get("E");	
    	}
    	//check if pixels count are equal
    	if ((Math.abs(pixelA - pixelB) <= Math.abs(pixelB - pixelA) * threshhold) && (Math.abs(pixelC - pixelB) <= Math.abs(pixelC-pixelB) * threshhold)) {
    		System.out.println("all images in a row are the same");
    		for (int i = 1; i <=8; i++) {
    			if (pixelG == figToPixelCount.get(Integer.toString(i))) {
    				potentialAnswers[i] = true;
    				for (int j = 1; j <= 8; j++) {
    					if (j != i) potentialAnswers[j] = false;
    				}
    				return;
    			}
    			if ((Math.abs(pixelG - figToPixelCount.get(Integer.toString(i))) > pixelG*0.05)) {
    				potentialAnswers[i] = false;
    			}
    		}
    		return;
    	}
    	
    	//check if pixelcount of B-A equals to pixelcount of C-B
    	if ((Math.abs((pixelB - pixelA) - (pixelC - pixelB)) < Math.abs(pixelB-pixelA) * threshhold) && (Math.abs((pixelE - pixelD) - (pixelF - pixelE)) < Math.abs(pixelE-pixelD) * threshhold)) {
    		System.out.println("B-A = C-B");
    		numOfPixelsInAnswer = pixelH + pixelH - pixelG;
    		for (int i = 1; i <= 8; i++) {
    			if (Math.abs(numOfPixelsInAnswer - figToPixelCount.get(Integer.toString(i))) > pixelH*threshhold) {
        			potentialAnswers[i] = false;
        		}
    		}
    		return;
    	}
    	
    	//check if pixel count are increasing
    	if (((pixelB > pixelA) && (pixelC > pixelB)) && ((pixelE > pixelD) && (pixelF > pixelE)) && (pixelH > pixelG))  {
    		//System.out.println("entering here...");
    		for (int i = 1; i <= 8; i++) {
    			if (figToPixelCount.get(Integer.toString(i)) < pixelH) {
    				potentialAnswers[i] = false;
    			}
    		}
    		return;
    	}  
    	
    	
    	//A + B = C && D + E = F
    	if ((pixelA + pixelB < 1.05*pixelC) && (pixelA + pixelB > 0.95*pixelC) && (pixelD + pixelE < 1.05*pixelF) && (pixelD + pixelE > 0.95*pixelF)) {
    		System.out.println("A + B = C && D + E = F");
    		numOfPixelsInAnswer = pixelG + pixelH;
    		int difference = Integer.MAX_VALUE;
    		int answer = -1;
    		for (int i = 1; i <= 8; i++) {
    			if (Math.abs(figToPixelCount.get(Integer.toString(i)) - numOfPixelsInAnswer) < difference) {
    				difference = Math.abs(figToPixelCount.get(Integer.toString(i)) - numOfPixelsInAnswer);
    				answer = i;
    			}
    		}
    		potentialAnswers[answer] = true;
    		for (int j = 1; j <= 8; j++) {
				if (j != answer) potentialAnswers[j] = false;
			}
    		return;
    	}
    	
    	//A - D = G && B - E = H
    	if ((pixelA - pixelD < 1.05*pixelG) && (pixelA - pixelD > 0.95*pixelG) && (pixelB - pixelE < 1.05*pixelH) && (pixelB - pixelE > 0.95*pixelH)) {
    		System.out.println("A - D = G && B - E = H");
    		numOfPixelsInAnswer = pixelC - pixelF;
    		for (int i = 1; i <= 8; i++) {
    			if (Math.abs(numOfPixelsInAnswer - figToPixelCount.get(Integer.toString(i))) > numOfPixelsInAnswer*threshhold) {
        			potentialAnswers[i] = false;
        		}
    		}    		
    		return;
    	}
    	
    	//A + C = B && D + F = E
    	if ((pixelA + pixelC < 1.05*pixelB) && (pixelA + pixelC > 0.95*pixelB) && (pixelD + pixelF < 1.05*pixelE) && (pixelD + pixelF > 0.95*pixelE)) {
    		System.out.println("A + C = B && D + F = E");
    		numOfPixelsInAnswer = pixelH - pixelG;
    		int difference = Integer.MAX_VALUE;
    		int answer = -1;
    		for (int i = 1; i <= 8; i++) {
    			if (Math.abs(figToPixelCount.get(Integer.toString(i)) - numOfPixelsInAnswer) < difference) {
    				difference = Math.abs(figToPixelCount.get(Integer.toString(i)) - numOfPixelsInAnswer);
    				answer = i;
    			}
    		}
    		potentialAnswers[answer] = true;
    		for (int j = 1; j <= 8; j++) {
				if (j != answer) potentialAnswers[j] = false;
			}
    		return;
    	}
    	
    	// A + B + C == D + E + F
    	if (((pixelA + pixelB + pixelC) <= 1.05 * (pixelD + pixelE + pixelF)) && ((pixelA + pixelB + pixelC) >= 0.95 * (pixelD + pixelE + pixelF)))  {
    		System.out.println("A+B+C = D+E+F");
    		numOfPixelsInAnswer = pixelA + pixelB + pixelC - pixelG - pixelH;	
    		for (int i = 1; i <= 8; i++) {
    			if (figToPixelCount.get(Integer.toString(i)) > 1.20 * numOfPixelsInAnswer || figToPixelCount.get(Integer.toString(i)) < 0.8 * numOfPixelsInAnswer) {
    				potentialAnswers[i] = false;
    			}
    		}
    		System.out.println("after counting pixels in each figure, potential remaining answers are ");
			for (int i = 1; i <= 8; i++) {
				if (potentialAnswers[i] == true) {
					System.out.print(i + " ");
				}
			}
    		if (oneAnswerLeft() != -1) return;
    		double highestScore = 0;
    		int answer = 0;
    		if (isDiagonal == false) {
    			int[][] orOperatorImage = threeImageOrOperation(figToArray.get("A"), figToArray.get("B"), figToArray.get("C"));
    			for (int i = 1; i <= 8; i++) {
    				if (potentialAnswers[i] == true) {
    					int[][] resultOrOperatorImage = threeImageOrOperation(figToArray.get("G"), figToArray.get("H"), figToArray.get(Integer.toString(i)));		
    					double simiScore = calculateSimilarity2(orOperatorImage,resultOrOperatorImage);
    					if (simiScore > highestScore) {
    						highestScore = simiScore;
    						answer = i;
    					}
    				}
    			}
    			
    		} else {
    			int[][] orOperatorImage = threeImageOrOperation(figToArray.get("B"), figToArray.get("F"), figToArray.get("G"));
    			for (int i = 1; i <= 8; i++) {
    				if (potentialAnswers[i] == true) {
    					int[][] resultOrOperatorImage = threeImageOrOperation(figToArray.get("A"), figToArray.get("E"), figToArray.get(Integer.toString(i)));  					
    					double simiScore = calculateSimilarity2(orOperatorImage,resultOrOperatorImage);
    					if (simiScore > highestScore) {
    						highestScore = simiScore;
    						answer = i;
    					}
    				}
    			}
    		}
    		if (highestScore > 0.9) {
    			for (int i = 1; i <= 8; i++) {
    				if (i == answer) {
    					potentialAnswers[i] = true;
    				} else {
    					potentialAnswers[i] = false;
    				}
    			}
    		}
    	}
    	
    	if ((Math.abs(pixelA - pixelB) < 1.1 * Math.abs(pixelD - pixelE)) && (Math.abs(pixelA - pixelB) > 0.9 * Math.abs(pixelD - pixelE)) && (Math.abs(pixelB - pixelC) < 1.1* Math.abs(pixelE - pixelF)) && (Math.abs(pixelB - pixelC) >0.9* Math.abs(pixelE - pixelF))) {
    		System.out.println("entering here increase by a fixed number");
    		numOfPixelsInAnswer = pixelH + pixelF - pixelE;
    		for (int i = 1; i <= 8; i++) {
    			if (figToPixelCount.get(Integer.toString(i)) > 1.05 * numOfPixelsInAnswer || figToPixelCount.get(Integer.toString(i)) < 0.95 * numOfPixelsInAnswer) {
    				potentialAnswers[i] = false;
    			}
    		}
    		return;
    	}
    }
    
    private int oneAnswerLeft() {
		int answerCount = 0;
		int answer = -1;
		for (int i = 1; i <= 8; i++) {
			if (potentialAnswers[i] == true) {
				answerCount++;
				answer = i;
			}
		}
		return answerCount == 1 ? answer : -1;
    }
    
    private double calculateSimilarity2(int[][] a, int[][] b) {
    	int N = a.length;
    	double numOfSamePixel = 0;
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			if (a[i][j] == b[i][j]) {
    				numOfSamePixel++;
    			}
    		}
    	}
    	System.out.println("calculated similarity score is " + numOfSamePixel/(N*N));
    	return numOfSamePixel/(N*N);
    }
    
    private int selectAnswerBasedTransform(String transform) {
    	int answer = -1;
    	int[][] generatedAnswer = null;
    	switch(transform)  {
    		case "Intersection (AND)":
    			generatedAnswer = andOperation(figToArray.get("G"), figToArray.get("H"));
    			answer = selectAnswerBasedTransformHelper(generatedAnswer);
    			break;
    		case "Union (OR)":
    			generatedAnswer = orOperation(figToArray.get("G"), figToArray.get("H"));
    			answer = selectAnswerBasedTransformHelper(generatedAnswer);
    			break;
    		case "XOR":
    			generatedAnswer = xorOperation(figToArray.get("G"), figToArray.get("H"));
    			answer = selectAnswerBasedTransformHelper(generatedAnswer);
    			break;
    		case "horizontalFlip":
    			generatedAnswer = horizontalFlip(figToArray.get("G"));
    			answer = selectAnswerBasedTransformHelper(generatedAnswer);	
    			break;	
    	}
    	return answer;
    	
    }
    
    private int selectAnswerBasedTransformHelper(int[][] generatedAnswer) {
    	double highestScore = 0.00;
    	int answer = -1;
    	for (int i = 1; i <= 8; i++) {
    		//if (potentialAnswers[i] == true) {
    			int[][] array = figToArray.get(Integer.toString(i));
    			double score = calculateSimilarity2(generatedAnswer, array);
    			if (score > highestScore) {highestScore = score; answer = i;}	
    		//}
    	}
    	if (highestScore > 0.95) {
    		return answer;
    	} else {
    		return -1;
    	}
    }
    	
    private HashMap<String, Double> findTransform(RavensFigure fig1, RavensFigure fig2, RavensFigure fig3, RavensFigure fig4, RavensFigure fig5, RavensFigure fig6) {
    	HashMap<String, Double> res = new HashMap<String, Double>();
    	int[][] array1 = convertToArray(fig1);
    	int[][] array2 = convertToArray(fig2);
    	int[][] array3 = convertToArray(fig3);
    	int[][] array4 = convertToArray(fig4);
    	int[][] array5 = convertToArray(fig5);
    	int[][] array6 = convertToArray(fig6);
    	System.out.println("AND");
    	res.put("Intersection (AND)", Math.min(calculateSimilarity2(andOperation(array1, array2), array3), calculateSimilarity2(andOperation(array4, array5), array6)));
    	System.out.println("XOR");
    	res.put("XOR", calculateSimilarity2(xorOperation(array1, array2), array3)); 	
    	System.out.println("Union");
    	res.put("Union (OR)", Math.min(calculateSimilarity2(orOperation(array1, array2), array3), calculateSimilarity2(orOperation(array4, array5), array6)));
    	System.out.println("Identical");
    	res.put("identical", calculateSimilarity2(array1, array2));
    	//res.put("verticalFlip", calculateSimilarity2(verticalFlip(array1), array2));
    	//res.put("horizontalFlip", calculateSimilarity2(horizontalFlip(array1), array3));
    	//res.put("rotate90Degree", calculateSimilarity2(rotate90(array1), array2));
    	//res.put("rotate180Degree", calculateSimilarity2(rotate180(array1), array2));
    	//res.put("rotate270Degree", calculateSimilarity2(rotate270(array1), array2));
    	//res.put("(A or B) - (A and B)", calculateSimilarity2(minusOperation(orOperation(array1, array2), andOperation(array1, array2)),array3));
    	return res;
    }
    
    private int[][] convertToArray(RavensFigure fig) {
    	BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fig.getVisual()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Raster raster = img.getData();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int res[][] = new int[h][w];
        for (int x = 0; x < h; x++) {
            for(int y = 0; y < w; y++) {
                int val = raster.getSample(x,y,0);
                if (val > 127) {
                	res[y][x] = 0;
                } else {
                	res[y][x] = 1;
                }
            }
        }
        return res;
    }
    
    private int[][] verticalFlip(int[][] a) {
    	int N = a.length;
    	int[][] res = new int[N][N];
    	for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                res[i][j] = a[a.length - 1- i][j];
            }
        }
    	return res;
    }
    
    private int[][] horizontalFlip(int[][] a) {
    	int N = a.length;
    	int[][] res = new int[N][N];
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			res[i][N - j - 1] = a[i][j];
    		}
    	}
    	return res;
    }
    
       
    private int[][] rotate90(int[][] a) {
    	int N = a.length;
    	int[][] res = new int[N][N];
    	for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                res[i][j] = a[N - j - 1][i];
            }
        }
    	return res;
    }
    
    private int[][] rotate180(int[][] a) {
    	return rotate90(rotate90(a));
    }
    
    private int[][] rotate270(int[][] a) {
    	return rotate90(rotate180(a));
    }
    
    private int[][] orOperation(int[][] a, int[][] b) {
    	int w = a[0].length;
    	int h = a.length;
    	int[][] res = new int[h][w];
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			if (a[i][j] == 1 || b[i][j] == 1) {
    				res[i][j] = 1;
    			} else {
    				res[i][j] = 0;
    			}
    		}
    	}
    	return res; 
    }
    
    private int[][] minusOperation(int[][] a, int[][] b) {
    	int N = a.length;
    	int[][] res = new int[N][N];
    	for (int i = 0; i < N; i++) {
    		for (int j = 0; j < N; j++) {
    			res[i][j] = a[i][j] - b[i][j];
    		}
    	}
    	return res;
    }
    
    private int[][] threeImageOrOperation(int[][] a, int[][] b, int[][] c) {
    	int w = a[0].length;
    	int h = a.length;
    	int[][] res = new int[h][w];
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			if (a[i][j] == 1 || b[i][j] == 1 || c[i][j] == 1) {
    				res[i][j] = 1;
    			} else {
    				res[i][j] = 0;
    			}
    		}
    	}
    	return res; 
    }
    
    
    
    private int[][] andOperation(int[][] a, int[][] b) {
    	int w = a[0].length;
    	int h = a.length;
    	int[][] res = new int[h][w];
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			if (a[i][j] == 1 && b[i][j] == 1) {
    				res[i][j] = 1;
    			} else {
    				res[i][j] = 0;
    			}
    		}
    	}
    	return res; 
    }
    
    private int[][] xorOperation(int[][] a, int[][] b) {
    	int w = a[0].length;
    	int h = a.length;
    	int[][] res = new int[h][w];
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			if (a[i][j] != b[i][j]) {
    				res[i][j] = 1;
    			} else {
    				res[i][j] = 0;
    			}
    		}
    	}
    	return res; 
    }
    
    private int numOfPixel(int[][] a) {
        if (a == null || a.length == 0) {
            return 0;
        }
        int m = a.length;
        int n = a[0].length;
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
            	if (a[i][j] == 1) {
            		count++;
            	}
            }
        }
        return count;
    }
    
/* 
 THIS CODE IS ADAPTED FROM  https://leetcode.com/discuss/92238/union-find-java-solution-easily-generalized-other-problems   
 */
    public int numOfObjects(int[][] grid) {  
    	int[][] distance = {{1,0},{-1,0},{0,1},{0,-1}};
        if (grid == null || grid.length == 0 || grid[0].length == 0)  {
            return 0;  
        }
        UnionFind uf = new UnionFind(grid);  
        int rows = grid.length;  
        int cols = grid[0].length;  
        for (int i = 0; i < rows; i++) {  
            for (int j = 0; j < cols; j++) {  
                if (grid[i][j] == 1) {  
                    for (int[] d : distance) {
                        int x = i + d[0];
                        int y = j + d[1];
                        if (x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] == 1) {  
                            int id1 = i*cols+j;
                            int id2 = x*cols+y;
                            uf.union(id1, id2);  
                        }  
                    }  
                }  
            }  
        }  
        return uf.count;  
    }
    
    private void printArray(int[][] array) {
    	int h = array.length;
    	int w = array[0].length;
    	for (int i = 0; i < h; i++) {
    		for (int j = 0; j < w; j++) {
    			//if (array[i][j] == 1) {
    				System.out.print(array[i][j]);
    			//}
    		}
    		System.out.println("");
    	}
    }
}
