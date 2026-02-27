package rps.bll.player;

//Project imports
import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

//Java imports
import java.util.ArrayList;

/**
 * Example implementation of a player.
 *
 * @author smsj
 */
public class Player implements IPlayer {

    private String name;
    private PlayerType type;

    /**
     * @param name
     */
    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
    }


    @Override
    public String getPlayerName() {
        return name;
    }


    @Override
    public PlayerType getPlayerType() {
        return type;
    }


    private Move getHumanMove(Result result) {
        if (result.getWinnerPlayer().getPlayerType() == PlayerType.Human)
            return result.getWinnerMove();
        else
            return result.getLoserMove();
    }

    private Move getCounterMove(Move move) {
        switch (move) {
            case Rock: return Move.Paper;
            case Paper: return Move.Scissor;
            case Scissor: return Move.Rock;
            default: return getRandomMove();
        }
    }

    private Move getRandomMove() {
        int nr = new Random().nextInt(3);
        if (nr == 0) return Move.Rock;
        if (nr == 1) return Move.Paper;
        return Move.Scissor;
    }

    /**
     * Decides the next move for the bot...
     * @param state Contains the current game state including historic moves/results
     * @return Next move
     */
    @Override
    public Move doMove(IGameState state) {
        //marvok
        // Hent alle tidligere resultater (historik) og lav en liste
        // Matematik: Vi bruger hele sekvensen {X1, X2, ..., Xn} hvor Xi er human move i runde i
        List<Result> results = new ArrayList<>(state.getHistoricResults());

        // Hvis vi ikke har nok data (mindre end 2 runder) → spil random
        // Matematik: Ingen overgangssandsynligheder kan estimeres med < 2 observationer
        if (results.size() < 2) {
            return getRandomMove();
        }

        // Find spillerens sidste move
        // Matematik: X_n = sidste move
        Result lastResult = results.get(results.size() - 1);
        Move lastOpponentMove = lastResult.getLoserPlayer().getPlayerType() == PlayerType.Human
                ? lastResult.getLoserMove()
                : lastResult.getWinnerMove();

        // Tæl hvor ofte spilleren spiller Rock, Paper, Scissor efter sidste move
        // Matematik: N_{i,j} = antal gange spilleren gik fra move i til move j
        int rockCount = 0;
        int paperCount = 0;
        int scissorCount = 0;

        // Loop igennem historik for at opbygge transition counts
        // Matematik: For alle runder k=1..n-1
        // Hvis X_k == X_n (sidste move), så tæl X_{k+1}
        for (int i = 0; i < results.size() - 1; i++) {

            // X_k = human move i runde i
            Move currentMove = getHumanMove(results.get(i));
            // X_{k+1} = human move i næste runde
            Move nextMove = getHumanMove(results.get(i + 1));

            // Hvis X_k matcher sidste move, tæl hvad spilleren spillede næste gang
            if (currentMove == lastOpponentMove) {
                if (nextMove == Move.Rock) rockCount++;
                if (nextMove == Move.Paper) paperCount++;
                if (nextMove == Move.Scissor) scissorCount++;
            }
        }

        // Matematik: Estimer transition sandsynligheder
        // P(Rock | lastMove) = rockCount / (rockCount + paperCount + scissorCount)
        // P(Paper | lastMove) = paperCount / total
        // P(Scissor | lastMove) = scissorCount / total

        // Find mest sandsynlige næste move
        // Matematik: predictedMove = argmax_j P(X_{n+1} = j | X_n = lastOpponentMove)
        Move predictedMove;

        if (rockCount >= paperCount && rockCount >= scissorCount)
            predictedMove = Move.Rock;
        else if (paperCount >= rockCount && paperCount >= scissorCount)
            predictedMove = Move.Paper;
        else
            predictedMove = Move.Scissor;

        // Returner counter move
        // Matematik: botMove = f(predictedMove) hvor f er counter-funktionen
        // f(Rock) = Paper, f(Paper) = Scissor, f(Scissor) = Rock
        return getCounterMove(predictedMove);
    }
}
