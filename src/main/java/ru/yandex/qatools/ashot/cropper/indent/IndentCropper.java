package ru.yandex.qatools.ashot.cropper.indent;

import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.Coords;
import ru.yandex.qatools.ashot.cropper.DefaultCropper;
import ru.yandex.qatools.ashot.util.ImageTool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author <a href="pazone@yandex-team.ru">Pavel Zorin</a>
 */

public class IndentCropper extends DefaultCropper {

    public static final int DEFAULT_INDENT = 50;

    private int indent = DEFAULT_INDENT;

    protected List<IndentFilter> filters = new LinkedList<>();

    public IndentCropper(final int indent) {
        this.indent = indent;
    }

    public IndentCropper() {
        this.indent = DEFAULT_INDENT;
    }

    @Override
    public Screenshot cropScreenshot(BufferedImage image, Set<Coords> coordsToCompare) {
        Coords cropArea = Coords.unity(coordsToCompare);
        Coords indentMask = createIndentMask(cropArea, image);
        Coords coordsWithIndent = applyIndentMAsk(cropArea, indentMask);

        Screenshot cropped = super.cropScreenshot(image, new HashSet<>(asList(coordsWithIndent)));
        cropped.setCoordsToCompare(Coords.setReferenceCoords(coordsWithIndent, coordsToCompare));
        List<NoFilteringArea> noFilteringAreas = createNotFilteringAreas(cropped.getImage(),cropped.getCoordsToCompare());
        cropped.setImage(applyFilters(cropped.getImage()));
        pasteAreasToCompare(cropped.getImage(), noFilteringAreas);
        return cropped;
    }

    protected Coords applyIndentMAsk(Coords origin, Coords mask) {
        Coords spreadCoords = new Coords(0, 0);
        spreadCoords.x = origin.x - mask.x;
        spreadCoords.y = origin.y - mask.y;
        spreadCoords.height = mask.y + origin.height + mask.height;
        spreadCoords.width = mask.x + origin.width + mask.width;
        return spreadCoords;
    }

    protected Coords createIndentMask(Coords originCoords, BufferedImage image) {
        Coords indentMask = new Coords(originCoords);
        indentMask.x = Math.min(indent, originCoords.x);
        indentMask.y = Math.min(indent, originCoords.y);
        indentMask.width = Math.min(indent, image.getWidth() - originCoords.x - originCoords.width);
        indentMask.height = Math.min(indent, image.getHeight() - originCoords.y - originCoords.height);
        return indentMask;
    }

    protected List<NoFilteringArea> createNotFilteringAreas(BufferedImage image, Set<Coords> coordsToCompare) {
        List<NoFilteringArea> noFilteringAreas = new ArrayList<>();
        for (Coords noFilteringCoords : coordsToCompare) {
            noFilteringAreas.add(new NoFilteringArea(image, noFilteringCoords));
        }
        return noFilteringAreas;
    }

    protected void pasteAreasToCompare(BufferedImage filtered, List<NoFilteringArea> noFilteringAreas) {
        Graphics graphics = filtered.getGraphics();
        for (NoFilteringArea noFilteringArea : noFilteringAreas) {
            graphics.drawImage(
                    noFilteringArea.getSubimage(),
                    noFilteringArea.getCoords().x,
                    noFilteringArea.getCoords().y,
                    null);

        }
        graphics.dispose();
    }

    public IndentCropper addIndentFilter(IndentFilter filter) {
        this.filters.add(filter);
        return this;
    }

    protected BufferedImage applyFilters(BufferedImage image) {
        for (IndentFilter filter : filters) {
            image = filter.apply(image);
        }
        return image;
    }

    private static class NoFilteringArea {
        private BufferedImage subimage;
        private Coords coords;

        private NoFilteringArea(BufferedImage origin, Coords noFilterCoords) {
            this.subimage = ImageTool.subImage(origin, noFilterCoords);
            this.coords = noFilterCoords;
        }

        public BufferedImage getSubimage() {
            return subimage;
        }

        public Coords getCoords() {
            return coords;
        }
    }
}
