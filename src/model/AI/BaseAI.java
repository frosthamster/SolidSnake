package model.AI;

import model.creatures.snakes.Snake;
import model.game.Game;
import model.utils.Direction;

public class BaseAI {
  protected Game game;
  protected Snake snake;

  public BaseAI(Game game, Snake snake) {
    this.game = game;
    this.snake = snake;
  }

  public Direction makeTurn() {
    return Direction.None;
  }
}
