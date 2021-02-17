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
	// the number of continuing same color discs to win
	public static final int DEFAULT_WIN_LENGTH = 4;
	// upper limit of board row and columns size
	public static final int MAX_ROWS_SIZE = 1000;
	public static final int MAX_COLS_SIZE = 1000;
	// upper limit of number of continuing same color discs to win
	public static final int MAX_WIN_LENGTH = 1000;
	// number of game file
	public static final int GAME_FILE_NUMBER = 4;
	// is player 2 a computer
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

	// is the player 2 a computer
	private boolean isPlayer2Computer;
	
	// =============================================================================
	// ================================ CONSTRUCTOR ================================
	// =============================================================================
	public Model() {
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

	/**
	 *  get a list of non-empty cols of current game board
	 * @return a list that contain the non-empty columns number
	 */
	public int[] getNonEmptyCols() {

		int nonEmptyColsNumber = 0;
		// get the number of non-empty columns
		for (int col = 0; col < getNrCols(); col++) {
			if (isMoveValid(col)) {
				nonEmptyColsNumber++;
			}
		}

		if (nonEmptyColsNumber == 0) {
			System.err.println("ERROR: there is no empty cols.");
		}

		int[] nonEmptyCols = new int[nonEmptyColsNumber];
		int pointer = 0;

		// save the non-empty columns number to list
		for (int col = 0; col < getNrCols(); col++) {
			if (isMoveValid(col)) {
				nonEmptyCols[pointer] = col;
				pointer++;
			}
		}
		return nonEmptyCols;
	}

	/**
	 * to check weather or not a move is valid
	 * @param move the number of column that want to place a disc
	 * @return true if the column has a empty place
	 * @throws IllegalArgumentException invalid input of column number
	 */
	public boolean isMoveValid(int move) {
		if (move < 0 || getNrCols() <= move) {
			throw new IllegalArgumentException("move cols is not in valid rang! move is " + move);
		}

		// if the column has a empty place, return true
		// else return false
		for(int row = 0; row < getNrRows(); row++) {
			if (board[row][move] == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * initialize set of board of following parameters
	 * set current round of game as 0
	 * and set all place of board as empty
	 * @param rows set board number of rows
	 * @param cols set board number of columns
	 * @param length set board number of continuing same color discs to win
	 * @param isComputer set player 2 as computer or human
	 * @throws IllegalArgumentException invalid input of parameters
	 */
	public void initialize(int rows, int cols, int length, boolean isComputer) {
		if (rows < 0 || cols < 0 || length < 0 || MAX_ROWS_SIZE <= rows
				|| MAX_COLS_SIZE <= cols || MAX_WIN_LENGTH < length) {

			throw new IllegalArgumentException("invalid parameter!");
		}

		nrRows = rows;
		nrCols = cols;
		winLength = length;
		isPlayer2Computer = isComputer;

		round = 0;
		board = new int[nrRows][nrCols];

		// clear board
		for(int row = 0; row < getNrRows(); row++) {
			for (int col = 0; col < getNrCols(); col++) {
				board[row][col] = 0;
			}
		}

	}

	/**
	 * initialize set of board of following parameters
	 * @param setRound set round of current game
	 * @param rows set board number of rows
	 * @param cols set board number of columns
	 * @param length set board number of continuing same color discs to win
	 * @param isComputer set player 2 as computer or human
	 * @param setBoard set board state of current game
	 * @throws IllegalArgumentException invalid input of parameters
	 * @throws NullPointerException null of input array
	 */
	public void initialize(int setRound, int rows, int cols, int length, boolean isComputer, int[][] setBoard) {
		if (rows < 0 || cols < 0 || length < 0 || MAX_ROWS_SIZE <= rows
				|| MAX_COLS_SIZE <= cols || MAX_WIN_LENGTH < length || round < 0) {

			throw new IllegalArgumentException("invalid parameter!");
		}
		if (setBoard == null) {
			throw new NullPointerException("board array is null");
		}

		nrRows = rows;
		nrCols = cols;
		winLength = length;
		isPlayer2Computer = isComputer;

		round = setRound;
		board = setBoard;

	}

	/**
	 * check weather or not the last player win after move
	 * @return if last player win, return true. else return false
	 */
	public boolean isLastPlayerWin() {
		// copyData is the array to store that the sequence of board position to check
		int[] copyData;
		boolean isWin = false;

		// check by columns
		copyData = new int[getNrRows()];

		for(int col = 0; col < getNrCols(); col++) {

			// copy column to array
			for(int row = 0; row < getNrRows(); row++) {
				copyData[row] = board[row][col];
			}

			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		// check by rows
		copyData = new int[getNrCols()];

		for(int row = 0; row < getNrRows(); row++) {

			// copy row to array
			for(int col = 0; col < getNrCols(); col++) {
				copyData[col] = board[row][col];
			}

			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		// check by slope of shape: \ (from upper-left to lower-right)
		// the sum of row's number and columns' number of each slope is a constant
		// for example
		// 2  |0  0  0|
		// 1  |1  0  0|   consider the first 1 in this row, sum of row and col is 1 + 0 = 1
		// 0  |0  1  0|   consider the first 1 in this row, sum of row and col is 0 + 1 = 1
		//     0  1  2
		// these two of 1 are in same slope from upper-left to lower-right

		// the maximum length of array
		int copyArrayLength = getNrRows() + getNrRows() - 1;
		copyData = new int[copyArrayLength];

		// enumerate the sum of row and col from 0 to nrRow + nrCol - 2
		for(int sum = 0; sum < copyArrayLength; sum++) {

			// clean copyArray
			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			// enumeration the row of board, so the col is sum - row
			for(int row = 0; sum - row >= 0; row++) {
				// pay attention: maybe the row or col of position is not in board
				// if position is not in board, getBoardPosition function will return 0
				copyData[row] = getBoardPosition(row, sum - row);
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		// check by slope of shape: / (from upper-right to lower-left)
		// first enumerate the row of position
		for(int row = 0; row < getNrRows(); row++) {

			// clear copyArray
			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			// variable i means increment
			// check from lower-left to upper-right
			// this board shows how it works
			// 0 0 3 2 1 0 0
			// 0 3 2 1 0 0 0
			// 3 2 1 0 0 0 0
			// 2 1 0 0 0 0 0
			// 1 0 0 0 0 0 0
			// (row + 0, 0) (row + 1, 1) (row + 2, 2) ... is in same slope
			// 1 in board means the slope that is checked in first loop
			// 2 in board means the slope that is checked in second loop
			for(int i = 0; i < getNrCols() && i + row < getNrRows(); i++) {
				copyData[i] = board[row + i][i];
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}

		// check by slope of shape: / (from upper-right to lower-left)
		// first enumerate the col of position
		for(int col = 0; col < getNrCols(); col++) {

			// clear copyArray
			for(int i = 0; i < copyArrayLength; i++) {
				copyData[i] = 0;
			}

			// variable i means increment
			// check from lower-left to upper-right
			// this board shows how it works
			// 0 0 0 0 1 2 3
			// 0 0 0 1 2 3 0
			// 0 0 1 2 3 0 0
			// 0 1 2 3 0 0 0
			// 1 2 3 0 0 0 0
			// (0, col + 0) (1, col + 1) (2, col + 2) ... is in same slope
			// 1 in board means the slope that is checked in first loop
			// 2 in board means the slope that is checked in second loop
			for(int i = 0; i < getNrRows() && i + col < getNrCols(); i++) {
				copyData[i] = board[i][col + i];
			}
			if(isArrayWinCheck(copyData)) {
				isWin = true;
			}
		}
		return isWin;
	}

	/**
	 * get the data of board (row, col) position
	 * @param row number row of position in board
	 * @param col number col of position in board
	 * @return if these position is not inside the board, return 0
	 * 		   else return board[row][col] value
	 * @throws NullPointerException when input is null
	 */
	public int getBoardPosition(int row, int col) {
		if (row < 0 || getNrRows() - 1 < row || col < 0 || getNrCols() - 1 < col) {
			return  0;
		}
		return board[row][col];
	}

	/**
	 * check weather or not there are four continuing "1" or "2" in array
	 * @param data array should be check
	 * @return true if four continuing "1" or "2" exist, else false
	 */
	private boolean isArrayWinCheck(int[] data) {
		if (data == null) {
			throw new NullPointerException("The array to check is empty!");
		}
		// count is the current number of continuing "1" or "2"
		int player = 0, count = 0;
		for(int i = 0; i < data.length; i++) {
			if(data[i] == 0) {
				// board is empty in this position
				player = 0;
				count = 0;
			} else if (player == data[i]) {
				// same color disc compare to last disc
				count++;
				if(count >= winLength) {
					// game is win when continuing same color discs is equal or more than
					// win length
					return true;
				}
			} else {
				// this is other player's disc
				// set player as color and count discs from 1
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

	/**
	 *  get current row of board
	 * @return row of board
	 */
	public int getNrRows() {
		return nrRows;
	}

	/**
	 *  get current col of board
	 * @return col of board
	 */
	public int getNrCols() {
		return nrCols;
	}

	/**
	 *  get current game round
	 * @return game round
	 */
	public int getRound() {
		return round;
	}

	/**
	 *  get current game rule of number of continuing same color discs to win
	 * @return win length of game
	 */
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
			throw new IllegalArgumentException(
					"The given row must be larger zero and smaller than number of board rows but is: " + row
			);
		}
		int[] returnRow = new int[getNrCols()];
		for(int i = 0; i < getNrCols(); i++) {
			returnRow[i] = board[row][i];
		}
		return returnRow;
	}

	/**
	 * get weather or not player 2 is set as computer
	 * @return true if player 2 is computer, false if it is human
	 */
	public boolean getIsComputer() {
		return isPlayer2Computer;
	}
}
