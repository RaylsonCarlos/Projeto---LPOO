import greenfoot.*;
import java.util.List;
import java.util.Iterator;

/**
 * A classe PacMan representa o personagem do usuário no jogo, controlado pelo teclado
 * 
 * @author Raylson, Carlos, Weydson
 * @version 2.0
 */
public class PacMan extends Personagem {
    /**Variável para controlar o deslocamento no array de sprites.
     */
    private int offset = 0;
    /**Array para controlar a ordem de exibição dos sprites.
     */
    private final static int[] animationOffset = { 0, 1, 2, 1 };
    /**Armazena a quantidade de turnos que já se passaram desde que o jogador tentou mudar de direção.
     */
    private int contadorDirection = 0;
    /**Controla em até quantos turnos o comando do jogador de mudar de direção será executado (se possível).
     * Valores maiores suavizam a tomada de direção do personagem, valores menores diminuem a janela de tempo para o jogador apertar das teclas.
     */
    private static final int delayDirection = 6;
    /**Armazena o última direção escolhida pelo jogador
     */
    private int possibleDirection = Personagem.WEST;

    /**Array dos sprites do pac-man.
     */

    private static GreenfootImage[] spritesNORTH;
    private static GreenfootImage[] spritesSOUTH;
    private static GreenfootImage[] spritesEAST;
    private static GreenfootImage[] spritesWEST;

    /**
     * Inicializa o pac-man com velocidade 3, e direção WEST
     */
    public PacMan() {
        super(3);
        spritesNORTH = new GreenfootImage[] 
        {   new GreenfootImage("north_0.png"),
            new GreenfootImage("north_1.png"), 
            new GreenfootImage("north_2.png") };
        spritesSOUTH = new GreenfootImage[] 
        {   new GreenfootImage("south_0.png"),
            new GreenfootImage("south_1.png"), 
            new GreenfootImage("south_2.png") };
        spritesEAST = new GreenfootImage[] 
        {   new GreenfootImage("east_0.png"),
            new GreenfootImage("east_1.png"), 
            new GreenfootImage("east_2.png") };
        changeDirection(Personagem.WEST);
        spritesWEST = new GreenfootImage[] 
        {   new GreenfootImage("west_0.png"),
            new GreenfootImage("west_1.png"), 
            new GreenfootImage("west_2.png") };
        setImage(spritesWEST[1]);
    }

    /**Consome os objetos tipo {@link Pastilha} que estejam num raio de 1 célula
     * return true se o pac-man encontrou alguma comida
     */
    private int foundFood() {
        int points = 0;
        List<Pastilha> food = getObjectsInRange(1, Pastilha.class);
        if (food.size() > 0) {
            Iterator it = food.iterator();
            while (it.hasNext()) {                
                if(it.next() instanceof PastilhaEspecial){
                    points += 50;
                    foundPastilhaEspecial();
                } else {
                    points += 10;
                }                
            }
            getWorld().removeObjects(food);           
        }
       return points;
    }
    
    /**
     * Método chamado quando o pacman encontra uma pastilha especial
     */
    private void foundPastilhaEspecial(){
        List<Fantasma> fantasmas = getWorld().getObjects(Fantasma.class);
        for(Fantasma fan : fantasmas){
            fan.efeitoPastilha();
        }
        SoundPlayer.stop();
        SoundPlayer.playBackgroundFrightened();
    }
    

    /**
     * Verifica o teclado em busca de direções para cima, baixo, esquerda ou direita.
     */
    private void verificarTeclado() {
        String key = Greenfoot.getKey();
        if (key != null) {
            offset = 1;
            if (key.equals("up")) {
                possibleDirection = Personagem.NORTH;
                contadorDirection = 0;
            }
            if (key.equals("down")) {
                possibleDirection = Personagem.SOUTH;
                contadorDirection = 0;
            }
            if (key.equals("right")) {
                possibleDirection = Personagem.EAST;
                contadorDirection = 0;
            }
            if (key.equals("left")) {
                possibleDirection = Personagem.WEST;
                contadorDirection = 0;
            }
        }
    }

    /**
     * Faz o pac-man agir: consome objetos pastilha, aplica efeito sonoro, verifica mudança de direção, move e muda os sprites do personagem.
     */
    @Override
    public void act() {
        int points = foundFood();
        GameController.score(points);
        if (points > 0) {
            SoundPlayer.playEffectPillEaten();
        }

        verificarTeclado();

        if (contadorDirection < delayDirection) {
            if (canMove(possibleDirection)) {
                changeDirection(possibleDirection);
            } else {
                contadorDirection++;
            }
        }

        if (timeToChangeSprite()) {
            switch (getDirection()) {
                case Personagem.NORTH:
                setImage(spritesNORTH[animationOffset[offset % 4]]);
                break;
                case Personagem.SOUTH:
                setImage(spritesSOUTH[animationOffset[offset % 4]]);
                break;
                case Personagem.EAST:
                setImage(spritesEAST[animationOffset[offset % 4]]);
                break;
                case Personagem.WEST:
                setImage(spritesWEST[animationOffset[offset % 4]]);
                break;
            }
            offset++;
        }

        super.act();
    }
}
