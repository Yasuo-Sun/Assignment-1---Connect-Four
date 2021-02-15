import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		// TODO Complete this method. The following bits of code should be useful:

		// gaming mean whether the player is in gaming.
		boolean isGaming = false;

		while(true) {
			String userInput;
			//Start guide
			if (!isGaming) {

				String[] validInputOfStart = {"NewGame", "Exit", "LoadGame"};

				String inputGuideOfStart =
						"Type \"NewGame\" to start a new game.\n" +
						"Type \"LoadGame\" to load a game.\n"+
						"Type \"Exit\" to exit the programme.";

				userInput = getStringInRange(validInputOfStart, inputGuideOfStart);

			} else {
				// Guide in gaming
				String[] validInputInGaming = {"Place", "Concede", "NewGame", "Exit", "SaveGame"};
				String gameMessage = String.format(
						"This is round %d, player %d go.\n", model.getRound(), getCurrentPlayer()
				);
				String inputGuideInGaming =
						"Type \"Place\" to place a disc.\n" +
						"Type \"Concede\" to concede.\n" +
						"Type \"SaveGame\" to save game.\n" +
						"Type \"NewGame\" to start a new game.\n" +
						"Type \"Exit\" to exit the programme.";

				userInput = getStringInRange(validInputInGaming, gameMessage + inputGuideInGaming);

			}

			if (userInput.equals("NewGame")) {

				 isGaming = isSuccessfulSetNewGame();

			} else if (userInput.equals("Exit")) {

				//isGaming = false;
				break;

			} else if (userInput.equals("Place") && isGaming) {

				String inputGuideOfPlace = "Type the number of col you want to place.";
				int placeCol = getNumberOfRange(0, model.getNrCols() - 1, inputGuideOfPlace);
				if(model.isMoveValid(placeCol)) {
					model.makeMove(placeCol);
				} else {
					System.out.println(
							"This col is full.\n" +
							"Please select a col that has at least one empty place."
					);
				}


			} else if (userInput.equals("Concede") && isGaming) {

				printDefeat();
				isGaming = false;

			} else if (userInput.equals("LoadGame")) {

				printGameFiles(Model.GAME_FILE_NUMBER);

				String[] validInputInLoadGame = new String[Model.GAME_FILE_NUMBER + 1];
				for (int gameFileNumber = 1; gameFileNumber <= Model.GAME_FILE_NUMBER; gameFileNumber++) {
					validInputInLoadGame[gameFileNumber] = String.format("Game%d", gameFileNumber);
				}
				validInputInLoadGame[0] = "Return";

				String inputGuideInLoadGame =
						"Type \"GameN\" to load game N.\n" +
						"For example: Type \"Game2\" to load game 2.\n" +
						"Type \"Return\" to return last page.";

				while (true) {
					String userInputInLoadGame = getStringInRange(validInputInLoadGame, inputGuideInLoadGame);

					if (!userInputInLoadGame.equals("Return")) {

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
						break;
					}
				}
			} else if (userInput.equals("SaveGame")) {
				printGameFiles(Model.GAME_FILE_NUMBER);

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
					String userInputOfSaveGame = getStringInRange(validInputInSaveGame, inputGuideInSaveGame);

					if (!userInputOfSaveGame.equals("Return")) {

						Path gameFile = getGameFilePath(userInputOfSaveGame);

						if (isGameFileEmpty(gameFile)) {
							saveGameToFile(gameFile);
							break;

						} else {

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
								//do nothing
							}
						}
					} else {
						break;
					}
				}

			}

			if (isGaming) {
				view.displayBoard(model);
			}
			if (model.isLastPlayerWin()) {
				printDefeat();
				isGaming = false;
			} else if (model.isGameDraw()) {
				System.out.println("This game is a draw, no player win");
				isGaming = false;
			}
		}
	}

	private void saveGameToFile(Path file) throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		BufferedWriter writer = Files.newBufferedWriter(file, charset);

		writer.write(Integer.toString(model.getRound()));
		writer.newLine();

		writer.write(Integer.toString(model.getNrRows()));
		writer.newLine();

		writer.write(Integer.toString(model.getNrCols()));
		writer.newLine();

		writer.write(Integer.toString(model.getWinLength()));
		writer.newLine();

		for (int row = 0; row < model.getNrRows(); row++) {
			for (int col = 0; col < model.getNrCols(); col++) {
				writer.write(Integer.toString(model.getBoardPosition(row, col)));
				writer.newLine();
			}
		}
		writer.close();
		System.out.printf("Save game success.\n");
	}

	private void loadGame(Path file) throws IOException {
		Charset charset = Charset.forName("US-ASCII");
		BufferedReader reader = Files.newBufferedReader(file, charset);

		int round = 	Integer.parseInt(reader.readLine());
		int row =   	Integer.parseInt(reader.readLine());
		int col =   	Integer.parseInt(reader.readLine());
		int winLength = Integer.parseInt(reader.readLine());

		int[][] board = new int[row][col];

		for (int readRow = 0; readRow < row; readRow++) {
			for (int readCol = 0; readCol < col; readCol++) {
				board[readRow][readCol] = Integer.parseInt(reader.readLine());
			}
		}
		reader.close();
		model.initialize(round, row, col, winLength, board);
	}

	private Path getGameFilePath(String gameFile) {

		int length = gameFile.length();
		int gameFileNumber = Integer.parseInt(String.valueOf(gameFile.charAt(length - 1)));
		String gameFilePath = String.format("gameFile%d.txt", gameFileNumber);
		return Paths.get(gameFilePath);
	}

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

	private void printGameFiles(int fileNumber) throws IOException {

		Path[] gameFilePath = new Path[fileNumber];
		for(int gameFileNumber = 1; gameFileNumber <= fileNumber; gameFileNumber++) {
			String gameFileName = String.format("gameFile%d.txt", gameFileNumber);
			gameFilePath[gameFileNumber - 1] = Paths.get(gameFileName);
		}

		Charset charset = Charset.forName("US-ASCII");
		for (int gameFileNumber = 1; gameFileNumber <= fileNumber; gameFileNumber++) {

			try {
				BufferedReader reader = Files.newBufferedReader(gameFilePath[gameFileNumber - 1], charset);
				int round = Integer.parseInt(reader.readLine());

				System.out.printf("Game%d ", gameFileNumber);
				if (round == -1) {
					System.out.printf("(empty!)\n");
				} else {
					System.out.printf("(round %d)\n", round);
				}
				reader.close();

			} catch (NoSuchFileException e) {

				Files.createFile(gameFilePath[gameFileNumber - 1]);
				initializeGameFile(gameFilePath[gameFileNumber - 1]);
				System.out.printf("Game%d (empty!)\n", gameFileNumber);

			} catch (NumberFormatException e) {

				initializeGameFile(gameFilePath[gameFileNumber - 1]);
				System.out.printf("Game%d (empty!)\n", gameFileNumber);
			}

		}
		System.out.print("\n");

	}

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

	private boolean isSuccessfulSetNewGame() {
		String[] validInputOfNewGame = {"Default", "Customize", "Return"};

		String inputGuideOfNewGame =
				"Type \"Default\" to start game as default settings.\n" +
				"Type \"Customize\" to change the setting of game.\n" +
				"Type \"Return\" to return last page.";

		boolean isSuccessfulSetNewGame = false;

		while (true) {
			String userInput = getStringInRange(validInputOfNewGame, inputGuideOfNewGame);

			if(userInput.equals("Default")) {

				model.initialize(Model.DEFAULT_NR_ROWS, Model.DEFAULT_NR_ROWS, Model.DEFAULT_WIN_LENGTH);
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
				setRow = getNumberOfRange(0, Model.MAX_ROWS_SIZE, inputGuideOfCustomizeRow);
				if(setRow != 0) {
					setCol = getNumberOfRange(0, Model.MAX_COLS_SIZE, inputGuideOfCustomizeCol);
					if(setCol != 0) {
						setWinLength = getNumberOfRange(0, Model.MAX_WIN_LENGTH, inputGuideOfCustomizeWinLength);
						if(setWinLength != 0) {
							model.initialize(setRow, setCol, setWinLength);
							isSuccessfulSetNewGame = true;
							break;
						}
					}
				}
			} else if(userInput.equals("Return")) {
				break;
			}
		}
		if(isSuccessfulSetNewGame) {
			System.out.printf("Board row set as %d.\n", model.getNrRows());
			System.out.printf("Board col set as %d.\n", model.getNrCols());
			System.out.printf("Game rule win length set as %d.\n", model.getWinLength());
			view.displayNewGameMessage();
		}
		return isSuccessfulSetNewGame;
	}

	private void printDefeat() {
		int defeatPlayer = getCurrentPlayer();
		int winPlayer = getOtherPlayer();
		System.out.printf("Defeat: player %d.\n", defeatPlayer);
		System.out.printf("Win:    player %d.\n", winPlayer);
	}

	private int getCurrentPlayer() {
		int currentPlayer = (model.getRound() % 2) + 1;
		return currentPlayer;
	}

	private int getOtherPlayer() {
		int otherPlayer = 3 - getCurrentPlayer();
		return otherPlayer;
	}

	public String getStringInRange(String[] range, String inputGuide) {
		while (true) {
			System.out.println(inputGuide);
			String inputString = InputUtil.readStringFromUser();
			for(int i = 0; i < range.length; i++) {
				if(range[i].equals(inputString)) {
					return inputString;
				}
			}
			printInvalidInputMessages();
		}
	}

	public int getNumberOfRange(int min, int max, String inputGuide) {
		while (true) {
			System.out.println(inputGuide);
			System.out.printf("Number should in range %d - %d\n", min, max);
			int inputNumber = InputUtil.readIntFromUser();
			if(min <= inputNumber && inputNumber <= max) {
				return inputNumber;
			}
			printInvalidInputMessages();
		}
	}

	public void printInvalidInputMessages() {
		System.out.println("Please type a valid input.");
	}

}
