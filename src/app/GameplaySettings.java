package app;

import java.io.Serializable;
import model.creatures.CreatureType;
import model.game.GameSettings;
import model.utils.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static model.creatures.CreatureType.*;

public class GameplaySettings implements GameSettings, Serializable {

    private final CreatureType[][] field;
    private final boolean spawnEnabled;
    private final int appleSpawnRate;
    private final int appleDeathRate;
    private final int mushroomSpawnRate;
    private final int mushroomDeathRate;
    private final int snakesAmount;


    public GameplaySettings(CreatureType[][] field,
                            boolean spawnEnabled,
                            int appleSpawnRate, int appleDeathRate,
                            int mushroomSpawnRate, int mushroomDeathRate, int snakesAmount) {
        this.field = field;
        this.spawnEnabled = spawnEnabled;
        this.appleSpawnRate = appleSpawnRate;
        this.appleDeathRate = appleDeathRate;
        this.mushroomSpawnRate = mushroomSpawnRate;
        this.mushroomDeathRate = mushroomDeathRate;
        this.snakesAmount = snakesAmount;
    }

    @Override
    public int getAppleSpawnRate() {
        return appleSpawnRate;
    }

    @Override
    public int getAppleDeathRate() {
        return appleDeathRate;
    }

    @Override
    public int getMushroomSpawnRate() {
        return mushroomSpawnRate;
    }

    @Override
    public int getMushroomDeathRate() {
        return mushroomDeathRate;
    }

    @Override
    public boolean isFoodSpawnEnabled() {
        return spawnEnabled;
    }

    @Override
    public CreatureType[][] getInitialField() {
        return field;
    }

    @Override
    public int getSnakesAmount() {
        return snakesAmount;
    }

    @Override
    public int getWidth() {
        return field.length;
    }

    @Override
    public int getHeight() {
        return field[0].length;
    }

    public static CreatureType[][] getRandomField (int width, int height, int snakeCount) throws IllegalArgumentException{
        CreatureType[][] field = new CreatureType[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                    field[j][i] = Wall;
                }
            }
        }
      
        Point[] snakesLocations = GameplaySettings.generateSafeRandomPoints(field,
                snakeCount,0, field.length - 1,
                0, field[0].length - 1, 2, 2);
        for (int i = 0; i < snakeCount; i++) {
            if (snakesLocations[i] == null) {
                throw new IllegalArgumentException("The world is too small for both of us!" +
                        "(Field too small - could not spawn a snake)");
            }
            field[snakesLocations[i].getX()][snakesLocations[i].getY()] = SnakeHead;
        }

        Point[] applesLocations = GameplaySettings.generateSafeRandomPoints(field,
                snakeCount, 0, field.length - 1,
                0, field[0].length - 1, 1, 1);
        for (int i = 0; i < snakeCount; i++) {
            if(applesLocations[i] != null){
                field[applesLocations[i].getX()][applesLocations[i].getY()] = Apple;
            }
        }
        return field;
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

    private static Point[] generateSafeRandomPoints(CreatureType[][] field, int amount,
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
}