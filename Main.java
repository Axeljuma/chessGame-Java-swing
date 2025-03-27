// Created by Ayush Adhikari
/* This is my first project and it was made using java swing and awt. By no means is this code efficeint or ideal, however it works(at least in my trials) and
that is enough for me.
*/

package chessGame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(612, 626);
        
        ChessBoard board = new ChessBoard();
        frame.add(board);
        
        frame.setVisible(true);
    }
}

class ChessBoard extends JPanel {
    private static final int TILE_SIZE = 75;
    private static final int BOARD_SIZE = 8;
    private Image[][] pieces = new Image[BOARD_SIZE][BOARD_SIZE];
    private String[][] piecesName = new String[8][8];
    private String CurrentPiece = "";
    private boolean whitesTurn = true;
    private boolean whiteInCheck = false;
    private boolean blackInCheck = false;
    private boolean CastlingW = true;
    private boolean CastlingB = true;
    private int[] whiteKingPosition = {7,4};
    private int[] blackKingPosition = {0,4};
    private boolean ignore = false;
    private boolean phantomMove = false;
    private String currentWholePiece;
    private int KPone;
    private int KPtwo;
    private boolean CheckMate = false;
    private int enPassantR = -1;
    private int enPassantC = -1;
    private boolean enPassantActive = false;

    public ChessBoard() {
        loadImages();
        addMouseListener(new MouseAdapter() {
            int preCol;
            int preRow;
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                System.out.println("Clicked on: " + row + ", " + col);
                if (col >= 8 || row >= 8) {
                    System.out.println("Out of bounds");
                    CurrentPiece = "";
                }else if("".equals(CurrentPiece)){
                    CurrentPiece = getPiece(row, col);
                    preCol = col; preRow = row;
                }else if(canMove(CurrentPiece, row, col, preRow, preCol)){
                    if(!enPassantActive){
                        enPassantR = -1; enPassantC = -1;
                    }else{enPassantActive = false;}
                    pieces[row][col] = pieces[preRow][preCol]; piecesName[row][col] = piecesName[preRow][preCol]; 
                    pieces[preRow][preCol] = null; piecesName[preRow][preCol] = null;
                    if(piecesName[row][col].equals("wPawn") || piecesName[row][col].equals("bPawn")){promotion();}
                    if(whiteInCheck || blackInCheck){
                        boolean wPlaceHolder = whiteInCheck;boolean bPlaceHolder = blackInCheck;
                        whitesTurn = !whitesTurn;
                        if(isCheckMated(KPone, KPtwo)){
                            CheckMate = true;
                        }
                        phantomMove = false;
                        whiteInCheck = wPlaceHolder; blackInCheck = bPlaceHolder; whitesTurn = !whitesTurn;
                    }
                    System.out.println("SucessFully Moved");
                    whitesTurn = !whitesTurn;
                    CurrentPiece = "";
                    repaint();
                    if(whiteInCheck && CheckMate){
                        System.out.println("Black wins by checkmate!");
                    }else if(blackInCheck && CheckMate){
                        System.out.println("White wins by checkmate!");
                    } else if(CheckMate){
                        System.out.println("Draw!");
                    }
                }else{
                    System.out.println("Not your turn or cannot move there");
                    CurrentPiece = "";
                }
            }
        });
    }

    private void loadImages() {
        pieces[0][0] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-rook.png")).getImage(); piecesName[0][0] = "bRook";
        pieces[0][1] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-knight.png")).getImage(); piecesName[0][1] = "bKnight";
        pieces[0][2] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-bishop.png")).getImage(); piecesName[0][2] = "bBishop";
        pieces[0][3] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-queen.png")).getImage(); piecesName[0][3] = "bQueen";
        pieces[0][4] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-king.png")).getImage(); piecesName[0][4] = "bKing";
        pieces[0][5] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-bishop.png")).getImage(); piecesName[0][5] = "bBishop";
        pieces[0][6] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-knight.png")).getImage(); piecesName[0][6] = "bKnight";
        pieces[0][7] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-rook.png")).getImage(); piecesName[0][7] = "bRook";
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            pieces[1][i] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-pawn.png")).getImage(); piecesName[1][i] = "bPawn";
            pieces[6][i] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-pawn.png")).getImage(); piecesName[6][i] = "wPawn";
        }
        
        pieces[7][0] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-rook.png")).getImage(); piecesName[7][0] = "wRook";
        pieces[7][1] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-knight.png")).getImage(); piecesName[7][1] = "wKnight";
        pieces[7][2] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-bishop.png")).getImage(); piecesName[7][2] = "wBishop";
        pieces[7][3] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-queen.png")).getImage(); piecesName[7][3] = "wQueen";
        pieces[7][4] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-king.png")).getImage(); piecesName[7][4] = "wKing";
        pieces[7][5] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-bishop.png")).getImage(); piecesName[7][5] = "wBishop";
        pieces[7][6] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-knight.png")).getImage(); piecesName[7][6] = "wKnight";
        pieces[7][7] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-rook.png")).getImage(); piecesName[7][7] = "wRook";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if (pieces[row][col] != null) {
                    g.drawImage(pieces[row][col], col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20, this);
                }
            }
        }
    }
    
    private String getPiece(int preRowss, int preColss){
        if((preRowss >= 0 && preRowss < 8) && (preColss >= 0 && preColss < 8)){
            if(piecesName[preRowss][preColss] != null){
                String subP = piecesName[preRowss][preColss];
                currentWholePiece = subP.substring(1, subP.length());
                return subP;
            }
        }
        return "";
    }

    private boolean canMove(String pieceName, int row, int col, int preRow, int preCol){
        if(whitesTurn){
            if(row == preRow && col == preCol){return false;}
            else if(pieceName.equals("wRook")){ return validMove("Rook", row, col, preRow, preCol);}
            else if(pieceName.equals("wKnight")){ return validMove("Knight", row, col, preRow, preCol);}
            else if(pieceName.equals("wBishop")){ return validMove("Bishop", row, col, preRow, preCol);}
            else if(pieceName.equals("wQueen")){ return validMove("Queen", row, col, preRow, preCol);}
            else if(pieceName.equals("wKing")){ return validMove("King", row, col, preRow, preCol);}
            else if(pieceName.equals("wPawn")){ return validMove("Pawn", row, col, preRow, preCol);}
            else{ return false;}
        }else{
            if(row == preRow && col == preCol){return false;}
            else if(pieceName.equals("bRook")){ return validMove("Rook", row, col, preRow, preCol);}
            else if(pieceName.equals("bKnight")){ return validMove("Knight", row, col, preRow, preCol);}
            else if(pieceName.equals("bBishop")){ return validMove("Bishop", row, col, preRow, preCol);}
            else if(pieceName.equals("bQueen")){ return validMove("Queen", row, col, preRow, preCol);}
            else if(pieceName.equals("bKing")){ return validMove("King", row, col, preRow, preCol);}
            else if(pieceName.equals("bPawn")){ return validMove("Pawn", row, col, preRow, preCol);}
            else{ return false;}
        }
    }

    private boolean validMove(String pieceName, int row, int col, int preRows, int preCols){
        if(!validIndex(row, col)){return false;}
        boolean noRowSwap = preRows <= row;
        boolean noColSwap = preCols <= col;
        char firstIndex = whitesTurn ? 'w' : 'b';
        boolean kingStatus = whitesTurn ? whiteInCheck : blackInCheck;
        switch (pieceName) {  
            case "Rook" -> {
                return Rook(noColSwap, kingStatus, noRowSwap, firstIndex, row, col, preRows, preCols);
            }
            case "Knight" -> {
                return Knight(kingStatus, firstIndex, row, col, preRows, preCols);
            }
            case "Bishop" -> {
                return Bishop(noColSwap, noRowSwap, firstIndex, row, col, preRows, preCols);
            }
            case "Queen" -> {
                return Queen(noColSwap, noRowSwap, firstIndex, row, col, preRows, preCols);
            }
            case "King" -> {
                return King(kingStatus, noColSwap, noRowSwap, firstIndex, pieceName, row, col, preRows, preCols);
            }
            case "Pawn" -> {
                return Pawn(firstIndex, row, col, preRows, preCols);
            }
            default -> {
                return false;
            }
        }
    }
    private boolean Rook(boolean noColSwap, boolean kingStatus, boolean noRowSwap, char firstIndex, int row, int col, int preRows, int preCols){
        int preRowHolder = preRows; int preColHolder = preCols;
        if(row == preRows){
            while(preCols != col){
                preCols += noColSwap ? 1 : -1;
                if(preCols == col && pieces[row][preCols] != null){
                    if(piecesName[row][preCols].charAt(0) == firstIndex && !kingStatus){return false;}
                }
                else if(pieces[row][preCols] != null && preCols != col){return false;}
            }
        } else if (col == preCols){
            while(preRows != row){
                preRows += noRowSwap ? 1 : -1;
                if(preRows == row && pieces[preRows][col] != null){
                    if(piecesName[preRows][col].charAt(0) == firstIndex){return false;}
                }
                else if(pieces[preRows][col] != null && preRows != row){return false;}
            }
        } else{return false;}
        if(!ignore){
            if(!(isTheKingFree(row, col, preRowHolder, preColHolder))){return false;}
        }else{ignore = false;}
        return true;
    }

    private boolean Knight(boolean kingStatus, char firstIndex, int row, int col, int preRows, int preCols){
        if((preCols +2 == col) ||(preCols -2 == col)){
            if((preRows +1 == row) || (preRows -1 ==row)){
                if(piecesName[row][col] != null){
                    if(piecesName[row][col].charAt(0) == firstIndex){ return false;}
                }
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}}else{ignore = false;}
                return true;
            }
        }else if((preRows +2 == row) || (preRows -2 == row)){
            if((preCols +1 == col) || (preCols -1 == col)){
                if(piecesName[row][col] != null){
                    if(piecesName[row][col].charAt(0) == firstIndex){ return false;}
                }
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}}else{ignore = false;}
                return true;
            }
        }
        return false;
    }

    private boolean Bishop(boolean noColSwap, boolean noRowSwap, char firstIndex, int row, int col, int preRows, int preCols){
        int preRowHolder = preRows; int preColHolder = preCols;
        if(Math.abs(preCols - col) == Math.abs(preRows - row)){
            while(preCols != col){
                preCols += noColSwap ? 1 : -1;
                preRows += noRowSwap ? 1 : -1;
                if(preCols == col && pieces[preRows][preCols] != null){
                    if(piecesName[preRows][preCols].charAt(0) == firstIndex){return false;}
                }
                else if(pieces[preRows][preCols] != null){return false;}
            }
            if(!ignore){
                if(!(isTheKingFree(row, col, preRowHolder, preColHolder))){return false;}
            }else{ignore = false;}
            return true;
        }
        return false;
    }

    private boolean Queen(boolean noColSwap, boolean noRowSwap, char firstIndex, int row, int col, int preRows, int preCols){
        int preRowHolder = preRows; int preColHolder = preCols;
        if(Math.abs(preCols - col) == Math.abs(preRows - row)){
            while(preCols != col){
                preCols += noColSwap ? 1 : -1;
                preRows += noRowSwap ? 1 : -1;
                if(preCols == col && pieces[preRows][preCols] != null){
                    if(piecesName[preRows][preCols].charAt(0) == firstIndex){return false;}
                }
                else if(pieces[preRows][preCols] != null){return false;}
            }
            if(!ignore){
                if(!(isTheKingFree(row, col, preRowHolder, preColHolder))){return false;}}else{ignore = false;}
            return true;
        }else if(row == preRows){
            while(preCols != col){
                preCols += noColSwap ? 1 : -1;
                if(preCols == col && pieces[row][preCols] != null){
                    if(piecesName[row][preCols].charAt(0) == firstIndex){return false;}
                }
                else if(pieces[row][preCols] != null && preCols != col){return false;}
            }
        } else if (col == preCols){
            while(preRows != row){
                preRows += noRowSwap ? 1 : -1;
                if(preRows == row && pieces[preRows][col] != null){
                    if(piecesName[preRows][col].charAt(0) == firstIndex){return false;}
                }
                else if(pieces[preRows][col] != null && preRows != row){return false;}
            }
        } else if(true){return false;}
        if(!ignore){
            if(!(isTheKingFree(row, col, preRowHolder, preColHolder))){return false;}
        }else{ignore = false;}
        return true;
    }
    
    private boolean King(boolean kingStatus, boolean noColSwap, boolean noRowSwap, char firstIndex, String pieceName, int row, int col, int preRows, int preCols){
        if(row >= 0 && row < 8 && col >=0 && col < 8){ 
            if(whitesTurn && CastlingW && Math.abs(preCols - col)== 2 && preRows == row){
                return castling(kingStatus, pieceName, row, col, preRows, preCols, noColSwap);
            }else if(!whitesTurn && CastlingB && Math.abs(preCols - col) == 2 && preRows == row){
                return castling(kingStatus, pieceName, row, col, preRows, preCols, noColSwap);
            }
            if(Math.abs(col -preCols) > 1 || Math.abs(row -preRows) > 1){
                return false;
            }else if (Math.abs(col -preCols) == 1 || Math.abs(row -preRows) == 1){
                if(piecesName[row][col] != null){
                    if(piecesName[row][col].charAt(0) == firstIndex){
                        return false;
                    }
                }
                if(!ignore){
                    
                    if(whitesTurn){whiteKingPosition[0] = row; whiteKingPosition[1] = col;} else if(!whitesTurn){blackKingPosition[0] = row; blackKingPosition[1] = col;}
                    boolean checked = isTheKingFree(row, col, preRows, preCols);
                    if(!checked){
                        if(whitesTurn){whiteKingPosition[0] = preRows; whiteKingPosition[1] = preCols;} else if(!whitesTurn){blackKingPosition[0] = preRows; blackKingPosition[1] = preCols;}
                        return false;
                    }
                    if(checked && phantomMove){
                        if(whitesTurn){whiteKingPosition[0] = preRows; whiteKingPosition[1] = preCols;} else if(!whitesTurn){blackKingPosition[0] = preRows; blackKingPosition[1] = preCols;}
                        return true;
                    }
                }else{
                    ignore = false;
                }
                if(whitesTurn && !phantomMove){whiteKingPosition[0] = row; whiteKingPosition[1] = col; CastlingW = false;} else if(!whitesTurn && !phantomMove){blackKingPosition[0] = row; blackKingPosition[1] = col; CastlingB = false;}
                return true;
                
            }return false;
        }return false;
    }  
    
    private boolean Pawn(char firstIndex, int row, int col, int preRows, int preCols){
        int j; int k;
        j = (firstIndex == 'w') ? -2: 2;
        k = (firstIndex == 'w') ? -1: 1; 
        char currentIndex = (firstIndex == 'w') ? 'b' : 'w';
        if (Math.abs(preCols - col) > 1 || Math.abs(preRows-row) > 2) {return false;}
        else if(preRows == 1 || preRows == 6){
            if(preRows + j == row && col == preCols){
                if(piecesName[row][col] != null || piecesName[row - k][col] != null){return false;}
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}}else{ignore = false;}
                enPassantR = row; enPassantC = col; enPassantActive = true;
                return true;
            }else if(preRows + k == row && col == preCols){
                if(piecesName[row][col] != null){return false;}
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                }else{ignore = false;}
                return true;
            }else if(preRows + k == row && Math.abs(preCols - col) == 1){
                if(piecesName[row][col] != null){
                    if(piecesName[row][col].charAt(0) == currentIndex){
                        if(!ignore){
                            if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                        }else{ignore = false;}
                        return true;
                    }
                }return false;
            }else{return false;}
        
        }else{
            if(preRows + k == row && col == preCols){
                if(piecesName[row][col] != null){return false;}
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                }else{ignore = false;}
                return true;
            }else if(enPassantC > -1 && preRows + k == row && Math.abs(preCols - col) == 1 && (preRows ==3 || preRows ==4) && enPassantR == preRows && enPassantC == col){
                if(!ignore){
                    if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                }else{ignore = false;}
                piecesName[row][col] = piecesName[row-k][col]; piecesName[row-k][col] = null;
                pieces[row][col] = pieces[row-k][col]; pieces[row-k][col] = null;
                return true;
            }else if(preRows + k == row && Math.abs(preCols - col) == 1){
                if(piecesName[row][col] != null){
                    if(piecesName[row][col].charAt(0) == currentIndex){
                        if(!ignore){
                            if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                        }else{ignore = false;}
                        return true;
                    }
                }return false;
            }
            return false;
        }
    }
    public void promotion(){
        for (int i = 0; i < 8; i++) {
            if (getPiece(0, i).equals("wPawn")) {
                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                String inputPiece = (String) JOptionPane.showInputDialog(
                    null, 
                    "Promote your Pawn (Queen, Rook, Bishop, Knight):", 
                    "Pawn Promotion", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, 
                    options[0]);
                pieces[0][i] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/white-" + inputPiece.toLowerCase() + ".png")).getImage();
                piecesName[0][i] = "w" + inputPiece;
            }
            if (getPiece(7, i).equals("bPawn")) {{
                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                String inputPiece = (String) JOptionPane.showInputDialog(
                    null, 
                    "Promote your Pawn (Queen, Rook, Bishop, Knight):", 
                    "Pawn Promotion", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, 
                    options[0]);
                pieces[7][i] = new ImageIcon(getClass().getResource("/chessGame/ChessImages/black-" + inputPiece.toLowerCase() + ".png")).getImage();
                piecesName[7][i] = "b" + inputPiece;
                }
    
            }
        }
    }    
    
    public boolean castling(boolean kingStatus, String pieceName, int row, int col, int preRows, int preCols, boolean noColSwap){
        int edge = noColSwap ? 7 : 0;
        int castleBox =  noColSwap ? 5 : 3;
        while(preCols != edge){
            preCols += noColSwap ? 1 : -1;
            String rookPiece = whitesTurn ? "wRook" : "bRook";
            if (preCols == edge){
                if(piecesName[row][edge].equals(rookPiece)){
                    pieces[row][castleBox] = pieces[row][edge];
                    pieces[row][edge]= null;
                    piecesName[row][castleBox] = piecesName[row][edge];
                    piecesName[row][edge] = null;
                    if(whitesTurn){CastlingW = false; System.out.println("White successfully castle.");} else if(!whitesTurn){CastlingB =false; System.out.println("Black successfully castled.");}
                    if(whitesTurn){whiteKingPosition[0] = row; whiteKingPosition[1] = col;} else if(!whitesTurn){blackKingPosition[0] = row; blackKingPosition[1] = col;}
                    if(!ignore && !phantomMove){
                        if(!(isTheKingFree(row, col, preRows, preCols))){return false;}
                    }else{ignore = false;}
                    return true;
                }
            }
            else if(piecesName[row][preCols] != null){
                return false;
            }
        }     
        return false;      
    }
    private boolean validIndex(int rows, int cols){
        if(rows < 0 || rows > 7 || cols < 0 || cols > 7){
            return false;
        }return true;
    }

    private boolean KingisChecked(int KP1, int KP2, int preRows, int preCols){
        boolean reversedR = KP1 > preRows;
        boolean reversedC = KP2 > preCols;
        char firstIndex = whitesTurn ? 'b' : 'w';
        String Rook = firstIndex + "Rook";
        String Bishop = firstIndex + "Bishop";
        String Queen = firstIndex + "Queen";
        if(KP1 == preRows){
            int k = reversedC ? -1 : 1;
            while(KP2 != preCols){
                KP2 += k;
                if(piecesName[KP1][KP2] != null){
                    String pieceName = piecesName[KP1][KP2];
                    if(pieceName.equals(Queen) || pieceName.equals(Rook)){
                        return true;
                    }break;
                }
            }return false;
        }
        else if(KP2 == preCols){
            int k = reversedR ? -1 : 1;
            while(KP1 != preRows){
                KP1 += k;
                if(piecesName[KP1][KP2] != null){
                    String pieceName = piecesName[KP1][KP2];
                    if(pieceName.equals(Queen) || pieceName.equals(Rook)){
                        return true;
                    }break;
                }
            }return false;
        }
        else if(Math.abs(preCols-KP2) == Math.abs(preRows-KP1)){
            for(int j = 1 ; j <8 ; j++){
                if(isNotEmpty(KP1+j, KP2+j)){
                    String pieceName = piecesName[KP1+j][KP2+j];
                    if(pieceName.equals(Queen) || pieceName.equals(Bishop)){
                        return true;
                    } break;
                }
            }
            for(int j = 1 ; j <8 ; j++){
                if(isNotEmpty(KP1+j, KP2-j)){
                    String pieceName = piecesName[KP1+j][KP2-j];
                    if(pieceName.equals(Queen) || pieceName.equals(Bishop)){
                        return true;
                    } break;
                }
            }
            for(int j = 1 ; j <8 ; j++){
                if(isNotEmpty(KP1-j, KP2+j)){
                    String pieceName = piecesName[KP1-j][KP2+j];
                    if(pieceName.equals(Queen) || pieceName.equals(Bishop)){
                        return true;
                    } break;
                }
            }
            for(int j = 1 ; j <8 ; j++){
                if(isNotEmpty(KP1-j, KP2-j)){
                    String pieceName = piecesName[KP1-j][KP2-j];
                    if(pieceName.equals(Queen) || pieceName.equals(Bishop)){
                        return true;
                    } break;
                }
            }
            return false;
        }
        return false;
    }

    private void kingIsChecked(int rows, int cols, int preRows, int preCols){
        ignore = true;
        KPone = whitesTurn ? blackKingPosition[0]: whiteKingPosition[0];
        KPtwo = whitesTurn ? blackKingPosition[1]: whiteKingPosition[1];
        if(validMove(currentWholePiece, rows, cols, KPone, KPtwo)){
            if(whitesTurn){
                System.out.println("Black in Check");
                blackInCheck = true;}
            else if(!whitesTurn){
                System.out.println("White in Check");
                whiteInCheck = true;}
        }else if(KingisChecked(KPone, KPtwo, preRows, preCols)){
            if(!whitesTurn){
                System.out.println("Black in Check");
                blackInCheck = true;}
            else if(whitesTurn){
                System.out.println("White in Check");
                whiteInCheck = true;}
        }
        else{
            ignore = false;
        }  
    }
    private boolean isTheKingFree(int row, int col, int preRows, int preCols){
        if(row >= 0 && row<8 && col >= 0 && col < 8){
            if (!phantomMove) {
                kingIsChecked(row, col, preRows, preCols);
            }//else{phantomMove = false;}
            String placeHolder = piecesName[row][col];
            piecesName[row][col] = piecesName[preRows][preCols];
            piecesName[preRows][preCols] = null;
            boolean isKingFree = kingIsFree();
            piecesName[preRows][preCols] = piecesName[row][col];
            piecesName[row][col] = placeHolder;
            
            return isKingFree;
        }return false;
    }

    private boolean isNotEmpty(int currentRow, int currentCol){
        if(currentRow >= 0 && currentRow < 8 && currentCol >=0 && currentCol <8){
            return piecesName[currentRow][currentCol] != null;
        }
        return false;
    }

    private boolean kingIsFree(){
        int K1 = whitesTurn ? whiteKingPosition[0]: blackKingPosition[0]; int K2 = whitesTurn ? whiteKingPosition[1]: blackKingPosition[1];
        char firstIndex = whitesTurn ? 'b' : 'w';
        String Pawn = firstIndex + "Pawn";
        String Rook = firstIndex + "Rook";
        String Knight = firstIndex + "Knight";
        String Bishop = firstIndex + "Bishop";
        String Queen = firstIndex + "Queen";
        String King = firstIndex + "King";

        int k = whitesTurn ? -1 : 1;
        if((getPiece(K1 + k, K2 + k).equals(Pawn) || getPiece(K1 + k, K2 - k).equals(Pawn))){
            return false;
        } else if (getPiece(K1 - 2, K2 + 1).equals(Knight) || getPiece(K1 - 2, K2 -1).equals(Knight)){
            return false;
        } else if(getPiece(K1 + 1, K2 - 2).equals(Knight) || getPiece(K1 - 1, K2 - 2).equals(Knight)){
            return false;
        } else if (getPiece(K1 + 1, K2 + 2).equals(Knight) || getPiece(K1 -1, K2 + 2).equals(Knight)){
            return false;
        } else if(getPiece(K1 + 2, K2 - 1).equals(Knight) || getPiece(K1 + 2, K2 + 1).equals(Knight)){
            return false;
        }
        for(int i = 1 ; i <8 ; i++){
            if(isNotEmpty(K1+i, K2)){
                if(getPiece(K1+i, K2).equals(Rook) || getPiece(K1+i, K2).equals(Queen)){
                    return false;
                } break;
            }  
        }
        for(int i = 1 ; i <8 ; i++){
            if(isNotEmpty(K1-i, K2)){
                if (getPiece(K1-i, K2).equals(Rook) || getPiece(K1-i, K2).equals(Queen)){
                    return false;
                } break;
            }
        }
        for(int i = 1 ; i <8 ; i++){
            if(isNotEmpty(K1, K2+i)){
                if (getPiece(K1, K2+i).equals(Rook) || getPiece(K1, K2+i).equals(Queen)){
                    return false;
                } break;
            }
        }
        for(int i = 1 ; i <8 ; i++){
            if(isNotEmpty(K1, K2-i)){
                if(getPiece(K1, K2-i).equals(Rook) || getPiece(K1, K2-i).equals(Queen)){
                    return false;
                } break;
            }
        }
        
        for(int j = 1 ; j <8 ; j++){
            if(isNotEmpty(K1+j, K2+j)){
                if(getPiece(K1+j, K2+j).equals(Bishop) || getPiece(K1+j, K2+j).equals(Queen)){
                    return false;
                } break;
            }
        }
        for(int j = 1 ; j <8 ; j++){
            if(isNotEmpty(K1-j, K2-j)){
                if(getPiece(K1-j, K2-j).equals(Bishop) || getPiece(K1-j, K2-j).equals(Queen)){
                    return false;
                } break;
            }
        }
        for(int j = 1 ; j <8 ; j++){
            if(isNotEmpty(K1+j, K2-j)){
                if(getPiece(K1+j, K2-j).equals(Bishop) || getPiece(K1+j, K2-j).equals(Queen)){
                    return false;
                } break;
            }
        }
        for(int j = 1 ; j <8 ; j++){
            if(isNotEmpty(K1-j, K2+j)){
                if(getPiece(K1-j, K2+j).equals(Bishop) || getPiece(K1-j, K2+j).equals(Queen)){
                    return false;
                } break;
            }
        }
        if(getPiece(K1 + 1, K2 + 1).equals(King) || getPiece(K1 + 1, K2).equals(King) || getPiece(K1 + 1, K2 - 1).equals(King) || getPiece(K1, K2 - 1).equals(King)){
            return false;
        } else if (getPiece(K1 -1 , K2 - 1).equals(King) || getPiece(K1 -1, K2).equals(King) || getPiece(K1 - 1, K2 + 1).equals(King) || getPiece(K1, K2 + 1).equals(King)){
            return false;
        }
        if(whitesTurn){whiteInCheck = false;} else if(!whitesTurn){blackInCheck = false;}
        return true;
    }

    private boolean isCheckMated(int KPR, int KPC){
        phantomMove = true;
        if(validMove("King", KPR+1, KPC+1, KPR, KPC)){return false;}
        if(validMove("King", KPR+1, KPC, KPR, KPC)){return false;}
        if(validMove("King", KPR+1, KPC-1, KPR, KPC)){return false;}
        if(validMove("King", KPR, KPC+1, KPR, KPC)){return false;}
        if(validMove("King", KPR, KPC-1, KPR, KPC)){return false;}
        if(validMove("King", KPR-1, KPC-1, KPR, KPC)){return false;}
        if(validMove("King", KPR-1, KPC, KPR, KPC)){return false;}
        if(validMove("King", KPR-1, KPC+1, KPR, KPC)){return false;}
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                String subPiece;
                String Piece = piecesName[i][j];
                char firstIndex = whitesTurn ? 'w' : 'b';
                if(Piece != null){
                    subPiece = Piece.substring(1, Piece.length());
                } else{
                    subPiece = "";
                }
                if(Piece == null){continue;}
                else if(Piece.charAt(0) != firstIndex){continue;}
                else if(subPiece.equals("Pawn")){
                    int k = whitesTurn ? -1 : 1;
                    int p = whitesTurn ? -2 : 2;
                    
                    if(validMove("Pawn", i +k, j, i, j)){return false;}
                    
                    if (validMove("Pawn", i +p, j, i, j)){return false;}
                    
                    if (validMove("Pawn", i +k, j+k, i, j)){return false;}
                    
                    if (validMove("Pawn", i +k, j-k, i, j)){return false;}
                }
                else if(subPiece.equals("Rook") || subPiece.equals("Queen")){
                    for(int u = 1; u < 8; u++){
                        
                        if(validMove("Rook", i+u, j, i, j)){return false;}
                        
                        if(validMove("Rook", i-u, j, i, j)){return false;}
                        
                        if(validMove("Rook", i, j+u, i, j)){return false;}
                        
                        if(validMove("Rook", i, j-u, i, j)){return false;}
                    }
                }
                else if (subPiece.equals("Bishop") || subPiece.equals("Queen")){
                    for(int u = 1; u < 8; u++){
                        
                        if(validMove("Bishop", i+u, j+u, i, j)){return false;}
                        
                        if(validMove("Bishop", i-u, j-u, i, j)){return false;}
                        
                        if(validMove("Bishop", i+u, j-u, i, j)){return false;}
                        
                        if(validMove("Bishop", i-u, j+u, i, j)){return false;}
                    }
                }
                else if (subPiece.equals("Knight")){
                    
                    if(validMove("Knight", i+2, j+1, i, j)){return false;}
                    
                    if(validMove("Knight", i+2, j-1, i, j)){return false;}
                    
                    if(validMove("Knight", i-2, j+1, i, j)){return false;}
                    
                    if(validMove("Knight", i-2, j-1, i, j)){return false;}
                    
                    if(validMove("Knight", i+1, j+2, i, j)){return false;}
                    
                    if(validMove("Knight", i-1, j+2, i, j)){return false;}
                    
                    if(validMove("Knight", i-1, j-2, i, j)){return false;}
                    
                    if(validMove("Knight", i+1, j-2, i, j)){return false;}
                }
            }
        } 
        return true;
    }
}