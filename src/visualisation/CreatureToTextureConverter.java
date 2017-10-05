package visualisation;

import models.CreatureType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CreatureToTextureConverter {
    public static final Map<CreatureType, Function<Boolean, TextureType>> converters = new HashMap<>(){
    {
      put(CreatureType.SimpleSnake,
              b -> b ? TextureType.SimpleSnakeBodyPart : TextureType.SimpleSnakeHead);
    }
    };
}
