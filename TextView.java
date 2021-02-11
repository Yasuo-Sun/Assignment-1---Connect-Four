/**
 * This file is to be completed by you.
 *
 * @author <Please enter your matriculation number, not your name>
 */
public final class TextView
{
	public TextView()
	{
	
	}
	
	public final void displayNewGameMessage()
	{
		System.out.println("---- NEW GAME STARTED ----");
	}
	
	public final int askForMove()
	{
		System.out.print("Select a free column: ");
		return InputUtil.readIntFromUser();
	}
	
	public final void displayBoard(Model model)
	{
		int nrRows = model.getNrRows();
		int nrCols = model.getNrCols();
		// Get your board representation.
		
		// This can be used to print a line between each row.
		// You may need to vary the length to fit your representation.
		String rowDivider = "-".repeat(4 * nrCols + 1);
		
		// A StringBuilder is used to assemble longer Strings more efficiently.
		StringBuilder sb = new StringBuilder();
		
		// You can add to the String using its append method.
		sb.append(rowDivider);
		sb.append("\n");

		for(int row = model.getNrRows() - 1; row >= 0; row--) {
			int[] rowState = model.getBoardRow(row);
			for(int col = 0; col < model.getNrCols(); col++) {
				sb.append(rowState[col] + "   ");
			}
			sb.append("\n");
		}

		sb.append(rowDivider);
		
		// Then print out the assembled String.
		System.out.println(sb);
	}
}
