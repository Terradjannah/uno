package com.bot.unobot.gameengine;

import com.bot.unobot.card.*;
import com.bot.unobot.player.Player;

/**
 * Undetermined Ordinary Card State
 */
public class UndeterminedOrdinaryCardState implements State {

    //Variables
    Color currentColor;
    String display;
    Player currentPlayer;
    GameMaster gameMaster;

    /**
     * Undetermined Ordinary Card State Constructor
     */
    public UndeterminedOrdinaryCardState ( ){}

    public UndeterminedOrdinaryCardState ( Color color){
        this.currentColor = color;
        this.display = "";
        this.currentPlayer = null;
    }

    public UndeterminedOrdinaryCardState ( Color color, GameMaster gameMaster){
        this.currentColor = color;
        this.gameMaster=gameMaster;
        this.display = "";
        this.currentPlayer = null;
    }

    /**
     * Get Current Player
     * It returns the current player's ID playing the game.
     * @return current player's ID
     */
    @Override
    public String getCurrentPlayer() {
        this.currentPlayer = gameMaster.players.get(gameMaster.currentTurn%gameMaster.playerSize); // mungkin ini nanti masih diedit lagi, apakah di - incrementnya di sini atau di engine
        this.display =
                "Selamat! Kamu mendapatkan rejeki!\n" +
                        "\n" +
                        "Salah satu teman kamu barusan ngeluarin kartu reverse atau skip\n" +
                        "\n" +
                        "Kini giliran Anda untuk bermain \n" +
                        "\n" +
                        "Kartu Anda adalah sebagai berikut:\n" +
                        "\n"+
                        this.currentPlayer.showsPlayersCards();
        return this.currentPlayer.getId();
    }

    /**
     * Card Checking
     * It overrides the card Checking method in the State Interface.
     * It checks the card the player select with the previous card in the stack.
     * If the card has the same color or number, it will be accepted. If not, an
     * error message will be returned.
     * @param userInput
     */
    @Override
    public void cardChecking(String userInput) {
        String[] temp = userInput.split(" ");
        String cardName =  temp[0];
        Color cardColor = Color.SPECIAL; // TODO: covert user input string to Color enum
        Color lastColor = gameMaster.toBeReusedCardStack.peek().getColor();
        if (cardColor.equals(lastColor)){
            acceptUsersCard(cardName, cardColor);
            this.display = "Nice Move !!!!\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    endTurn();
        }
        else{
            this.display ="Maaf, Kartu yang Anda keluarkan tidak cocok\n" +
                    "\n" +
                    "Silahkan ketik .ambil untuk mengambil dari deck";
        }
    }

    /**
     * Accept User Card
     *
     * @param cardName
     * @param cardColor
     */
    @Override
    public void acceptUsersCard(String cardName, Color cardColor) {
        Card removalTarget = null;
        for (Card cards : this.currentPlayer.getCardsCollection()){
            if (cards.getColor().equals(cardColor) && cards.getName().equals(cardName)){
                removalTarget = cards;
            }
        }
        this.gameMaster.toBeReusedCardStack.push(removalTarget);
        this.currentPlayer.getCardsCollection().remove(removalTarget);
    }

    /**
     * Take Another Card
     * It adds a new card from the card collection to the player's card collection.
     * Then it ends the player's turn directly after taking the card.
     */
    @Override
    public void takeAnotherCard() {
        this.currentPlayer.getCardsCollection().add(this.gameMaster.cardStack.pop());
//        this.currentPlayer.getCardsCollection().add(this.gameMaster.cardStack.get(this.gameMaster.cardStack.size()-1));
//        this.gameMaster.cardStack.remove(this.gameMaster.cardStack.get(this.gameMaster.cardStack.size()-1));
        this.display = endTurn();
    }

    /**
     * End Turn
     * It returns a message saying that the player's turn has finished.
     * @return
     */
    @Override
    public String endTurn(){
        return "Giliran Anda sudah selesai!"+" \n"+
                "Tunggu giliran selanjutnya ya :) !";
    }

    @Override
    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    @Override
    public void update() {
        Card cardOnTopOfStack = this.gameMaster.toBeReusedCardStack.peek();
        if (cardOnTopOfStack instanceof OrdinaryCard){
            this.gameMaster.currentState = this.gameMaster.ordinaryCardState;
        }else if (cardOnTopOfStack instanceof PlusCard){
            this.gameMaster.currentState =  this.gameMaster.plusCardState;
        }else if (cardOnTopOfStack instanceof ReverseCard){
            this.gameMaster.currentState = this.gameMaster.reverseCardState;
        }else if (cardOnTopOfStack instanceof SkipCard){
            this.gameMaster.currentState = this.gameMaster.skipCardState;
        }
        this.gameMaster.currentTurn+=1;


    }
}
