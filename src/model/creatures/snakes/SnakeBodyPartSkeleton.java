package model.creatures.snakes;

import model.creatures.Creature;
import model.creatures.CreatureType;
import model.utils.Direction;
import model.utils.Point;
import java.util.HashMap;
import java.util.Map;

public class SnakeBodyPartSkeleton implements SnakeBodyPart {
    public SnakeBodyPartSkeleton(boolean isHead, Direction direction, Point location, Snake snake) {
        isDead = false;
        this.isHead = isHead;
        currentDirection = direction;
        previousDirection = Direction.None;
        this.location = location;
        this.snake = snake;
    }

    @Override
    public void makeMove(Creature[][] field, int currentTurn) {
        if(precedingBodyPart != null && precedingBodyPart.isDead()){
            isDead = true;
        }
        location = new Point(location, currentDirection);
        if(!isHead){
            previousDirection = currentDirection;
            currentDirection = precedingBodyPart.getPreviousDirection();
        }
    }

    private boolean isDead;
    @Override
    public boolean isDead(){
        return isDead;
    }


    @Override
    public void interactWith(Creature otherCreature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanUp() throws UnsupportedOperationException{
        if(!isDead){
            throw new UnsupportedOperationException("You can't dump the body before killing it!");
        }
        deattachNextBodyPart();
        if (precedingBodyPart != null) {
            precedingBodyPart.deattachNextBodyPart();
        }
    }

    @Override
    public void setIsDead(){
        isDead = true;
        if(nextBodyPart != null){
            nextBodyPart.setIsDead();
        }
    }


    private final boolean isHead;

    @Override
    public boolean isHead(){
        return isHead;
    }


    private Point location;

    @Override
    public Point getLocation(){
        return location;
    }

    public void setLocation(Point location){
        this.location = location;
    }

    private Direction previousDirection;

    @Override
    public Direction getPreviousDirection() {
        return previousDirection;
    }

    private Direction currentDirection;

    @Override
    public Direction getCurrentDirection(){
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        if(!isHead)
            return;
        if(currentDirection != ILLEGAL_TURN.get(this.currentDirection) && currentDirection != Direction.None){
            previousDirection = currentDirection;
            this.currentDirection = currentDirection;
        }
    }

    private static final Map<Direction, Direction> ILLEGAL_TURN = new HashMap<Direction, Direction>(){
        {
            put(Direction.Up, Direction.Down);
            put(Direction.Down, Direction.Up);
            put(Direction.Left, Direction.Right);
            put(Direction.Right, Direction.Left);
        }
    };

    @Override
    public CreatureType getCreatureType() {
        return null;
    }


    private final Snake snake;

    @Override
    public Snake getSnake(){
        return snake;
    }


    private SnakeBodyPart nextBodyPart = null;

    @Override
    public SnakeBodyPart getNextBodyPart(){
        return nextBodyPart;
    }

    private SnakeBodyPart precedingBodyPart = null;

    @Override
    public SnakeBodyPart getPrecedingBodyPart(){
        return precedingBodyPart;
    }


    @Override
    public void attachNewBodyPart(SnakeBodyPart bodyPart){
        if (nextBodyPart != null) {
            nextBodyPart.attachNewBodyPart(bodyPart);
        } else {
            nextBodyPart = bodyPart;
            bodyPart.attachToPrecedingBodyPart(this);
        }
    }

    @Override
    public void attachToPrecedingBodyPart(SnakeBodyPart bodyPart){
        if(isHead || bodyPart.getNextBodyPart() == null){
            throw new UnsupportedOperationException();
        }
        precedingBodyPart = bodyPart;
    }

    @Override
    public void deattachNextBodyPart() {
        if (nextBodyPart == null) {
            return;
        }
        SnakeBodyPartSkeleton asSkeleton = nextBodyPart.getSkeleton();
        asSkeleton.deattachFromPrecedingBodyPart();
        nextBodyPart = null;
    }

    @Override
    public SnakeBodyPartSkeleton getSkeleton() {
        return this;
    }

    private void deattachFromPrecedingBodyPart() {
        isDead = true;
        precedingBodyPart = null;
    }

}
