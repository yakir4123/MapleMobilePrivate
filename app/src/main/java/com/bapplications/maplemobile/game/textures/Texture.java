package com.bapplications.maplemobile.game.textures;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

import java.util.HashMap;
import java.util.Map;

public class Texture {

    public static final float xscale = 1;
    public static final float yscale = 1;

    private float z;
    protected Point pos;
    protected Point origin;
    protected Point dimensions;
    protected float imageRatio;
    protected int _textureDataHandle;
    protected float _rotationZ = 0.0f;
    protected NXBitmapNode bitmapNode;

    private static Map<Integer, Integer> bitmapToTextureMap = new HashMap<>();

    public Texture(NXNode src) {
        if (!(src instanceof NXBitmapNode)) {
            throw new IllegalArgumentException("NXNode must be NXBitmapNode in Texture instance");
        }
        pos = new Point();
        bitmapNode = (NXBitmapNode) src;
        Bitmap bmap = bitmapNode.get();
        this.origin = new Point(src.getChild("origin").get());
        origin.x *= -1;
        dimensions = new Point(bmap.getWidth(), bmap.getHeight());
        _textureDataHandle = loadGLTexture(((NXBitmapNode) src).get());
        bmap.recycle();
    }


    protected int loadGLTexture(Bitmap bitmap)
    {

        Integer cachedTextureId = bitmapToTextureMap.get(bitmap.hashCode());
        if (cachedTextureId != null)
        {
            return cachedTextureId;
        }

        imageRatio = bitmap.getWidth() / ((float) bitmap.getHeight());

        // generate one texture pointer and bind it to our handle
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);

        bitmapToTextureMap.put(bitmap.hashCode(), textureHandle[0]);
        return textureHandle[0];
    }

    public static void clear(){
        int size = bitmapToTextureMap.values().size();
        int[] textureArr = bitmapToTextureMap.values().stream().mapToInt(i->i).toArray();
        GLES20.glDeleteTextures(size, textureArr, 0);
        bitmapToTextureMap.clear();
    }

    public void draw (Point viewPos) {
        Point curPoint;
        //  correct pos
        curPoint = viewPos.plus(pos).plus(dimensions.mul(new Point(0.5f, -0.5f))).plus(origin);

        //        if (pos.x == 52) {
//            curPoint = pos.plus(dimensions.mul(new Point(-0.5f, -0.5f))).plus(origin);
//        }else
//            curPoint = pos.plus(dimensions.mul(new Point(0.5f, -0.5f))).plus(origin);

//        curPoint = pos.plus(dimensions.scalarMul(-0.5f)).plus(origin);
//        curPoint = pos;
        float[] curPos = curPoint.toGLRatio();
        Log.d("draw", "pos: " + pos + " origin: " + origin + " dimensions " + dimensions);
        Log.d("draw"," \t curPoint: " + curPoint);
        Log.d("draw"," \t x = " + (curPos[0] * Loaded.SCREEN_WIDTH) + " y = " + (curPos[1] * Loaded.SCREEN_HEIGHT));
        float[] scratchMatrix = new float[16];
        System.arraycopy(GLState._MVPMatrix, 0, scratchMatrix, 0, 16);

        // Add program to OpenGL environment
        GLES20.glUseProgram(GLState._programHandle);

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(GLState.positionHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(GLState.positionHandle, GLState.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, GLState.VERTEX_STRIDE, GLState._vertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureDataHandle);

        GLState._textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(GLState.textureCoordinateHandle);
        GLES20.glVertexAttribPointer(GLState.textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, GLState._textureBuffer);

        // translate the sprite to it's current position
//        Matrix.translateM(scratchMatrix, 0, curPos.x, curPos.y, 0.1f);
        Matrix.translateM(scratchMatrix, 0, curPos[0] , curPos[1], 1);

        // rotate the sprite
        Matrix.rotateM(scratchMatrix, 0, _rotationZ, 0, 0, 1.0f);
        // scale the sprite
        Matrix.scaleM(scratchMatrix, 0, dimensions.x, dimensions.y , 1);
//        Matrix.scaleM(scratchMatrix, 0, 1, 1 , 1);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(GLState.mvpMatrixHandle, 1, false, scratchMatrix, 0);

        // Draw the sprite
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, GLState.DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, GLState._drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(GLState.positionHandle);
        GLES20.glDisableVertexAttribArray(GLState.textureCoordinateHandle);
    }


    private Rectangle createRectangle() {

        float w = dimensions.x;
        float h = dimensions.y;

        Point rlt = origin.negateSign();
        float rl = rlt.x;
        float rr = rlt.x + w;
        float rt = rlt.y;
        float rb = rlt.y + h;
        float cx = pos.x;
        float cy = pos.y;

        return new Rectangle(
                cx + (xscale * rl),
                cx + (xscale * rr),
                cy + (yscale * rt),
                cy + (yscale * rb)
        );

    }

    public float getz()
    {
        return z;
    }

    public void setZ(float z) {
        this.z = (z + 10f) / 20;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}

