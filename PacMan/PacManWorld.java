import greenfoot.*;
import java.util.List;

/**
 * PacManWorld representa o mundo onde o cenário e os objetos (PacMan, fantasmas e pastilhas) irão interagir.
 * 
 * PacManWorld é filha da classe World, logo herda seus métodos, atributos e construtores públicos.
 *
 * @author Raylson, Carlos, Weydson
 * @version 2.0
 */
public class PacManWorld extends World {
    // Estabelece a imagem de fundo padrão do jogo e um inteiro para
    // a contagem de pontos.
    private static GreenfootImage background;
    /** Timer para contar o tempo para libertar fantasmas*/
    private long timerCela = 0l;
    /** Tempo em milisegundos para libertar um fantasma*/
    private int tempoCela = 6000;
    private int points = 0;

    /** 
     * O construtor da classe PacManWorld cria o cenário do mundo, define a
     * velocidade padrão, estabelece a imagem de fundo padrão e cria os objetos para as paredes, fantasmas, pastilhas e o PacMan.
     */
    public PacManWorld(int size) {
        // Cria o cenário do mundo com 57x68 células, cada célula tem
        // um tamanho de size/4 pixels.

        super(57, 68, size/4);


        
        // Define a imagem de fundo do cenário.
        background = new GreenfootImage("background.png");
        setBackground(background);

        // Cria as paredes dentro do mundo.
        populateWall();

        // Cria as pastilhas dentro do mundo.
        //populatePastilha();

        // Cria os fantasmas dentro do mundo.
        //populateFantasma();

        // Adiciona um objeto do tipo PacMan ao mundo, nas células
        // 28 e 47, utilizando um método da classe World.
        //addObject(new PacMan(),28,47);

        
        //Adiciona um objeto do tipo Life ao mundo, nas células
        //3 e 65, 8 e 65, 13 e 65; 
        addObject(new Life(), 3, 65);
        addObject(new Life(), 8, 65);
        addObject(new Life(), 13, 65);
        
        resetar(true);        
    }

    /**
    * Faz o mundo agir: transporta através do portal, calcula os pontos do jogador e verifica se o jogo acabou.
    */
    @Override
    public void act() {
        // Cria um portal, em que os Personagens podem passar para
        // atravessarem ao local oposto no cenário do mundo.
        
        portal();
        liberarFantasma();
        ganharJogo();
    }
    
    public void resetar(boolean resetarPastilhas){
        List<Life> lifes = getObjects(Life.class);
        
        //acabaram as vidas
        if(lifes.size() <= 0){
            GameController.fim();
            return;
        }
        
        //Pega a vida mais à direita
        Life life = lifes.get(0);
        for(Life lf : lifes){
            if(life.getX() > lf.getX()){
                life = lf;
            }
        }
        
        //remove a vida do jogo
        removeObject(life);        
        
        removeObjects(getObjects(Fantasma.class));
        populateFantasma();
        
        List<PacMan> pacmanLista = getObjects(PacMan.class);
        
        if(pacmanLista.size() <= 0){
            addObject(new PacMan(),28,47);
        } else {
            PacMan pacman = pacmanLista.get(0);
            pacman.setLocation(28,47);
            pacman.setImage("images/west_0.png");
            pacman.changeDirection(Personagem.WEST);
        }
        
        repaint();
        
        try{
            Thread.sleep(1000);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        //No caso de ter comido todas as pastilhas
        if(resetarPastilhas){
            populatePastilha();
        }
        
        // Define a velocidade de execucação das ações.
        // Esse método pertence à um classe do pacote do greenfoot,
        // antecipamente importada.
        Greenfoot.setSpeed(39);
        SoundPlayer.stop();
        SoundPlayer.playBackgroundNormal();
        timerCela = System.currentTimeMillis();
    }
    
    /**
     * Método que liberta fantasmas se estiver no tempo.
     */
    private void liberarFantasma(){
        if(System.currentTimeMillis() - timerCela > tempoCela){
            List<Fantasma> fantasmas = getObjects(Fantasma.class);
            for(Fantasma fan : fantasmas){
                if(!fan.liberdade()){
                    fan.setLiberdade(true);
                    break;
                }
            }
            timerCela = System.currentTimeMillis();
        }
    }

    /**
     * Implementa um portal que permite a passagem de personagens para o lado oposto do labirinto.
     */
    private void portal() {
        // Cria uma lista que contém objetos do tipo Personagem,
        // então retorna todos os objetos do tipo Personagem
        // presentes no mundo, utilizando o método getObjects(java.long.class<A>),
        // presente na superclasse World.
        List<Personagem> lista = getObjects(Personagem.class);

        // Para cada personagem na lista, o "for" irá rodar
        // uma vez.
        for (Personagem per : lista) {
            // O Personagem per irá ser mandado como argumento
            // para o método isInsidePortal(Personagem), que
            // irá retornar um boolean.
            // Se retornar verdadeiro, ele irá rodar o "if".
            if (isInsidePortal(per)) {
                // O personagem retorna a sua célula do eixo X
                // do mundo.
                // Se a célula em que o Personagem per estiver
                // for igual a 56, ele retorna verdadeiro e
                // entra no "if".
                if (per.getX() == 56) {
                    // O Personagem per retorna a sua célula
                    // do eixo Y, e então muda sua localização
                    // para a célula 0 do eixo X, mas continua
                    // na mesma célula do eixo Y.
                    per.setLocation(0,per.getY());
                }

                // Caso o Personagem per retornar uma célula
                // do eixo X diferente de 56, então ele irá
                // entrar no "else".
                else {
                    // O Personagem per retorna a sua célula
                    // do eixo Y, e então muda sua localização
                    // para a célula 56 do eixo X, mas continua
                    // na mesma célula do eixo Y.
                    per.setLocation(56,per.getY());
                }
            }
        }
    }

    /**
     * Quando o PacMan come todas as pastilhas do mundo, ele ganha o jogo.
     */
    private void ganharJogo()
    {
        List<Pastilha> pastilhas = getObjects(Pastilha.class);
        
        if(pastilhas.size() <= 0){
            resetar(true);
        }
    }

    /**
     * Verifica se existe algum objeto do tipo Personagem dentro das células do
     * portal (X: 0 ou X: 56 & Y: 29).
     * 
     * @param per um objeto do tipo Personagem.
     * @return true se o Personagem per estiver em determinada célula do eixo X e
     *         false se não estiver.
     */
    private boolean isInsidePortal(Personagem per) {
        // Cria uma variável que recebe o valor da célula do eixo X
        // e outra que recebe o valor da célula do eixo Y, utilizando
        // os métodos getX() e getY().
        int x = per.getX();
        int y = per.getY();

        // Se o valor de y for igual a 29, e o de x for igual a 0
        // ou 56, ele retorna verdadeiro e entra no "if".
        if(y == 29 && (x == 0 || x == 56))
        {
            // Retorna true como retorno.
            return true;
        }

        // Caso y for diferente de 29 ou x for diferente de 0 ou 56,
        // ele retorna false e entra no "else".
        else {
            // Retorna false como retorno.
            return false;
        }
    }

    /**
     * Cria os objetos do tipo Pastilha e coloca-os dentro do mundo do PacManWorld.
     */
    private void populatePastilha() {
        // Um monte de linha de código pra colocar as pastilhas.
        // p* trabalho desgraçado.
        pastilhasVertical(3, 3, 19);
        pastilhasHorizontal(3, 5, 27);
        pastilhasVertical(13, 5, 55);
        pastilhasHorizontal(11, 5, 13);
        pastilhasHorizontal(17, 5, 13);
        pastilhasVertical(25, 5, 13);
        pastilhasHorizontal(11, 15, 55);
        pastilhasVertical(19, 13, 19);
        pastilhasHorizontal(17, 21, 27);
        pastilhasHorizontal(3, 31, 55);
        pastilhasVertical(31, 5, 11);
        pastilhasVertical(43, 5, 11);
        pastilhasVertical(53, 5, 11);
        pastilhasVertical(53, 13, 19);
        pastilhasVertical(43, 13, 55);
        pastilhasHorizontal(17, 45, 53);
        pastilhasVertical(37, 13, 19);
        pastilhasHorizontal(17, 31, 37);        
        pastilhasHorizontal(47, 15, 27);
        pastilhasHorizontal(47, 29, 43);        
        pastilhasHorizontal(41, 31, 55);
        pastilhasVertical(25, 43, 47);
        pastilhasVertical(31, 43, 47);
        pastilhasHorizontal(41, 3, 27);        
        pastilhasVertical(3, 43, 49);
        pastilhasVertical(53, 43, 49);
        addObject(new Pastilha(), 5, 47);
        addObject(new Pastilha(), 7, 47);
        addObject(new Pastilha(), 49, 47);
        addObject(new Pastilha(), 51, 47);
        pastilhasVertical(7, 49, 55);
        pastilhasVertical(19, 49, 55);
        pastilhasVertical(37, 49, 55);
        pastilhasVertical(49, 49, 55);
        addObject(new Pastilha(), 3, 53);
        addObject(new Pastilha(), 5, 53);
        addObject(new Pastilha(), 9, 53);
        addObject(new Pastilha(), 11, 53);
        addObject(new Pastilha(), 45, 53);
        addObject(new Pastilha(), 47, 53);
        addObject(new Pastilha(), 51, 53);
        addObject(new Pastilha(), 53, 53);
        pastilhasVertical(3, 55, 61);
        pastilhasVertical(53, 55, 61);
        pastilhasHorizontal(53, 21, 27);
        pastilhasHorizontal(53, 31, 37);
        addObject(new Pastilha(), 25, 55);
        addObject(new Pastilha(), 25, 57);
        addObject(new Pastilha(), 31, 55);
        addObject(new Pastilha(), 31, 57);
        pastilhasHorizontal(59, 5, 53);        
        populatePastilhaEspecial();
    }
    
    /**
     * Cria as pastilhas especiais.
     */
    private void populatePastilhaEspecial(){
        removeObjects(getObjectsAt(3,7,Pastilha.class));
        removeObjects(getObjectsAt(53,7,Pastilha.class));
        removeObjects(getObjectsAt(3,47,Pastilha.class));
        removeObjects(getObjectsAt(53,47,Pastilha.class));
        addObject(new Pastilha(),3,9);
        addObject(new Pastilha(),53,9);
        addObject(new Pastilha(),5,47);
        addObject(new PastilhaEspecial(),3,7);
        addObject(new PastilhaEspecial(),53,7);
        addObject(new PastilhaEspecial(),3,47);
        addObject(new PastilhaEspecial(),53,47);
    }

    /**
     * Cria linhas de pastilhas verticais.
     * 
     * @param x  célula de eixo X
     * @param y0 célula de eixo Y incial
     * @param y1 célula de eixo Y final
     */
    private void pastilhasVertical(int x, int y0, int y1) {
        // Ele começa no eixo y0 e vai até o eixo y1, pulando de 1 em 1.
        for (int i = y0; i < y1; i+=2) {
            addObject(new Pastilha(), x, i);            
        }
    }

    /**
     * Cria linhas de pastilhas horizontais.
     * 
     * @param y  célula de eixo Y
     * @param x0 célula de eixo X incial
     * @param x1 célula de eixo X final
     */
    private void pastilhasHorizontal(int y, int x0, int x1) {
        // Ele começa no eixo x0 e vai até o eixo x1, pulando de 1 em 1.
        for (int i = x0; i < x1; i+=2) {
            addObject(new Pastilha(), i, y);            
        }
    }
    
    /**   
    * Instancia os Fanstasmas e põe eles no labirinto.
    */
    public void populateFantasma() {
        //TODO: cada um tem suas próprias características.
        // Cria objetos dos Fantasmas vermelho e rosa.
        Fantasma blinky = new Fantasma(Fantasma.RED);
        Fantasma pinky = new Fantasma(Fantasma.PINK);

        // Cria um objeto do Fantasma azul.
        Fantasma inky = new Fantasma(Fantasma.BLUE);

        // Cria um objeto do Fantasma marrom.
        Fantasma clyde = new Fantasma(Fantasma.BROWN);
        
        // Adiciona os objetos dos Fanstasmas vermelho, rosa, azul
        // e marrom ao PacManWorld, nas suas respectivas células no
        // eixo X e Y.
        addObject(pinky,28,30);
        addObject(inky,32,30);
        addObject(clyde,24,30);
        addObject(blinky,28,23);
        
        blinky.setLiberdade(true);
        pinky.setLiberdade(true);
    }
    
    /**
     * Cria a cela, onde os Fanstasmas irão ficar presos no início do jogo.
     */
    private void cela(){
        // Cria um quadrado para a cela dos Fantasmas.
        squareWall(21,25,14,8);
        squareWall(22,26,12,6);
    }
    
    /**
     * Cria as bordas externas, para delimitar o mundo onde o
     * PacMan, os Fantasmas e as Pastilhas podem agir.
     */
    private void bordasExternas(){
        // Cria as paredes verticais e horizontais, informando os
        // eixos X e Y.
        linhaVertical(1,1,19);
        linhaVertical(55,1,19);
        linhaHorizontal(1,1,27);
        linhaHorizontal(1,29,55);
        linhaHorizontal(19,1,11);
        linhaHorizontal(19,45,55);
        linhaVertical(11,19,27);
        linhaVertical(45,19,27);
        linhaHorizontal(27,-1,11);
        linhaHorizontal(27,45,56);
        linhaHorizontal(31,-1,11);
        linhaHorizontal(31,45,56);
        linhaVertical(11,31,39);
        linhaVertical(45,31,39);
        linhaHorizontal(39,1,11);
        linhaHorizontal(39,45,55);
        linhaVertical(1,39,49);
        linhaVertical(55,39,49);
        linhaVertical(1,51,61);
        linhaVertical(55,51,61);
        linhaHorizontal(61,1,55);
    }
    
    /**
     * Cria as paredes que representam as quinas externas do labirinto.
     */
    private void quinasExternas(){
        // Adiciona objetos do tipo Wall nos eixos x e y
        // passados como parâmetros.
        addObject(new Wall(),1,1);
        addObject(new Wall(),55,61);
        addObject(new Wall(),1,61);
        addObject(new Wall(),55,1);
        addObject(new Wall(),1,19);
        addObject(new Wall(),55,19);
        addObject(new Wall(),11,19);
        addObject(new Wall(),45,19);
        addObject(new Wall(),11,27);
        addObject(new Wall(),45,27);        
        addObject(new Wall(),11,31);
        addObject(new Wall(),45,31);
        addObject(new Wall(),11,39);
        addObject(new Wall(),45,39);
        addObject(new Wall(),1,39);
        addObject(new Wall(),55,39);
    }

    /**
     * Cria linhas de paredes verticais.
     * 
     * @param x  célula de eixo X
     * @param y0 célula de eixo Y incial
     * @param y1 célula de eixo Y final
     */
    private void linhaVertical(int x, int y0, int y1) {
        // Usa um for iniciando com o valor do eixo y0 e incrementa
        // 1 até atingir o valor do eixo y1.
        for (int i = y0 + 1; i < y1; i++) {
            // Adiciona objetos do tipo Wall nos eixos x e i.
            addObject(new Wall(), x, i);
        }
    }
    
    /**
     * Cria linhas de paredes horizontais.
     * 
     * @param y célula de eixo Y
     * @param x0 célula de eixo X incial
     * @param x1 célula de eixo X final
     */
    private void linhaHorizontal(int y, int x0, int x1)
    {
        // Usa um for iniciando com o valor do eixo x0 e incrementa
        // 1 até atingir o valor do eixo x1.
        for(int i = x0+1; i < x1; i++)
        {
            // Adiciona objetos do tipo Wall nos eixos i e y.
            addObject(new Wall(), i, y);
        }
    } 

    /**
     * Chama todas as paredes para criar um labirinto no PacManWorld.
     */
    private void populateWall() {
        // Cria as bordas, as quinas, a cela e os quadrados do labirinto.
        bordasExternas();
        quinasExternas();
        cela();
        squareWall(5,5,6,4);
        squareWall(15,5,8,4);
        squareWall(27,0,2,9);
        squareWall(33,5,8,4);
        squareWall(45,5,6,4);
        squareWall(5,13,6,2);
        squareWall(15,13,2,14);
        squareWall(21,13,14,2);
        squareWall(27,13,2,8);
        squareWall(39,13,2,14);
        squareWall(45,13,6,2);
        squareWall(17,19,6,2);
        squareWall(33,19,6,2);
        squareWall(15,31,2,8);
        squareWall(39,31,2,8);
        squareWall(21,37,14,2);
        squareWall(27,37,2,8);
        squareWall(5,43,6,2);
        squareWall(9,43,2,8);
        squareWall(15,43,8,2);
        squareWall(33,43,8,2);
        squareWall(45,43,2,8);
        squareWall(45,43,6,2);
        squareWall(0,49,5,2);
        squareWall(15,49,2,6);
        squareWall(21,49,14,2);
        squareWall(27,49,2,8);
        squareWall(39,49,2,6);
        squareWall(51,49,5,2);
        squareWall(5,55,18,2);
        squareWall(33,55,18,2);
        squareWall(5, 5, 6, 4);
        squareWall(15, 5, 8, 4);
        squareWall(27, 0, 2, 9);
        squareWall(33, 5, 8, 4);
        squareWall(45, 5, 6, 4);
        squareWall(5, 13, 6, 2);
        squareWall(15, 13, 2, 14);
        squareWall(21, 13, 14, 2);
        squareWall(27, 13, 2, 8);
        squareWall(39, 13, 2, 14);
        squareWall(45, 13, 6, 2);
        squareWall(17, 19, 6, 2);
        squareWall(33, 19, 6, 2);
        squareWall(15, 31, 2, 8);
        squareWall(39, 31, 2, 8);
        squareWall(21, 37, 14, 2);
        squareWall(27, 37, 2, 8);
        squareWall(5, 43, 6, 2);
        squareWall(9, 43, 2, 8);
        squareWall(15, 43, 8, 2);
        squareWall(33, 43, 8, 2);
        squareWall(45, 43, 2, 8);
        squareWall(45, 43, 6, 2);
        squareWall(0, 49, 5, 2);
        squareWall(15, 49, 2, 6);
        squareWall(21, 49, 14, 2);
        squareWall(27, 49, 2, 8);
        squareWall(39, 49, 2, 6);
        squareWall(51, 49, 5, 2);
        squareWall(5, 55, 18, 2);
        squareWall(33, 55, 18, 2);
    }

    /**
     * Cria os quadrados, que serão usados para formar o labirinto.
     * 
     * @param x0 eixo X incial
     * @param y0 eixo Y inicial
     * @param width largura do quadrado
     * @param height altura do quadrado
     */
    private void squareWall(int x0, int y0, int width, int height) {
        // Cria duas variáveis que recebem os valores dos eixos X final e
        // Y final.
        int xMax = x0 + width;
        int yMax = y0 + height;

        // Adiciona objetos do tipo Wall para criar as bordas dos quadrados.
        addObject(new Wall(),x0,y0);
        addObject(new Wall(),xMax,y0);
        addObject(new Wall(),x0,yMax);
        addObject(new Wall(),xMax,yMax);
        
        // Usa um laço para percorrer desde o X inicial até o X final.
        for(int i = x0+1; i < xMax; i++)
        {
            // Adiciona objetos do tipo Wall nos lados paralelos do quadrado.
            addObject(new Wall(),i,y0);
            addObject(new Wall(),i,yMax);
        }

        // Usa um laço para percorrer desde o Y inicial até o Y final
        for(int j = y0+1; j < yMax; j++)
        {
            // Adiciona objetos do tipo Wall nos lados paralelos do quadrado.
            addObject(new Wall(),x0,j);
            addObject(new Wall(),xMax,j);
            addObject(new Wall(), x0, y0);
            addObject(new Wall(), xMax, y0);
            addObject(new Wall(), x0, yMax);
            addObject(new Wall(), xMax, yMax);

            // Usa um laço para percorrer desde o X inicial até o X final.
            for (int i = x0 + 1; i < xMax; i++) {
                // Adiciona objetos do tipo Wall nos lados paralelos do quadrado.
                addObject(new Wall(), i, y0);
                addObject(new Wall(), i, yMax);
            }

            // Usa um laço para percorrer desde o Y inicial até o Y final
            for (j = y0+1; j < yMax; j++) {
                // Adiciona objetos do tipo Wall nos lados paralelos do quadrado.
                addObject(new Wall(), x0, j);
                addObject(new Wall(), xMax, j);
            }
        }
    }
}
