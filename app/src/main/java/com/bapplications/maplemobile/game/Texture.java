package com.bapplications.maplemobile.game;

import android.graphics.Bitmap;
import android.util.Range;

import com.bapplications.maplemobile.opengl.GameGLRenderer;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

public class Texture {

    private Point dimensions;
    private NXBitmapNode bitmapNode;
    private Point origin;
    public Texture(NXNode src) {
        if (src instanceof NXBitmapNode) {
            origin = ((NXPointNode) src.getChild("origin")).getPoint();
            bitmapNode = (NXBitmapNode) src;
            dimensions = new Point(bitmapNode.get().getWidth(), bitmapNode.get().getHeight());
//            GraphicsGL::get().addbitmap(bitmap);
        }
    }

    public void draw(DrawArgument args) {
        draw(args, new Range<Short>((short)0, (short)0));
    }

    private void draw(DrawArgument args, Range<Short> vertical) {
        GameGLRenderer.getInstance().draw(
                bitmapNode,
                args.get_rectangle(origin, dimensions),
                vertical,
                args.getColor(),
                args.getAngle()
        );
    }
}
