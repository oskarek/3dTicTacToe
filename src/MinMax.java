import java.util.HashMap;
import java.util.Vector;
/**
 * Class for running MinMax search.
 */
class MinMax {
  private GameState bestNextState;
  private int me;
  private static final int maxDepth = 9;

  private HashMap<String, Integer> stateCache = new HashMap<>();

  MinMax(GameState gameState) {
    bestNextState = gameState;
    me = gameState.getNextPlayer();
  }

  GameState getBestNextState() {
    maxsearch(bestNextState, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    return bestNextState;
  }

  private int numberOfCellsForPlayer(GameState state, int player) {
    int sum = 0;
    for (int i = 0; i < GameState.CELL_COUNT; i++) {
      if (state.at(i) == player)
        sum++;
    }
    return sum;
  }

  // TODO: Right now, only first horizontal layer
  private int numberOfCellsInDiagonalsForPlayer(GameState state, int player) {
    int sum = 0;
    int inset = 0;

    for (int l = 0;l < GameState.BOARD_SIZE; l++) {

      //Superdiagonals
      if(state.at(l,l,l) == player) {
        sum++;
      }
      if(state.at(l,GameState.BOARD_SIZE - 1 - l,l) == player) {
        sum++;
      }
      if(state.at(GameState.BOARD_SIZE - 1 - l,l,l) == player){
        sum++;
      }
      if(state.at(GameState.BOARD_SIZE - 1 - l, GameState.BOARD_SIZE - 1 - l, l) == player) {
        sum++;
      }


      for (int i = 0; i < GameState.BOARD_SIZE; i++) {

        //Straight ahead diagonals
        if (state.at(i, inset, l) == player)
          sum++;
        if (state.at(i, GameState.BOARD_SIZE - 1 - inset, l) == player)
          sum++;

        //Ontop diagonals
        if (state.at(l, inset, i) == player)
          sum++;
        if (state.at(l, GameState.BOARD_SIZE - 1 - inset, i) == player)
          sum++;

        inset++;
      }
      inset = 0;
    }

    return sum;
  }

  private int utilityForState(GameState state) {
    if (state.isEOG()) {
      if (me == Constants.CELL_X)
        return state.isXWin() ? 1 : 0;
      else return state.isOWin() ? 1 : 0;
    } else {
      return eval(state);
    }
  }

  private int eval(GameState state) {
    return numberOfCellsInDiagonalsForPlayer(state, me);
  }

  private String stateString(GameState state) {
    StringBuilder ss = new StringBuilder();
    for (int i = 0; i < GameState.CELL_COUNT; i++)
      ss.append(Constants.MESSAGE_SYMBOLS[state.at(i)]);
    return ss.toString();
  }

  private int maxsearch(GameState state, int depth, int alpha, int beta) {
    Integer cacheVal = stateCache.get(stateString(state));
    if (cacheVal != null) {
      return cacheVal;
    }

    if (depth >= maxDepth || state.isEOG())
      return utilityForState(state);

    Vector<GameState> nextStates = new Vector<>();
    state.findPossibleMoves(nextStates);

    GameState maxState = state;

    for (GameState nextState : nextStates) {
      int v = minsearch(nextState, depth+1, alpha, beta);
      if (v > alpha) {
        alpha = v;
        maxState = nextState;
      }
      if (v >= beta)
        break;
    }

    stateCache.put(stateString(state), alpha);
    bestNextState = maxState;

    return alpha;
  }

  private int minsearch(GameState state, int depth, int alpha, int beta) {
    Integer cacheVal = stateCache.get(stateString(state));
    if (cacheVal != null)
      return cacheVal;

    if (depth >= maxDepth || state.isEOG())
      return utilityForState(state);

    Vector<GameState> nextStates = new Vector<>();
    state.findPossibleMoves(nextStates);

    for (GameState nextState : nextStates) {
      int v = maxsearch(nextState, depth+1, alpha, beta);
      beta = Math.min(beta,v);
      beta = Math.min(beta,v);
      if (v <= alpha)
        break;
    }

    stateCache.put(stateString(state), beta);

    return beta;
  }
}
