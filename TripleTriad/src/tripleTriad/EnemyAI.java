package tripleTriad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnemyAI {
    public int depth;
    private final long TIME_LIMIT_MS = 15000;
    private int num_moves = 0;
    private int num_pruned = 0;

    public EnemyAI(int depth){
        this.depth = depth;
    }

    public Move findMove(Board board, int depth){
        long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        List<Move> moves = board.getMoves();
        //Board newBoard = new Board(board);
        //int dynamicDepth = Math.min(1 + board.turns / 3, depth);
        int dynamicDepth = board.turns < 4 ? 0 : depth;
        //int dynamicDepth = depth;

        // Sort from moves with cards of highest level to lowest
        moves.sort(Comparator.comparingInt((Move move) -> move.card.getCardLevel()).reversed());
        //System.out.println("Move list size: " + moves.size());

//        System.out.println("Cards in deck: " + board.activeDeck.getCards().size());
//        System.out.println("Empty slots: " + (9- board.activeSlots.size()));
//        System.out.println("Expected max moves: " + board.activeDeck.getCards().size() * (9 - board.activeSlots.size()));


        for(Move move: moves){
            num_moves++;
            //System.out.println("num moves: " + num_moves + " depth: " + depth + " activeDeck: " + board.activeDeck.deckColor);
            //System.out.println("MOVE -- Card: " + move.card.getCardName() + " Position: " + move.slot.getPosition());
            Board newBoard = new Board(board);
            GridSlot moveSlot = null;
            for(GridSlot slot: newBoard.getGridSlots()){
                if(slot.getPosition() == move.slot.getPosition()){
                    moveSlot = slot;
                }
            }
            newBoard.playCard(move.card, moveSlot);
//            System.out.println("Cards in deck: " + newBoard.activeDeck.getCards().size());
//            System.out.println("Empty slots: " + (9- newBoard.activeSlots.size()));
//            System.out.println("Expected max moves: " + newBoard.activeDeck.getCards().size() * (9 - newBoard.activeSlots.size()));
            boolean isMaximizer = board.currentColor != Card.Color.BLUE;
            int score = minimax(newBoard, dynamicDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizer, startTime);
            //System.out.println("Score= " + score);
            if(score > bestScore){
                bestScore = score;
                bestMove = move;
            }
        }
        //System.out.println("Best score: " + bestScore);
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        System.out.println("Duration of Move: " + duration + " seconds");
        System.out.println("Number of pruned moves: " + num_pruned);
        return bestMove;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer, long startTime){
        if (depth == 0 || board.isGameOver()) {
            return board.heuristic();
        }

        if (System.currentTimeMillis() - startTime >= TIME_LIMIT_MS) {
            System.out.println("Timer expired: " + (System.currentTimeMillis() - startTime) / 1000 + "sec");
            return board.heuristic();
        }

        List<Move> moves = board.getMoves();
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            // Sort from moves with cards of highest level to lowest
            moves.sort(Comparator.comparingInt((Move move) -> move.card.getCardLevel()).reversed());
            //System.out.println("Move list size: " + moves.size());

            for (Move move : moves) {
                num_moves++;
                //System.out.println("num moves: " + num_moves + " depth: " + depth + " activeDeck: " + board.activeDeck.deckColor);
                //System.out.println("Possible Player Move -- Depth: " + depth + " Card: " + move.card.getCardName() + " Position: " + move.slot.getPosition());
                Board newBoard = new Board(board);
                GridSlot moveSlot = null;
                for(GridSlot slot: newBoard.getGridSlots()){
                    if(slot.getPosition() == move.slot.getPosition()){
                        moveSlot = slot;
                    }
                }
                newBoard.playCard(move.card, moveSlot);
//                System.out.println("Cards in deck: " + newBoard.activeDeck.getCards().size());
//                System.out.println("Empty slots: " + (9- newBoard.activeSlots.size()));
//                System.out.println("Expected max moves: " + newBoard.activeDeck.getCards().size() * (9 - newBoard.activeSlots.size()));
                int eval = minimax(newBoard, depth - 1, alpha, beta, false, startTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                //System.out.println("maxEval = " + maxEval + " beta = " + beta);
                if (alpha >= beta){
                    System.out.println("break occurred");
                    num_pruned++;
                    return maxEval;
                }
                //alpha = Math.max(alpha, maxEval);
                //System.out.println("alpha = " + alpha + " beta = " + beta);
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;

            //moves.sort(Comparator.comparingInt(move -> move.card.getCardLevel()));
            moves.sort(Comparator.comparingInt((Move move) -> move.card.getCardLevel()).reversed());
            //System.out.println("Move list size: " + moves.size());

            for (Move move : moves) {
                num_moves++;
                //System.out.println("num moves: " + num_moves + " depth: " + depth + " activeDeck: " + board.activeDeck.deckColor);
                //System.out.println("Possible Enemy Move -- Depth: " + depth + " Card: " + move.card.getCardName() + " Position: " + move.slot.getPosition());
                Board newBoard = new Board(board);
                GridSlot moveSlot = null;
                for(GridSlot slot: newBoard.getGridSlots()){
                    if(slot.getPosition() == move.slot.getPosition()){
                        moveSlot = slot;
                    }
                }
                newBoard.playCard(move.card, moveSlot);
//                System.out.println("Cards in deck: " + newBoard.activeDeck.getCards().size());
//                System.out.println("Empty slots: " + (9- newBoard.activeSlots.size()));
//                System.out.println("Expected max moves: " + newBoard.activeDeck.getCards().size() * (9 - newBoard.activeSlots.size()));
                int eval = minimax(newBoard, depth - 1, alpha, beta, true, startTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                //System.out.println("minEval = " + minEval + " alpha = " + alpha);
                if (beta <= alpha) {
                    System.out.println("break occurred");
                    num_pruned++;
                    return minEval;
                }
                //beta = Math.min(beta, minEval);
                //System.out.println("alpha = " + alpha + " beta = " + beta);
            }
            return minEval;
        }
    }

    public Move findMoveIDS(Board board){

        Move bestMove = null;
        int maxDepth = this.depth;

        for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) {
            System.out.println("Searching at depth: " + currentDepth);
            bestMove = findMove(board, currentDepth);
        }

        return bestMove;
    }
}
