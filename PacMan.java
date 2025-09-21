import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;
        Block(Image image,int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            // ensure pacman is able to got L & R without hitting a wall
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall:walls) {
                if(collision(this, wall)) { // this being pacman or the ghost
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if(this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if(this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if(this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if(this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private Image cherryImage1;
    private Image cherryImage2;


    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    // Hashets are java collections that store unique objects with no duplicates 
    // Hashets are of type Block 

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman; // single object for pacman avatar 

    Block cherry = null;
    int cherriesLeftThisLevel = 2;
    int cherryTimer = 0;
    int cherryCooldown = 0;
    int cherryAnimTick = 0; // frame counter used to flip between cherry sprites

    List<Point> spawnTiles = new ArrayList<>(); // Each Point stores tile coordinates for all empty floor tiles cherry spirte could appear
    final int FRAMES_PER_SECOND = 20;
    final int CHERRY_DURATION_FRAMES = 30 * FRAMES_PER_SECOND; // 30 seconds visible
    final int CHERRY_COOLDOWN_FRAMES = 10 * FRAMES_PER_SECOND; // 10 second cooldown
    final int CHERRY_POINTS = 100;

    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    boolean pause = false;


    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this); // pacman object takes on properties of keylistener 
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        cherryImage1 = new ImageIcon(getClass().getResource("./cherry.png")).getImage();
        cherryImage2 = new ImageIcon(getClass().getResource("./cherry2.png")).getImage();

        loadMap();

        for(Block ghost: ghosts){
            char newDirection = directions[random.nextInt(4)]; // returns a # between 0 and 3 to give a random direction
            ghost.updateDirection(newDirection);
        }
            
        gameLoop = new Timer(50, this); // pacman object takes on the properties of actionlistener
        gameLoop.start();

    }

    public void loadMap() {
        // Initialise the hashsets 

        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        spawnTiles.clear();

        for(int r = 0; r < rowCount; r++) {
            for(int c = 0; c < columnCount; c++ ) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize; 
                int y = r * tileSize;

                if(tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall); // add wall image to hashset 
                }
                else if(tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost); // add blue ghost image to hashset 

                }
                else if(tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost); // add pink ghost image to hashset 

                }
                else if(tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost); // add orange ghost image to hashset 

                }
                else if(tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost); // add red ghost image to hashset 

                }
                else if(tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    spawnTiles.add(new Point(c,r));
                }
                else if(tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4); // programattically draw the food 
                    foods.add(food);
                    spawnTiles.add(new Point(c,r));
                }
            }
        }
        resetCherries();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // g.fillRect(pacman.x, pacman.y, pacman.width, pacman.height); Builds rectangle in place of avatar 
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost: ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        for(Block wall: walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        
        g.setColor(Color.WHITE);
        for(Block food: foods)
            g.fillRect(food.x, food.y, food.width, food.height);
        
        if(cherry != null && cherryTimer > 0) {
            Image frame = (cherryAnimTick / 10 % 2 == 0) ? cherryImage1 : cherryImage2; // every 10 frames we flip frames 
            g.drawImage(frame, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
        
        g.setFont(new Font("Arial", Font.PLAIN,18));
        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + "Score: " + String.valueOf(score), tileSize/2, tileSize/2);

        }

        if(pause && !gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 24) );
            g.drawString("PAUSED", boardWidth/2 - 40, boardHeight/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for(Block wall:walls) {
            if(collision(pacman,wall)) {
                pacman.x -= pacman.velocityX; // if pacman collides with a wall then take a step back 
                pacman.y -= pacman.velocityY;
                break;  
            }
        }

        for(Block ghost:ghosts) { 
            if(collision(ghost, pacman)) {
                lives -= 1;
                if(lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
            if(ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D')
                ghost.updateDirection('U');
            ghost.x += ghost.velocityX;  
            ghost.y += ghost.velocityY;
            for(Block wall:walls) {
                if(collision(ghost,wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;  
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }

            }  
        }

        Block foodEaten = null;
        for(Block food: foods) {
            if(collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
        handleCherryLifecycle();

        if(foods.isEmpty()) {
            loadMap();
            resetPositions();
        }

    }

    private void handleCherryLifecycle() {
        if(cherry == null && cherriesLeftThisLevel > 0 && cherryCooldown <= 0) {
            spawnCherry();
        }
        if(cherry != null) {
            cherryAnimTick ++;
            if(collision(pacman, cherry)) {
                score += CHERRY_POINTS;
                despawnCherryAndStartCooldown();
            }
            else {
                cherryTimer--;
                if(cherryTimer <= 0)
                    despawnCherryAndStartCooldown();
            } 
        }
        else if(cherryCooldown > 0)
                cherryCooldown--;
    }

    private void spawnCherry() {
        if(spawnTiles.isEmpty()) return;
        final int MAX_TRIES = 40;
        Block candidate = null;

        for(int attempt = 0; attempt < MAX_TRIES; attempt++) {
            Point tile = spawnTiles.get(random.nextInt(spawnTiles.size())); // select a random floor tile 
            int c = tile.x;
            int r = tile.y;

            int x = c * tileSize + (tileSize - 20) / 2; // converts tile coordinates to pixel coordinates and centres cherry sprite
            int y = r * tileSize + (tileSize - 20) / 2;

            Block temp = new Block(cherryImage1, x, y, 20, 20);

            boolean overlaps = collision(temp, pacman); // checks for overlapps between the random floor tile and pacman or ghosts 
            if(!overlaps) {
                for(Block g : ghosts) {
                    if(collision(temp,g)) {overlaps = true; break;}
                }
            }

            if(!overlaps) {
                candidate = temp;
                break;
            }
        }

        if(candidate == null) {
            Point tile = spawnTiles.get(0);
            int x = tile.x * tileSize + (tileSize - 20) / 2;
            int y = tile.y * tileSize + (tileSize - 20) / 2;
        }
        cherry = candidate;
        cherryTimer = CHERRY_DURATION_FRAMES;

    }

    private void despawnCherryAndStartCooldown() {
        cherry = null;
        cherryTimer = 0;
        cherriesLeftThisLevel = Math.max(0,cherriesLeftThisLevel - 1);
        cherryCooldown = CHERRY_COOLDOWN_FRAMES;
        cherryAnimTick = 0;
    }

    private void resetCherries() {
        cherry = null;
        cherryTimer = 0;
        cherriesLeftThisLevel = 2;
        cherryCooldown = CHERRY_COOLDOWN_FRAMES;
        cherryAnimTick = 0;

    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for(Block ghost:ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        cherry = null;
        cherryTimer = 0;
        cherryCooldown = CHERRY_COOLDOWN_FRAMES;
        cherryAnimTick = 0;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // every 50ms actionPerformed is called, 20fps
        // every frame, pacman moves 1/4 of a tile size
        move();
        repaint();
        if(gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            pause = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
            
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(!gameOver) 
                pause = !pause;
            if(pause)
                gameLoop.stop();
            else
                gameLoop.start();
            repaint();;
        }

        if(pacman.direction == 'U')
            pacman.image = pacmanUpImage;
        else if(pacman.direction == 'D')
            pacman.image = pacmanDownImage;
        else if(pacman.direction == 'L')
            pacman.image = pacmanLeftImage;
        else if(pacman.direction == 'R')
            pacman.image = pacmanRightImage;
            
    }

}
