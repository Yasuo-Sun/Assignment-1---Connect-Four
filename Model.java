/**
 * This file is to be completed by you.
 *
 * @author <Please enter your matriculation number, not your name>
 */
public final class Model
{
	// ===========================================================================
	// ================================ CONSTANTS ================================
	// ===========================================================================
	// The most common version of Connect Four has 7 rows and 6 columns.
	public static final int DEFAULT_NR_ROWS = 6;
	public static final int DEFAULT_NR_COLS = 7;
	public static final int DEFAULT_WIN_LENGTH = 4;
	public static final int MAX_ROWS_SIZE = 1000;
	public static final int MAX_COLS_SIZE = 1000;
	public static final int MAX_WIN_LENGTH = 1000;
	public static final int GAME_FILE_NUMBER = 4;
	public static final boolean DEFAULT_PLAY2_COMPUTER = false;

	// ========================================================================
	// ================================ FIELDS ================================
	// ========================================================================
	// The size of the board.
	private int nrRows;
	private int nrCols;
	private int winLength;

	// The state of board, value 0 mean empty, 1 mean discs of player 1, 2 mean discs of player 2;
	private int[][] board;

	// The round of game, start at 0;
	// When round % 2 == 0, this is the round of player 1.
	// When round % 2 == 1, this is the round of player 2.
	private int round;

	private boolean isPlayer2Computer;
	
	// =============================================================================
	// ================================ CONSTRUCTOR ================================
	// =============================================================================
	public Model()
	{
		// Initialise the board size to its default values.
		nrRows = DEFAULT_NR_ROWS;
		nrCols = DEFAULT_NR_COLS;
		winLength = DEFAULT_WIN_LENGTH;
		isPlayer2Computer = DEFAULT_PLAY2_COMPUTER;

		// Initialise the board state to its default values 0.
		// The state of board should be empty.
		board = new int[nrRows][nrCols];

		// Start at 0, player 1 goes first.
		round = 0;

	}
	
	// ====================================================================================
	// ================================ MODEL INTERACTIONS ================================
	// ====================================================================================
	public int[] getNonEmptyCols() {

		int nonEmptyColsNumber = 0;
		for (int col = 0; col < getNrCols(); col++) {
			if (isMoveValid(col)) {
				nonEmptyColsNumber++;
			}
		}

		int[] nonEmptyCols = new int[nonEmptyColsNumber];
		int pointer = 0;

		for (int col = 0; col < getNrCols(); col++) {
			if (isMoveValid(col)) {
				nonEmptyCols[pointer] = col;
				pointer++;
			}
		}
		return nonEmptyCols;
	}

	public boolean isMoveValid(int move) {

		for(int row = 0; row < getNrRows(); row++) {
			if (board[row][move] == 0) {
				return true;
			}
		}
		return false;
	}

	public void initialize(int rows, int cols, int length, boolean isComputer) {

		nrRows = rows;
		nrCols = cols;
		winLength = length;
		isPlayer2Computer = isComputer;

		round = 0;
		board = new int[nrRows][nrCols];

		for(int row = 0; row < getNrRows(); row++) {
			for (int col = 0; col < getNrCols(); col++) {
				board[row][col] = 0;
			}
		}

	}

	public void initialize(int setRound, int rows, int cols, int length, boolean isComputer, int[][] setBoard) {

		nrRows = rows;
		nrCols = cols;
		winLength = length;
		isPlayer2Computer = isComputer;

		round = setRound;
		board = setBoard;

	}

	public boolean isLastPlayerWin() {
		int[] copyData;
		boolean isWin = false;

		copyData = new int[getNrRows()];
		for(int col = 0; col < getNrCols(); col++) {

			for(int row = 0; row < getNrRows(); row++) {
				copyData[row] = board[row][col];
			}

			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		copyData = new int[getNrCols()];
		for(int row = 0; row < getNrRows(); row++) {

			for(int col = 0; col < getNrCols(); col++) {
				copyData[col] = board[row][col];
			}

			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		int copyArrayLength = getNrRows() + getNrRows() - 1;
		copyData = new int[copyArrayLength];
		for(int sum = 0; sum < copyArrayLength; sum++) {

			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			for(int row = 0; sum - row >= 0; row++) {
				copyData[row] = getBoardPosition(row, sum - row);
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		for(int row = 0; row < getNrRows(); row++) {

			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			for(int i = 0; i < getNrCols() && i + row < getNrRows(); i++) {
				copyData[i] = board[row + i][i];
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		for(int col = 0; col < getNrCols(); col++) {

			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			for(int i = 0; i < getNrRows() && i + col < getNrCols(); i++) {
				copyData[i] = board[i][col + i];
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}
		return isWin;
	}

	public int getBoardPosition(int row, int col) {
		if (row < 0 || getNrRows() - 1 < row || col < 0 || getNrCols() - 1 < col) {
			return  0;
		}
		return board[row][col];
	}

	private boolean isArrayWinCheck(int[] data) {
		int player = 0, count = 0;
		for(int i = 0; i < data.length; i++) {
			if(data[i] == 0) {
				player = 0;
				count = 0;
			} else if (player == data[i]) {
				count++;
				if(count >= winLength) {
					return true;
				}
			} else {
				player = data[i];
				count = 1;
			}
		}
		return false;
	}

	// determine weather the game is a draw, this function do not determine the
	// player win of game, you must first call isLastPlayerWin() function to ensure there
	// is no player win.
	// @return true when all empty places were filled.
	public boolean isGameDraw() {
		return round == (getNrRows() * getNrCols());
	}

	/**
	 * place a disc in col[move]
	 * @param move the col that should be placed
	 * @throws IllegalArgumentException if the col is out of the range or the col is full
	 */
	public void makeMove(int move) {
		if (move < 0 || getNrCols() <= move) {
			throw new IllegalArgumentException(
					"The given col must be larger zero and smaller than maxcols but is: " + move
			);
		}

		// Find the first empty place from bottom to top
		int discPosition = -1;
		for(int row = 0; row < getNrRows(); row++) {
			if (board[row][move] == 0) {
				discPosition = row;
				break;
			}
		}

		if (discPosition == -1) {
			throw new IllegalArgumentException(
					"The given col is full: " + move
			);
		}

		// When round % 2 == 0, this is the round of player 1.
		// When round % 2 == 1, this is the round of player 2.
		if (round % 2 == 0) {
			board[discPosition][move] = 1;
		} else {
			board[discPosition][move] = 2;
		}

		//this round is end, start a new round.
		round++;
	}
	
	// =========================================================================
	// ================================ GETTERS ================================
	// =========================================================================
	public int getNrRows() {
		return nrRows;
	}
	
	public int getNrCols() {
		return nrCols;
	}

	public int getRound() {
		return round;
	}

	public int getWinLength() {
		return winLength;
	}

	/**
	 * Get a specific row in board.
	 * @param row the number of row in board.
	 * @return reference of this row.
	 * @throws IllegalArgumentException if the row is out of the range
	 */
	public int[] getBoardRow(int row) {
		if (row < 0 || getNrRows() <= row) {
			throw new IllegalArgumentException("The given row must be larger zero and smaller than maxrows but is: " + row);
		}
		int[] returnRow = new int[getNrCols()];
		for(int i = 0; i < getNrCols(); i++) {
			returnRow[i] = board[row][i];
		}
		return returnRow;
	}

	public boolean getIsComputer() {
		return isPlayer2Computer;
	}
}
