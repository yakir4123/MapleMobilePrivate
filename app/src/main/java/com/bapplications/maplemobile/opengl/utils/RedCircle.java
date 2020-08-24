package com.bapplications.maplemobile.opengl.utils;

import android.graphics.Bitmap;
import com.bapplications.maplemobile.gameplay.textures.Texture;

public class RedCircle extends Texture {

    private static final float RADIUS = 8;
    public static Bitmap bmap;
    public int loadedTex = -1;
    public Point dimensions;

    public static void init(Bitmap bitmap) {
        bmap = bitmap;
    }

    public RedCircle(Point p) {
        super();
        // set pos going to flip y but i want to draw where the image is actually by p so i flip it before
        p.y *= -1;
        setPos(p, false);
        if(loadedTex == -1) {
            loadedTex = loadGLTexture(bmap);
            dimensions = new Point(RADIUS, RADIUS);
            half_dimensions_glratio = dimensions.scalarMul(0.5f).toGLRatio();
            bmap.recycle();
        }
        textureDataHandle = loadedTex;
        super.dimensions = dimensions;
    }

}
