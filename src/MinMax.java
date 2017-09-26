import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
/**
 * Class for running MinMax search.
 */
class MinMax {
  private GameState state;
  private int me;

  // State string -> depth -> ordered next states
  private HashMap<String, HashMap<Integer, Vector<GameState>>> orderedNextStates = new HashMap<>();

  MinMax(GameState gameState) {
    state = gameState;
    me = gameState.getNextPlayer();
  }

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

  GameState getBestNextState() {
    Vector<GameState> nextStates = new Vector<>();
    state.findPossibleMoves(nextStates);

    // get the state that maximizes the minimax search value
    int maxval = Integer.MIN_VALUE;
    GameState maxState = state;
    for (GameState s : nextStates) {
      int v = IDS(s);
      if (v > maxval) {
        maxval = v;
        maxState = s;
      }
    }
//    System.err.println("MAXVAL: " + maxval);
//    System.err.println("STATE: " + maxState.toMessage());
    return maxState;
  }

  private int IDS(GameState state) {
    int score = 0;
    for (int depth = 1; depth < 6; depth++)
      score = search(state, 0, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    return score;
  }

  private void insertOrderedNextStates(GameState state, int level, Vector<GameState> nextStates) {
    orderedNextStates
        .computeIfAbsent(stateString(state), k -> new HashMap<>())
        .put(level, nextStates);
  }

  private int search(GameState state, int depth, int maxDepth, int alpha, int beta) {
    if (depth >= maxDepth || state.isEOG())
      return Evaluator.utilityForState(state, me);

    HashMap<Integer, Vector<GameState>> tmp = orderedNextStates.get(stateString(state));
    Vector<GameState> nextStates = null;

    if (tmp != null)
      nextStates = tmp.get(depth);

    if (nextStates == null) {
      nextStates = new Vector<>();
      state.findPossibleMoves(nextStates);
    }

    TreeMap<Integer, GameState> states = new TreeMap<>();
    int score;

    if (state.getNextPlayer() == me) { //maxsearch
      for (int i = nextStates.size() - 1; i >= 0; i--) {
        GameState nextState = nextStates.get(i);
        int v = search(nextState, depth+1, maxDepth, alpha, beta);
        states.put(v, nextState);
        alpha = Math.max(alpha, v);
        if (v >= beta)
          break;
      }
      score = alpha;
    } else { // minsearch
      for (GameState nextState : nextStates) {
        int v = search(nextState, depth+1, maxDepth, alpha, beta);
        states.put(v, nextState);
        beta = Math.min(beta, v);
        if (v <= alpha)
          break;
      }
      score = beta;
    }

    insertOrderedNextStates(state, depth, new Vector<>(states.values()));
    return score;
  }
}
