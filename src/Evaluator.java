class Evaluator {
  private static int sumLinesForPlayer(GameState state, int player) {
    int sum = 0;
    for (int i = 0; i < GameState.BOARD_SIZE; i++) {
      // rows and cols
      for (int j = 0; j < GameState.BOARD_SIZE; j++) {
        int[] horiz_row = new int[GameState.BOARD_SIZE];
        int[] horiz_col = new int[GameState.BOARD_SIZE];
        int[] vert_row = new int[GameState.BOARD_SIZE];
        int[] vert_col = new int[GameState.BOARD_SIZE];
        for (int k = 0; k < GameState.BOARD_SIZE; k++) {
          horiz_row[k] = state.at(j,k,i);
          horiz_col[k] = state.at(k,j,i);
          vert_row[k] = state.at(i,j,k);
          vert_col[k] = state.at(i,k,j);
        }
        sum += utilityForLine(horiz_row, player);
        sum += utilityForLine(horiz_col, player);
        sum += utilityForLine(vert_row, player);
        sum += utilityForLine(vert_col, player);
      }

      // diagonals
      int[] horiz_diag1 = new int[GameState.BOARD_SIZE];
      int[] horiz_diag2 = new int[GameState.BOARD_SIZE];
      int[] vert_diag1 = new int[GameState.BOARD_SIZE];
      int[] vert_diag2 = new int[GameState.BOARD_SIZE];
      int inset = 0;
      for (int j = 0; j < GameState.BOARD_SIZE; j++) {
        horiz_diag1[j] = state.at(j,inset,i);
        horiz_diag2[j] = state.at(j,GameState.BOARD_SIZE-1-inset,i);
        vert_diag1[j] = state.at(i,j,inset);
        vert_diag2[j] = state.at(i, j,GameState.BOARD_SIZE-1-inset);
        inset++;
      }
      sum += utilityForLine(horiz_diag1, player);
      sum += utilityForLine(horiz_diag2, player);
      sum += utilityForLine(vert_diag1, player);
      sum += utilityForLine(vert_diag2, player);
    }

    // superdiagonals
    int[] super_diag1 = new int[GameState.BOARD_SIZE];
    int[] super_diag2 = new int[GameState.BOARD_SIZE];
    int[] super_diag3 = new int[GameState.BOARD_SIZE];
    int[] super_diag4 = new int[GameState.BOARD_SIZE];
    for (int l = 0;l < GameState.BOARD_SIZE; l++) {
      super_diag1[l] = state.at(l, l, l);
      super_diag2[l] = state.at(l, GameState.BOARD_SIZE - 1 - l, l);
      super_diag3[l] = state.at(GameState.BOARD_SIZE - 1 - l, l, l);
      super_diag4[l] = state.at(GameState.BOARD_SIZE - 1 - l, GameState.BOARD_SIZE - 1 - l, l);
    }
    sum += utilityForLine(super_diag1, player);
    sum += utilityForLine(super_diag2, player);
    sum += utilityForLine(super_diag3, player);
    sum += utilityForLine(super_diag4, player);
    return sum;
  }

  private static int numberOfPlayerMarks(int[] line, int player) {
    int sum = 0;
    for (int c : line)
      if (c == player) sum++;
    return sum;
  }

  private static int utilityForLine(int[] line, int player) {
    int opponent = player == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X;

    int diff = numberOfPlayerMarks(line, player) - numberOfPlayerMarks(line, opponent);
    int sum = 0;
    for (int i = 0; i < Math.abs(diff); i++)
      sum += Math.pow(10,i);
    return diff >= 0 ? sum : -sum;
  }

  static int utilityForState(GameState state, int player) {
    if (state.isEOG()) {
      if (player == Constants.CELL_X)
        return state.isXWin() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      else return state.isOWin() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    } else {
      return eval(state, player);
    }
  }

  private static int eval(GameState state, int player) {
    return sumLinesForPlayer(state, player);
  }
}
