import javax.swing.*;
import java.util.Arrays;

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
	
	public void startSession()
	{
		// TODO Complete this method. The following bits of code should be useful:

		// gaming mean whether the player is in gaming.
		boolean isGaming = false;

		while(true) {
			String userInput;
			//Start guide
			if (!isGaming) {

				String[] validInputOfStart = {"NewGame", "Exit"};

				String inputGuideOfStart =
						"Type \"NewGame\" to start a new game.\n" +
						"Type \"Exit\" to exit the programme.";

				userInput = getStringInRange(validInputOfStart, inputGuideOfStart);

			} else {
				// Guide in gaming
				String[] validInputInGaming = {"Place", "Concede", "NewGame", "Exit"};
				String gameMessage = String.format(
						"This is round %d, player %d go.\n", model.getRound(), getCurrentPlayer()
				);
				String inputGuideInGaming =
						"Type \"Place\" to place a disc.\n" +
						"Type \"Concede\" to concede.\n" +
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
