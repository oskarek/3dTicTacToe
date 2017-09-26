import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
/**
 * Class for running MinMax search.
 */
class MinMax {
  private GameState bestNextState;
  private int me;
  private static final int maxDepth = 5;

  private HashMap<String, Integer> stateCache;
  private HashMap<String, GameState> bestNextStateCache;

  MinMax(GameState gameState, HashMap<String, Integer> stateCache, HashMap<String, GameState> bestNextStateCache) {
    bestNextState = gameState;
    me = gameState.getNextPlayer();
    this.stateCache = stateCache;
    this.bestNextStateCache = bestNextStateCache;
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

  // TODO: more equivalence states maybe?
  private ArrayList<String> equivalentStateStrings(GameState state) {
    ArrayList<String> states = new ArrayList<>();

    StringBuilder turnCubeLeft = new StringBuilder();
    StringBuilder turnCubeRight = new StringBuilder();
    StringBuilder turnCube180Degrees = new StringBuilder();
    StringBuilder turnCubeUp = new StringBuilder();
    StringBuilder turnCubeDown = new StringBuilder();
    StringBuilder turnCubeUp180Degrees = new StringBuilder();
    for(int row = 0; row < GameState.BOARD_SIZE; row++) {
      for (int column = 0; column < GameState.BOARD_SIZE; column++) {
        for (int layer = 0; layer < GameState.BOARD_SIZE; layer++) {
            turnCubeLeft.append(Constants.MESSAGE_SYMBOLS[state.at(row,GameState.BOARD_SIZE - 1 - layer,column)]);
            turnCubeRight.append(Constants.MESSAGE_SYMBOLS[state.at(row,layer,GameState.BOARD_SIZE - 1 - column)]);
            turnCube180Degrees.append(Constants.MESSAGE_SYMBOLS[state.at(row,GameState.BOARD_SIZE-1-column,GameState.BOARD_SIZE-1-layer)]);
            turnCubeUp.append(Constants.MESSAGE_SYMBOLS[state.at(GameState.BOARD_SIZE-1-layer,column,row)]);
            turnCubeDown.append(Constants.MESSAGE_SYMBOLS[state.at(layer,column,GameState.BOARD_SIZE-1-row)]);
            turnCubeUp180Degrees.append(Constants.MESSAGE_SYMBOLS[state.at(GameState.BOARD_SIZE-1-row,column,GameState.BOARD_SIZE-1-layer)]);
        }
      }
    }
    states.add(turnCubeLeft.toString());
    states.add(turnCubeRight.toString());
    states.add(turnCube180Degrees.toString());
    states.add(turnCubeUp.toString());
    states.add(turnCubeDown.toString());
    states.add(turnCubeUp180Degrees.toString());
/*    for (int i = 0; i < GameState.BOARD_SIZE; i++) {
      StringBuilder sb1 = new StringBuilder();
      StringBuilder sb2 = new StringBuilder();
      StringBuilder sb3 = new StringBuilder();
      StringBuilder sb4 = new StringBuilder();
      StringBuilder sb5 = new StringBuilder();
      StringBuilder sb6 = new StringBuilder();
      for (int j = 0; j < GameState.BOARD_SIZE; j++) {
        for (int k = 0; k < GameState.BOARD_SIZE; k++) {
          sb1.append(Constants.MESSAGE_SYMBOLS[state.at(i,j,k)]);
          sb2.append(Constants.MESSAGE_SYMBOLS[state.at(i,k,j)]);
          sb3.append(Constants.MESSAGE_SYMBOLS[state.at(j,i,k)]);
          sb4.append(Constants.MESSAGE_SYMBOLS[state.at(j,k,i)]);
          sb5.append(Constants.MESSAGE_SYMBOLS[state.at(k,i,j)]);
          sb6.append(Constants.MESSAGE_SYMBOLS[state.at(k,j,i)]);
        }
      }
      states.add(sb1.toString());
      states.add(sb2.toString());
      states.add(sb3.toString());
      states.add(sb4.toString());
      states.add(sb5.toString());
      states.add(sb6.toString());
    }*/
    return states;
  }

  private String stateString(GameState state) {
    StringBuilder ss = new StringBuilder();
    for (int i = 0; i < GameState.CELL_COUNT; i++)
      ss.append(Constants.MESSAGE_SYMBOLS[state.at(i)]);
    return ss.toString();
  }

  private int maxsearch(GameState state, int depth, int alpha, int beta) {
    Integer cacheVal;
    for (String s : equivalentStateStrings(state)) {
      cacheVal = stateCache.get(s);
      if (cacheVal != null) {
        bestNextState = bestNextStateCache.get(s);
        return cacheVal;
      }
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

    String stateStr = stateString(state);
    stateCache.put(stateStr, alpha);
    bestNextStateCache.put(stateStr, maxState);
    bestNextState = maxState;

    return alpha;
  }

  // TODO: use equivalent states
  private int minsearch(GameState state, int depth, int alpha, int beta) {
    Integer cacheVal = stateCache.get(stateString(state));
    for (String s : equivalentStateStrings(state)) {
      cacheVal = stateCache.get(s);
      if (cacheVal != null) {
        bestNextState = bestNextStateCache.get(s);
        return cacheVal;
      }
    }

    if (depth >= maxDepth || state.isEOG())
      return utilityForState(state);

    Vector<GameState> nextStates = new Vector<>();
    state.findPossibleMoves(nextStates);

    GameState minState = state;

    for (GameState nextState : nextStates) {
      int v = maxsearch(nextState, depth+1, alpha, beta);
      beta = Math.min(beta,v);
      if (v < beta) {
        beta = v;
        minState = nextState;
      }
      beta = Math.min(beta,v);
      if (v <= alpha)
        break;
    }

    String stateStr = stateString(state);
    stateCache.put(stateStr, alpha);
    bestNextStateCache.put(stateStr, minState);

    return beta;
  }
}
