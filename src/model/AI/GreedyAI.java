package model.AI;

import model.creatures.Creature;
import model.creatures.CreatureType;
import model.creatures.snakes.Snake;
import model.game.Game;
import model.utils.Direction;
import model.utils.Point;
import sun.misc.Queue;

public class GreedyAI extends BaseAI {
  private static final Direction[] directionOrder = {
      Direction.Up,
      Direction.Right,
      Direction.Down,
      Direction.Left
  };

  public static boolean isOpposite(Direction d1, Direction d2) {
    for (int i = 0; i < 4; ++i)
      if (directionOrder[i] == d1 && directionOrder[(i + 2) % 4] == d2)
        return true;
    return false;
  }

  private int[][] distance;
  private final int unvisited;
  private final int unreachable;

  public GreedyAI(Game game, Snake snake) {
    super(game, snake);
    distance = new int[game.getWidth()][game.getHeight()];
    unvisited = game.getWidth() * game.getHeight();
    unreachable = unvisited + 1;
  }

  @Override
  public Direction makeTurn() {
    int width = game.getWidth();
    int height = game.getHeight();
    Queue<Point> queue = new Queue<>();
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        Creature creature = game.getCreatureAt(new Point(i, j));
        if (creature == null) {
          distance[i][j] = unvisited;
          continue;
        }
        CreatureType creatureType = creature.getCreatureType();
        if (creatureType == CreatureType.Apple || creatureType == CreatureType.Mushroom) {
          distance[i][j] = 0;
          queue.enqueue(new Point(i, j));
        }
        else
          distance[i][j] = unreachable;
      }
    }
    while (!queue.isEmpty()) {
      Point cur;
      try {
        cur = queue.dequeue();
      } catch (InterruptedException e) {
        break;
      }
      for (Direction dir: Direction.values()) {
        Point next = new Point(cur, dir);
        int x = next.getX();
        int y = next.getY();
        if (next.isInBounds(width, height) && distance[x][y] == unvisited) {
          distance[x][y] = distance[cur.getX()][cur.getY()] + 1;
          queue.enqueue(next);
        }
      }
    }
    Point start = snake.getHead().getLocation();
    Direction best = Direction.None;
    Direction curDir = snake.getCurrentDirection();
    int bestValue = unreachable;
    for (Direction dir: Direction.values()) {
      if (isOpposite(curDir, dir))
        continue;
      Point next = new Point(start, dir);
      int x = next.getX();
      int y = next.getY();
      if (next.isInBounds(width, height) && distance[x][y] < bestValue) {
        best = dir;
        bestValue = distance[x][y];
      }
    }
    return best;
  }
}
