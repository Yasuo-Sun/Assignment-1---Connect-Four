import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * This file is to be completed by you.
 *
 * @author <Please enter your matriculation number, not your name>
 */
public final class Controller
{
	private final Model model;
	private final TextView view;
	
	public Controller(Model model, TextView view)
	{
		this.model = model;
		this.view = view;
	}
	
	public void startSession() throws IOException {

		// gaming mean whether the player is in gaming.
		boolean isGaming = false;

		while(true) {
			String userInput;
			//Start guide
			if (!isGaming) {
				// When game is not start, print home page (input guide)
				// and get user input in these valid strings.
				String[] validInputOfStart = {"NewGame", "Exit", "LoadGame"};

				String inputGuideOfStart =
						"Type \"NewGame\" to start a new game.\n" +
						"Type \"LoadGame\" to load a game.\n"+
						"Type \"Exit\" to exit the programme.";

				userInput = getStringInRange(validInputOfStart, inputGuideOfStart);

			} else {

				String[] validInputInGaming = {"Place", "Concede", "NewGame", "Exit", "SaveGame", "Return"};

				String gameMessage = String.format(
						"This is round %d, player %d go.\n", model.getRound(), getCurrentPlayer()
				);
				String inputGuideInGaming =
						"Type \"Place\" to place a disc.\n" +
						"Type \"Concede\" to concede.\n" +
						"Type \"SaveGame\" to save game.\n" +
						"Type \"NewGame\" to start a new game.\n" +
						"Type \"Return\" to return last page. (This option will not save your game! You should save your game manually.)\n" +
						"Type \"Exit\" to exit the programme. (This option will not save your game! You should save your game manually.)";

				// If game set as playing with computer and game in the round of computer (player 2)
				if (model.getIsComputer() && getCurrentPlayer() == 2) {
					// Automatically set user input and do not receive any user input from console
					// This option do not print game message and user input guide in computer round
					userInput = "ComputerMove";
				} else {
					// If game is not set as play with computer Or if game set as play with computer
					// but this is the round of human (player 1), then print game message and
					// user input guide and get user input.
					userInput = getStringInRange(validInputInGaming, gameMessage + inputGuideInGaming);
				}

			}


			if (userInput.equals("NewGame")) {
				// Start new game
				 isGaming = isSuccessfulSetNewGame();
				 view.displayNewGameMessage();

			} else if (userInput.equals("Exit")) {
				// End the loop and exit programme
				break;

			} else if (userInput.equals("Place") && isGaming) {
				//Place a disc in a col

				String inputGuideOfPlace = "Type the number of col you want to place.";

				// print input guide and get user input in valid range
				int placeCol = getNumberOfRange(0, model.getNrCols() - 1, inputGuideOfPlace);

				// Check validation of moving
				if(model.isMoveValid(placeCol)) {
					model.makeMove(placeCol);

				} else {
					// Print error message to user
					System.out.println(
							"This col is full.\n" +
							"Please select a col that has at least one empty place."
					);
				}


			} else if (userInput.equals("Concede") && isGaming) {
				//Current player concede

				printDefeat();
				isGaming = false;

			} else if (userInput.equals("LoadGame")) {
				// Load a game in game file

				// Print basic details of game file, including weather or not game file empty
				// if game file is non-empty, print the round of saved game in game file
				printGameFiles(Model.GAME_FILE_NUMBER);

				// Set valid input strings
				// valid input string [0] is "Return"
				// if N > 0, valid input string [N] is "GameN"
				String[] validInputInLoadGame = new String[Model.GAME_FILE_NUMBER + 1];

				for (int gameFileNumber = 1; gameFileNumber <= Model.GAME_FILE_NUMBER; gameFileNumber++) {
					validInputInLoadGame[gameFileNumber] = String.format("Game%d", gameFileNumber);
				}
				validInputInLoadGame[0] = "Return";

				//--------------------------------------------------------------
				String inputGuideInLoadGame =
						"Type \"GameN\" to load game N.\n" +
						"For example: Type \"Game2\" to load game 2.\n" +
						"Type \"Return\" to return last page.";

				while (true) {
					// Print input guide and get user input in these valid strings
					String userInputInLoadGame = getStringInRange(validInputInLoadGame, inputGuideInLoadGame);

					if (!userInputInLoadGame.equals("Return")) {
						// user input is equal to string of form "GameN"

						// get path of "gameFileN"
						Path loadGameFilePath = getGameFilePath(userInputInLoadGame);

						if (!isGameFileEmpty(loadGameFilePath)) {
							loadGame(loadGameFilePath);
							isGaming = true;
							break;

						} else {

							String gameFileEmptyMessage =
									"This file is empty.\n" +
									"Please select a non-empty file.";

							System.out.println(gameFileEmptyMessage);
						}

					} else {
						// user input equal to "Return", return to last page
						break;
					}
				}
			} else if (userInput.equals("SaveGame")) {
				// save game option

				// Print basic details of game file, including weather or not game file empty
				// if game file is non-empty, print the round of saved game in game file
				printGameFiles(Model.GAME_FILE_NUMBER);

				// Set valid input strings
				// valid input string [0] is "Return"
				// if N > 0, valid input string [N] is "GameN"
				String[] validInputInSaveGame = new String[Model.GAME_FILE_NUMBER + 1];
				for (int gameFileNumber = 1; gameFileNumber <= Model.GAME_FILE_NUMBER; gameFileNumber++) {
					validInputInSaveGame[gameFileNumber] = String.format("Game%d", gameFileNumber);
				}
				validInputInSaveGame[0] = "Return";

				String inputGuideInSaveGame =
						"Type \"GameN\" to save game at file n.\n" +
						"For example: Type \"Game2\" to save game at Game2 file.\n" +
						"Type \"Return\" to return last page.";

				while (true) {
					// Print input guide and get user input in these valid strings
					String userInputOfSaveGame = getStringInRange(validInputInSaveGame, inputGuideInSaveGame);

					if (!userInputOfSaveGame.equals("Return")) {
						// user input is equal to string of form "GameN"

						// get path of "gameFileN"
						Path gameFile = getGameFilePath(userInputOfSaveGame);

						if (isGameFileEmpty(gameFile)) {
							saveGameToFile(gameFile);
							break;

						} else {
							// if the game file that user want to save in is non-empty
							// print game file non-empty message and ask user weather or
							// not still want to save

							System.out.printf("This file is not empty\n");
							System.out.printf("Do you want to cover this file?\n");

							String[] validInputAnswer = {"Yes", "No"};
							String inputGuideInAnswer =
									"Type \"Yes\" to cover this file.\n" +
									"Type \"No\" to return last page.";

							String userInputAnswer = getStringInRange(validInputAnswer, inputGuideInAnswer);

							if (userInputAnswer.equals("Yes")) {
								saveGameToFile(gameFile);
								break;
							} else {
								//do nothing and get in next loop
							}

						}
					} else {
						// user input is equal to "Return"
						break;
					}
				}

			} else if (userInput.equals("ComputerMove")) {
				// user input is set as "ComputerMove" in game that is human vs computer model
				// and this is the round of computer
				// user can not input "ComputerMove" to get in this branch

				// get non-empty cols of board
				// this array can not be empty because draw of game was checked in last loop
				int[] nonEmptyCol = model.getNonEmptyCols();

				Random rand = new Random();

				// random generate a number in range 0 to nonEmptyCol.length - 1
				int moveCol = rand.nextInt(nonEmptyCol.length);

				model.makeMove(nonEmptyCol[moveCol]);

				System.out.printf("Computer: place a disc at %d\n", moveCol);

			} else if ((userInput.equals("Return"))) {
				// return to last page (home page)
				isGaming = false;
			}

			if (isGaming) {
				view.displayBoard(model);
			}

			// game state check after
			if (model.isLastPlayerWin()) {
				// print defeat player and win player
				printDefeat();
				isGaming = false;
			} else if (model.isGameDraw()) {
				System.out.println("This game is a draw, no player win");
				isGaming = false;
			}
		}
	}

	/**
	 * save all the information of current to a file
	 * this function can not be called before called printGameFiles function
	 * @param file Path of game file that the current game should save to
	 * @throws IOException if an I/O error occurs opening or creating the file
	 */
	private void saveGameToFile(Path file) throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		BufferedWriter writer = Files.newBufferedWriter(file, charset);

		// save round of game
		writer.write(Integer.toString(model.getRound()));
		writer.newLine();

		// save board rows
		writer.write(Integer.toString(model.getNrRows()));
		writer.newLine();

		// save board cols
		writer.write(Integer.toString(model.getNrCols()));
		writer.newLine();

		// save win length of game
		writer.write(Integer.toString(model.getWinLength()));
		writer.newLine();

		// save game model
		// human vs human 	 ---- true
		// human vs computer ---- false
		writer.write(Boolean.toString(model.getIsComputer()));
		writer.newLine();

		// save board state of current game
		for (int row = 0; row < model.getNrRows(); row++) {
			for (int col = 0; col < model.getNrCols(); col++) {
				writer.write(Integer.toString(model.getBoardPosition(row, col)));
				writer.newLine();
			}
		}
		writer.close();
		System.out.printf("Save game success.\n");
	}

	/**
	 * load all game information to variable model
	 * this function can not be called before called printGameFiles function
	 * @param file Path of game file to load
	 * @throws IOException if an I/O error occurs opening or creating the file
	 */
	private void loadGame(Path file) throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		BufferedReader reader = Files.newBufferedReader(file, charset);

		int round = 	Integer.parseInt(reader.readLine());
		int row =   	Integer.parseInt(reader.readLine());
		int col =   	Integer.parseInt(reader.readLine());
		int winLength = Integer.parseInt(reader.readLine());

		boolean isPlayer2Computer = Boolean.parseBoolean(reader.readLine());

		int[][] board = new int[row][col];

		for (int readRow = 0; readRow < row; readRow++) {
			for (int readCol = 0; readCol < col; readCol++) {
				board[readRow][readCol] = Integer.parseInt(reader.readLine());
			}
		}
		reader.close();
		model.initialize(round, row, col, winLength, isPlayer2Computer, board);
	}

	/**
	 * get game file path of a string
	 * @param gameFile A string of form "GameN", N should in rang 0 to 9
	 * @return Path of corresponding game file of game file string, "gameFileN.txt" form
	 */
	private Path getGameFilePath(String gameFile) {

		int length = gameFile.length();
		// get character N in string of form "GameN"
		char nCharacter = gameFile.charAt(length - 1);
		// char convert to int
		int gameFileNumber = Integer.parseInt(String.valueOf(nCharacter));
		// get name of game file
		String gameFilePath = String.format("gameFile%d.txt", gameFileNumber);

		return Paths.get(gameFilePath);
	}

	/**
	 * check the round of game in game file to get weather ot not this game file is empty
	 * the first of game file must be the round of game
	 * this function can not be called before called printGameFiles function
	 * @param file Path of game file
	 * @return if game file is empty, return true, else return false
	 * @throws IOException if an I/O error occurs opening or creating the file
	 */
	private boolean isGameFileEmpty(Path file) throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		BufferedReader reader = Files.newBufferedReader(file, charset);
		int round = Integer.parseInt(reader.readLine());;
		if (round == -1) {
			return true;
		}
		reader.close();
		return false;
	}

	/**
	 * print all game files basic information in form "GameN (round r)" or "GameN (empty!)"
	 * if game file is destroy or not exist, this function will create or initialize game file
	 * to correct form
	 * @param fileNumber the maximum number of game file
	 * @throws IOException if an I/O error occurs opening or creating the file
	 */
	private void printGameFiles(int fileNumber) throws IOException {

		Path[] gameFilePath = new Path[fileNumber];
		for(int gameFileNumber = 1; gameFileNumber <= fileNumber; gameFileNumber++) {
			// get the name of game file in form "gameFileN"
			String gameFileName = String.format("gameFile%d.txt", gameFileNumber);
			// gameFileNumber - 1 because path array start at 0
			gameFilePath[gameFileNumber - 1] = Paths.get(gameFileName);
		}

		Charset charset = Charset.forName("US-ASCII");
		for (int gameFileNumber = 1; gameFileNumber <= fileNumber; gameFileNumber++) {

			try {
				BufferedReader reader = Files.newBufferedReader(gameFilePath[gameFileNumber - 1], charset);
				int round = Integer.parseInt(reader.readLine());

				System.out.printf("Game%d ", gameFileNumber);
				if (round == -1) {
					// round is -1 meaning empty
					System.out.printf("(empty!)\n");
				} else {
					System.out.printf("(round %d)\n", round);
				}
				reader.close();

			} catch (NoSuchFileException e) {
				// the game file is not exist

				// create file
				Files.createFile(gameFilePath[gameFileNumber - 1]);
				// initialize game file as empty file form (round of this game file is -1)
				initializeGameFile(gameFilePath[gameFileNumber - 1]);

				System.out.printf("Game%d (empty!)\n", gameFileNumber);

			} catch (NumberFormatException e) {
				// data in game file is destroyed

				// initialize game file as empty file form (round of this game file is -1)
				initializeGameFile(gameFilePath[gameFileNumber - 1]);

				System.out.printf("Game%d (empty!)\n", gameFileNumber);
			}

		}
		System.out.print("\n");

	}

	/**
	 * initialize game file to empty form (round is -1)
	 * @param file path of game file
	 */
	private void initializeGameFile(Path file) {

		Charset charset = Charset.forName("US-ASCII");
		try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
			String round = "-1\n";
			writer.write(round);
			writer.close();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	/**
	 * Set a new game in two model, "Default" and "Customize".
	 * "Default" model is 6 * 7 size of board, 4 continue same color discs to win, human vs human.
	 * "Customize" model is decided by user input.
	 * @return If successful set a new game, return true. else return false;
	 */
	private boolean isSuccessfulSetNewGame() {
		String[] validInputOfNewGame = {"Default", "Customize", "Return"};

		String inputGuideOfNewGame =
				"Type \"Default\" to start game as default settings.\n" +
				"Type \"Customize\" to change the setting of game.\n" +
				"Type \"Return\" to return last page.";

		boolean isSuccessfulSetNewGame = false;

		while (true) {
			// Print input guide of setting game and get user input in these valid strings.
			String userInput = getStringInRange(validInputOfNewGame, inputGuideOfNewGame);

			if(userInput.equals("Default")) {

				// Set all parameter as default values and initialize model.
				model.initialize(Model.DEFAULT_NR_ROWS, Model.DEFAULT_NR_ROWS, Model.DEFAULT_WIN_LENGTH, Model.DEFAULT_PLAY2_COMPUTER);
				isSuccessfulSetNewGame = true;
				break;

			} else if(userInput.equals("Customize")) {

				String inputGuideOfCustomizeRow =
						"Type a number to customize board row.\n" +
						"Or type \"0\" to return last page.";

				String inputGuideOfCustomizeCol =
						"Type a number to customize board col\n" +
						"Or type \"0\" to return last page";

				String inputGuideOfCustomizeWinLength =
						"Type a number to customize win length\n" +
						"Or type \"0\" to return last page";

				int setRow = 0, setCol = 0, setWinLength = 0;
				// Print input guide and get user input to set row, value 0 mean return last page
				setRow = getNumberOfRange(0, Model.MAX_ROWS_SIZE, inputGuideOfCustomizeRow);
				if(setRow != 0) {

					// Print input guide and get user input to set col, value 0 mean return last page
					setCol = getNumberOfRange(0, Model.MAX_COLS_SIZE, inputGuideOfCustomizeCol);
					if(setCol != 0) {

						// Print input guide and get user input to set number of continuing same color discs
						// to win, value 0 mean return last page
						setWinLength = getNumberOfRange(0, Model.MAX_WIN_LENGTH, inputGuideOfCustomizeWinLength);
						if(setWinLength != 0) {

							// Print board details to help user to check.
							System.out.printf("Board row set as %d.\n", model.getNrRows());
							System.out.printf("Board col set as %d.\n", model.getNrCols());
							System.out.printf("Game rule win length set as %d.\n", model.getWinLength());
							System.out.printf("\n");

							// ------------------------------------------------------
							String[] validInputInPlayWithComputer = {"Yes", "No"};

							String inputGuideInPlayWithComputer =
									"Do you want to play with computer?\n" +
									"Type \"Yes\" to set player 2 as computer.\n" +
									"Type \"No\" to set player 2 as human";

							// Ask weather or not user want to play with computer and get answer.
							String userInputOfAnswerComputer = getStringInRange(validInputInPlayWithComputer, inputGuideInPlayWithComputer);

							boolean isPlayer2Computer = false;

							if (userInputOfAnswerComputer.equals("Yes")) {

								isPlayer2Computer = true;

							} else if (userInputOfAnswerComputer.equals("No")){
								//do nothing
							}

							model.initialize(setRow, setCol, setWinLength, isPlayer2Computer);

							isSuccessfulSetNewGame = true;
							break;
						}
					}
				}
			} else if(userInput.equals("Return")) {
				break;
			}
		}
		return isSuccessfulSetNewGame;
	}

	/**
	 * print defeat player and win player of this game
	 */
	private void printDefeat() {
		int defeatPlayer = getCurrentPlayer();
		int winPlayer = getOtherPlayer();
		System.out.printf("Defeat: player %d.\n", defeatPlayer);
		System.out.printf("Win:    player %d.\n", winPlayer);
	}

	/**
	 * get the current player number
	 * @return the current player number (1 or 2) of game
	 */
	private int getCurrentPlayer() {
		int currentPlayer = (model.getRound() % 2) + 1;
		return currentPlayer;
	}

	/**
	 * get the other player number (player in next round)
	 * @return the other player number (1 or 2) of game
	 */
	private int getOtherPlayer() {
		int otherPlayer = 3 - getCurrentPlayer();
		return otherPlayer;
	}

	/**
	 * get a input string from user in a valid input strings
	 * print the input guide string before user input
	 * @param range valid input strings list
	 * @param inputGuide a string to tell the user what should user to input
	 * @return a string of user input in one of valid input strings
	 */
	private String getStringInRange(String[] range, String inputGuide) {
		while (true) {
			System.out.println(inputGuide);

			String inputString = InputUtil.readStringFromUser();
			// check weather or not the user input is in valid input strings
			for(int i = 0; i < range.length; i++) {
				if(range[i].equals(inputString)) {
					return inputString;
				}
			}
			printInvalidInputMessages();
		}
	}

	/**
	 *  get a input number from user in valid range [min, max]
	 *  print the input guide string before user input
	 * @param min min of range [min, max]
	 * @param max max of range [min, max]
	 * @param inputGuide a string to tell the user what should user to input
	 * @return a number of user input in rang [min, max]
	 */
	private int getNumberOfRange(int min, int max, String inputGuide) {
		while (true) {
			// guide of input
			System.out.println(inputGuide);
			System.out.printf("Number should in range %d - %d\n", min, max);

			int inputNumber = InputUtil.readIntFromUser();
			if(min <= inputNumber && inputNumber <= max) {
				return inputNumber;
			}
			printInvalidInputMessages();
		}
	}

	/**
	 * print a message to tell user that makes a invalid input
	 */
	private void printInvalidInputMessages() {
		System.out.println("Please type a valid input.");
	}

}
