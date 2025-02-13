package dev.lyze.gdxtinyvg.styles;

import com.badlogic.gdx.utils.LittleEndianInputStream;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.drawers.GradientShapeDrawer;
import dev.lyze.gdxtinyvg.enums.Range;
import java.io.IOException;
import lombok.Getter;

public abstract class Style {
    @Getter private final TinyVG tinyVG;

    public Style(TinyVG tinyVG) {
        this.tinyVG = tinyVG;
    }

    public abstract void read(LittleEndianInputStream stream, Range range, int fractionBits) throws IOException;

    public abstract void start(GradientShapeDrawer drawer, Viewport viewport);

    public void end(GradientShapeDrawer drawer, Viewport viewport) {
        drawer.getBatch().flush();
    }
}
