package dev.lyze.gdxtinyvg.commands.paths;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LittleEndianInputStream;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.enums.Range;
import dev.lyze.gdxtinyvg.types.UnitPoint;
import dev.lyze.gdxtinyvg.types.Vector2WithWidth;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.var;

@EqualsAndHashCode
@Getter
public class UnitPathSegment {
    private final UnitPathCommand[] commands;
    private UnitPoint start;

    public UnitPathSegment(int commandCount) {
        commands = new UnitPathCommand[commandCount];
    }

    public UnitPathSegment read(LittleEndianInputStream stream, TinyVG tinyVG) throws IOException {
        start = new UnitPoint(stream, tinyVG.getHeader().getCoordinateRange(), tinyVG.getHeader().getFractionBits());

        for (int i = 0; i < commands.length; i++)
            commands[i] = UnitPathCommand.readCommand(stream, tinyVG);

        return this;
    }

    public Array<Vector2WithWidth> calculatePoints(float lineWidth) {
        var point = start.convert();

        var path = new Array<Vector2WithWidth>();
        for (UnitPathCommand command : commands) {
            command.calculatePoints(point, lineWidth, path);

            // fail save when a segment is only (close -)
            if (path.size == 0)
                continue;

            var lastPoint = path.get(path.size - 1);
            point = lastPoint.getPoint();
            lineWidth = lastPoint.getWidth();
        }

        return path;
    }
}
