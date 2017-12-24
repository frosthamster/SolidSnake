package model.game;

import model.creatures.Apple;
import model.creatures.Creature;
import model.creatures.CreatureType;
import model.creatures.Mushroom;
import model.creatures.Wall;
import model.creatures.snakes.Snake;
import model.creatures.snakes.SnakeBodyPart;
import model.utils.Direction;
import model.utils.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class Game {
    private Creature[][] field;
    private final Snake[] snakes;
    private int turnNumber = 0;
    private int appleSpawnRate = 20;
    private int appleDeathRate = 50;
    private int mushroomSpawnRate = 40;
    private int mushroomDeathRate = 30;
    private boolean foodSpawnActivated = true;

    public Game(GameSettings settings) throws IllegalArgumentException{
        foodSpawnActivated = settings.isFoodSpawnEnabled();
        if (!foodSpawnActivated){
            appleSpawnRate = 0;
            mushroomSpawnRate = 0;
        }
        if (mushroomDeathRate < 0 || appleDeathRate < 0){
            throw new IllegalArgumentException("How can you die before you were born, Benjamin?" +
                    "(Negative death rates are illegal)");
        }
        if (appleSpawnRate < 0 || mushroomSpawnRate < 0){
            throw new IllegalArgumentException("My name is Doctor. Doctor who?" +
                    " (Negative spawn rates are illegal)");
        }
        snakes = new Snake[settings.getSnakesAmount()];
        appleSpawnRate = settings.getAppleSpawnRate();
        appleDeathRate = settings.getAppleDeathRate();
        mushroomSpawnRate = settings.getMushroomSpawnRate();
        mushroomDeathRate = settings.getMushroomDeathRate();
        CreatureType[][] initialField = settings.getInitialField();
        field = new Creature[initialField[0].length][initialField.length];
        if (initialField == null){
            throw new IllegalArgumentException("You w0t m8? It's a bloody void! (Field was null)");
        }
        int snakeNumber = 0;
        for (int j = 0; j < initialField.length; j++) {
            for (int i = 0; i < initialField[0].length; i++) {
                if (initialField[j][i] == null){
                    continue;
                }
                switch (initialField[j][i]){
                    case Wall:
                        field[i][j] = new Wall(new Point(i, j));
                        break;
                    case Apple:
                        field[i][j] = new Apple(new Point(i, j), 0, appleDeathRate);
                        break;
                    case Mushroom:
                        field[i][j] = new Mushroom(new Point(i, j),
                                0, mushroomDeathRate);
                        break;
                    case SnakeHead:
                        Snake snake = new Snake(new Point(i, j), Direction.None);
                        field[i][j] = snake.getHead();
                        snakes[snakeNumber] = snake;
                        snakeNumber++;
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Access denied!" +
                                " (%s) is not allowed here",
                                initialField[j][i].toString()));
                }
            }
        }
        if (snakeNumber != settings.getSnakesAmount()){
            throw new IllegalArgumentException("snakes! It has to be snakes!" +
                    " (Snake amount is not equal to the amount found on the field");
        }
    }

    public GameFrame makeTurn(Direction[] playerDirection){
        if (playerDirection.length != snakes.length){
            throw new IllegalArgumentException("Learn how to count sheeps " +
                    "and only then move on to snakes" +
                    " (amount of snakes is not equal to amount of given directions)");
        }
        turnNumber++;
        Map<Point, List<Creature>> collisions = makeMoves(playerDirection);
        Map<Point, Creature> survivedCreatures = resolveCollisions(collisions);
        cleanUp();
        makeNewField(survivedCreatures);
        boolean isThereAnySnakeAlive = false;
        for (Snake snake : snakes) {
            isThereAnySnakeAlive = isThereAnySnakeAlive || !snake.isDead();
        }
        if(!isThereAnySnakeAlive) {
            return null;
        }
        return new GameFrame(field.length, field[0].length, survivedCreatures, snakes);
    }

    private void makeNewField(Map<Point, Creature> survivedCreatures)
            throws IndexOutOfBoundsException{
        field = new Creature[field.length][field[0].length];
        for (Point location : survivedCreatures.keySet()) {
            try {
                field[location.getX()][location.getY()] = survivedCreatures.get(location);
            }
            catch (IndexOutOfBoundsException a){
                throw new IndexOutOfBoundsException("I told you, build the bloody walls first!" +
                        "");
            }
        }

        if(foodSpawnActivated && appleSpawnRate != 0 && turnNumber % appleSpawnRate == 0) {
            Point[] apples = Game.generateSafeRandomPoints(field, snakes.length,
                    0, field.length - 1,
                    0, field[0].length - 1,
                    1, 1);
            for (Point appleLocation : apples) {
                if(appleLocation != null){
                    field[appleLocation.getX()][appleLocation.getY()] = new Apple(appleLocation, turnNumber, appleDeathRate);
                }
            }
        }
        if(foodSpawnActivated && mushroomSpawnRate != 0 && turnNumber % mushroomSpawnRate == 0) {
            Point[] mushrooms = Game.generateSafeRandomPoints(field, snakes.length,
                    0, field.length - 1,
                    0, field[0].length - 1,
                    1, 1);
            for (Point mushroomLocation : mushrooms) {
                if(mushroomLocation != null){
                    field[mushroomLocation.getX()][mushroomLocation.getY()] = new Mushroom(mushroomLocation, turnNumber, mushroomDeathRate);
                }
            }
        }
    }

    private void cleanUp() {
        for (Creature[] row : field) {
            for (Creature creature : row) {
                if (creature != null && creature.isDead()) {
                    creature.cleanUp();
                }
            }
        }
    }

    private Map<Point, Creature> resolveCollisions(Map<Point, List<Creature>> collisions)
            throws IllegalStateException{
        Map<Point, Creature> resolved = new HashMap<>();
        for (Point location: collisions.keySet()) {
            List<Creature> collidingCreatures = dropDead(collisions.get(location));
            int length = collidingCreatures.size();
            if(length > 1){
                for (int i = 0; i < length - 1; i++) {
                    for (int j = i + 1; j < length; j++) {
                        collidingCreatures.get(i).interactWith(collidingCreatures.get(j));
                        collidingCreatures.get(j).interactWith(collidingCreatures.get(i));
                    }
                }
            }
            Creature survivedCreature = null;
            for (Creature creature : collidingCreatures) {
                if(creature.isDead()){
                    continue;
                }
                if (survivedCreature != null) {
                    throw new IllegalStateException("Anotha one!" +
                            " Two creatures collided and still alive");
                }
                survivedCreature = creature;
            }
            if (survivedCreature == null) {
                continue;
            }
            resolved.put(location, survivedCreature);
        }
        Map<Point, Creature> survived = new HashMap<>();
        for (Point location : resolved.keySet()) {
            if (!resolved.get(location).isDead()){
                survived.put(location, resolved.get(location));
            }
        }
        return survived;
    }

    private List<Creature> dropDead(List<Creature> creatures) {
        List<Creature> alive = new ArrayList<>();
        for (Creature creature : creatures) {
            if (!creature.isDead()){
                alive.add(creature);
            }
        }
        return alive;
    }

    private Map<Point, List<Creature>> makeMoves(Direction[] playerDirection){
        for (int i = 0; i < snakes.length; i++) {
            snakes[i].setCurrentDirection(playerDirection[i]);
        }
        Map<Point, List<Creature>> collisions =
                new HashMap<>();
        for (Creature[] row : field){
            for (Creature creature : row){
                if (creature != null) {
                    SnakeBodyPart asBodyPart = creature instanceof SnakeBodyPart ? ((SnakeBodyPart) creature) : null;
                    if (asBodyPart != null)
                        continue;
                    creature.makeMove(field, turnNumber);
                    Point location = creature.getLocation();
                    collisions.computeIfAbsent(location, k -> new ArrayList<>());
                    collisions.get(location).add(creature);
                }
            }
        }
        for (Snake snake : snakes) {
            if (snake.isDead())
                continue;
            SnakeBodyPart snakeBodyPart = snake.getHead();
            while (true) {
                snakeBodyPart.makeMove(field, turnNumber);
                Point location = snakeBodyPart.getLocation();
                collisions.computeIfAbsent(location, k -> new ArrayList<>());
                collisions.get(location).add(snakeBodyPart);
                snakeBodyPart = snakeBodyPart.getNextBodyPart();
                if (snakeBodyPart == null) {
                    break;
                }
            }
        }
        return collisions;
    }

    private static List<Point> generatePointForEveryCell(int x1, int x2, int y1, int y2,
                                                               int borderX, int borderY){
        List<Point> points = new LinkedList<>();
        for (int x = x1 + borderX; x < x2 - borderX + 1; x++) {
            for (int y = y1 + borderY; y < y2 - borderY + 1; y++) {
                points.add(new Point(x, y));
            }
        }
        return points;
    }

    private static Point[] generateSafeRandomPoints(Creature[][] field, int amount,
                                                    int x1, int x2, int y1, int y2,
                                                    int borderX, int borderY){
        List<Point> points = generatePointForEveryCell(x1, x2, y1, y2, borderX, borderY);
        Point[] randomPoints = new Point[amount];
        int pointNumber = 0;
        while (true){
            if(pointNumber == amount){
                break;
            }
            if(points.size() == 0){
                break;
            }
            int index = ThreadLocalRandom.current().
                    nextInt(0, points.size());
            Point point = points.get(index);
            if(field[point.getX()][point.getY()] == null) {
                randomPoints[pointNumber] = point;
                pointNumber++;
            }
            points.remove(index);
        }
        return randomPoints;
    }

    public int getWidth() {
        return field.length;
    }

    public int getHeight() {
        return field[0].length;
    }

    public Creature getCreatureAt(Point point) {
        return field[point.getX()][point.getY()];
    }

    public Snake getSnake(int index) {
        return snakes[index];
    }
}
