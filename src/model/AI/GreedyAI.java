package model.AI;

import model.creatures.Creature;
import model.creatures.CreatureType;
import model.creatures.snakes.Snake;
import model.game.Game;
import model.utils.Direction;
import model.utils.Point;
import sun.misc.Queue;

public class GreedyAI extends BaseAI{
  private int[][] distance;
  private final int unvisited;
  private final int unreachable;

  public GreedyAI(Game game, Snake snake) {
    super(game, snake);
    distance = new int[game.getWidth()][game.getHeight()];
    unvisited = game.getHeight() * game.getHeight();
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
    int best_value = unreachable;
    for (Direction dir: Direction.values()) {
      Point next = new Point(start, dir);
      int x = next.getX();
      int y = next.getY();
      if (next.isInBounds(width, height) && distance[x][y] < best_value) {
        best = dir;
        best_value = distance[x][y];
      }
    }
    return best;
  }
}
