package dev.lyze.gdxtinyvg.commands;

import com.badlogic.gdx.utils.LittleEndianInputStream;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.commands.headers.OutlineFillHeader;
import dev.lyze.gdxtinyvg.drawers.TinyVGShapeDrawer;
import dev.lyze.gdxtinyvg.drawers.chaches.TinyVGDrawerCache;
import dev.lyze.gdxtinyvg.enums.CommandType;
import dev.lyze.gdxtinyvg.enums.StyleType;
import dev.lyze.gdxtinyvg.types.UnitPoint;
import java.io.IOException;
import sun.security.krb5.internal.crypto.RsaMd5CksumType;

/**
 * Fills a polygon and draws an outline at the same time.
 */
public class OutlineFillPolygonCommand extends Command {
    private final TinyVGDrawerCache cache;
    private OutlineFillHeader<UnitPoint> header;

    public OutlineFillPolygonCommand(TinyVG tinyVG) {
        super(CommandType.OUTLINE_FILL_POLYGON, tinyVG);

        cache = new TinyVGDrawerCache(getTinyVG());
    }

    @Override
    public void read(LittleEndianInputStream stream, StyleType primaryStyleType) throws IOException {
        header = new OutlineFillHeader<>(UnitPoint.class).read(stream, primaryStyleType, getTinyVG());

        onPropertiesChanged();
    }

    @Override
    public void draw(TinyVGShapeDrawer drawer, Viewport viewport) {
        drawer.setStyle(header.getPrimaryStyle(), viewport);
        cache.filledPolygon(drawer);

        drawer.setStyle(header.getSecondaryStyle(), viewport);
        cache.path(drawer, header.getLineWidth(), false);
    }

    @Override
    public void onPropertiesChanged() {
        cache.calculateVertices(header.getData());
        cache.calculateTriangles();
    }
}
