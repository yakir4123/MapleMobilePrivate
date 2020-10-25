package com.bapplications.maplemobile.utils;

import android.graphics.Bitmap;

import com.bapplications.maplemobile.gameplay.textures.Texture;

import java.util.HashMap;
import java.util.Map;

public class DrawableCircle extends Texture {

    public static Bitmap template;
    private static final float RADIUS = 8;
    private static final Map<Integer, Integer> colorToHandler = new HashMap<>();

    public static void init(Bitmap bitmap) {
        template = bitmap;
    }

    public static DrawableCircle createCircle(Point p, int color) {
        DrawableCircle res = new DrawableCircle();


        if(!colorToHandler.containsKey(color)) {
            Bitmap bmap = changeBitmapColor(template, color);
            int textureDataHandle = loadGLTexture(bmap);
            bmap.recycle();
            colorToHandler.put(color, textureDataHandle);
        }

        res.textureDataHandle = colorToHandler.get(color);
        p.flipY();
        res.origin = new Point();
        res.setPos(p, false);
        res.dimensions = new Point(RADIUS, RADIUS);
        res.half_dimensions_glratio = res.dimensions.scalarMul(0.5f).toGLRatio();

        return res;
    }

    private static Bitmap changeBitmapColor(Bitmap src, int color)
    {
        Bitmap res = src.copy(Bitmap.Config.ARGB_8888, true);

        int [] allpixels = new int[src.getHeight() * src.getWidth()];
        src.getPixels(allpixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(Color.alpha(allpixels[i]) > 0)
            {
                allpixels[i] = color;
            }
        }

        res.setPixels(allpixels,0,src.getWidth(),0, 0, src.getWidth(),src.getHeight());
        return res;
    }

}
