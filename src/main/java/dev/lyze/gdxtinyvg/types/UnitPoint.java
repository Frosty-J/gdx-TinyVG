package dev.lyze.gdxtinyvg.types;

import com.badlogic.gdx.math.Vector2;
import lombok.*;

/**
 * Points are a X and Y coordinate pair.
 */
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnitPoint {
    /**
     * Horizontal distance of the point to the origin.
     */
    private Unit x;
    /**
     * Vertical distance of the point to the origin.
     */
    private Unit y;

    /**
     * @return Converts x and y coordinates of the Unit into a Vector2. Instantiates
     *         a new vector.
     */
    public Vector2 convert() {
        return convert(new Vector2());
    }

    /**
     * @param storage The vector which gets written to.
     * @return Converts x and y coordinates of the Unit into a Vector2.
     */
    public Vector2 convert(Vector2 storage) {
        return storage.set(x.convert(), y.convert());
    }
}
