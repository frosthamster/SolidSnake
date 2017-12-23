package model.game;

import java.io.Serializable;
import model.creatures.Creature;
import model.creatures.CreatureType;
import model.creatures.CreatureTypeValidator;
import model.creatures.snakes.Snake;
import model.creatures.snakes.SnakeBodyPart;
import model.utils.Direction;
import model.utils.Point;
import java.util.HashMap;
import java.util.Map;

public class GameFrame implements Serializable{

    private final int height;
    private final int width;
    private final Map<Point, CreatureInfo> typeInfos;
    private final Map<Point, CreatureType> types;
    private final int[] scores;

    public GameFrame(int width,
                     int height,
                     Map<Point, Creature> creatures,
                     Snake[] snakes){
        this.width = width;
        this.height = height;
        types = extractTypes(creatures);
        typeInfos = convertICreatureToCreatureInfo(creatures, snakes);
        scores = getScores(snakes);
    }

    private int[] getScores(Snake[] snakes){
        int[] exctracted = new int[snakes.length];
        for (int i = 0; i < snakes.length; i++) {
            exctracted[i] = snakes[i].getScore();
        }
        return exctracted;
    }

    private Map<Point, CreatureType> extractTypes(Map<Point, Creature> creatures){
        Map<Point, CreatureType> typeMap = new HashMap<>();
        for (Point location : creatures.keySet()) {
            Creature creature = creatures.get(location);
            typeMap.put(location, creature.getCreatureType());
        }
        return typeMap;
    }

    private Map<Point, CreatureInfo> convertICreatureToCreatureInfo(Map<Point, Creature> creatures,
                                                                    Snake[] snakes){
        Map<Point, CreatureInfo> textures = new HashMap<>();
        for (Point location : creatures.keySet()) {
            Creature creature = creatures.get(location);
            CreatureInfo texture = new CreatureInfo(creature.getCreatureType(),
                    creature.getCurrentDirection(), seekForSnake(creature, snakes));
            textures.put(location, texture);
        }
        return textures;
    }

    private Integer seekForSnake(Creature creature, Snake[] snakes) throws IllegalStateException {
        CreatureType type = creature.getCreatureType();
        if (!CreatureTypeValidator.isSnake(type)){
            return null;
        }
        SnakeBodyPart bodyPart = (SnakeBodyPart)creature;
        for (int i = 0; i < snakes.length; i++) {
            if(bodyPart.getSnake() == snakes[i])
                return i;
        }
        throw new IllegalStateException("I'm a cat and i walk by myself!" +
                "(Somehow found bodyPart that doesn't belong to any snake)");
    }


    public class CreatureInfo implements Serializable {
        private final CreatureType type;
        private final Direction direction;
        private final Integer playerNumber;

        public CreatureInfo(CreatureType type,
                            Direction direction, Integer playerNumber){
            this.type = type;
            this.direction = direction;
            this.playerNumber = playerNumber;
        }

        public CreatureType getType() {
            return type;
        }
        public Direction getDirection() {
            return direction;
        }

        /**
         * @return Snake number if body part, null otherwise
         */
        public Integer getPlayerNumber() {
            return playerNumber;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Point, CreatureInfo> getCreaturesInfo(){
        return typeInfos;
    }

    public int[] getScores() {
        return scores;
    }

    public Map<Point, CreatureType> getTypes() {
        return types;
    }
}
