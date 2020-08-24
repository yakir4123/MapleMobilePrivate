package com.bapplications.maplemobile.gameplay.textures;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLUtils;
import android.graphics.Bitmap;

import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;

import java.util.HashMap;
import java.util.Map;

public class Texture {

    private Point pos;
    private Point origin;
    private Point shift;

    protected Object z;
    protected byte flip = 1;
    protected Point dimensions;
    protected float[] half_dimensions_glratio;
    protected int textureDataHandle;
    protected float _rotationZ = 0.0f;
    protected NXBitmapNode bitmapNode;

    private static Map<Integer, Integer> bitmapToTextureMap = new HashMap<>();

    public Texture(){}

    public Texture(NXNode src) {
        initTexture(src);
    }

    public void initTexture(NXNode src){
        if (!(src instanceof NXBitmapNode)) {
            throw new IllegalArgumentException("NXNode must be NXBitmapNode in Texture instance");
        }
        shift = new Point();
        bitmapNode = (NXBitmapNode) src;
        Bitmap bmap = bitmapNode.get();
        this.origin = new Point(src.getChild("origin").get());
        dimensions = new Point(bmap.getWidth(), bmap.getHeight());
        half_dimensions_glratio = dimensions.scalarMul(0.5f).toGLRatio();
        origin = pointToAndroid(origin);
        setPos(new Point());
        textureDataHandle = loadGLTexture(bmap);
        bmap.recycle();
    }

    protected static int loadGLTexture(Bitmap bitmap)
    {

        Integer cachedTextureId = bitmapToTextureMap.get(bitmap.hashCode());
        if (cachedTextureId != null)
        {
            return cachedTextureId;
        }

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

    public void draw (DrawArgument args) {

        flip(args.getDirection());
        Point drawingPos = args.getPos().plus(pos);
        float[] curPos = drawingPos.toGLRatio();
        if(!(curPos[0] + half_dimensions_glratio[0] > -1
                || curPos[0] - half_dimensions_glratio[0] < 1
                || curPos[1] - half_dimensions_glratio[1] > -1
                || curPos[1] - half_dimensions_glratio[1] < 1)) {
            return;
        }
        float[] scratchMatrix = new float[16];
        System.arraycopy(GLState._MVPMatrix, 0, scratchMatrix, 0, 16);

        // Add program to OpenGL environment
        GLES20.glUseProgram(GLState._programHandle);

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(GLState.positionHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(GLState.positionHandle, GLState.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, GLState.VERTEX_STRIDE, GLState._vertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);

        GLState._textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(GLState.textureCoordinateHandle);
        GLES20.glVertexAttribPointer(GLState.textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, GLState._textureBuffer);

        // translate the sprite to it's current position
        Matrix.translateM(scratchMatrix, 0, curPos[0] , curPos[1], 1);

        // rotate the sprite
        Matrix.rotateM(scratchMatrix, 0, _rotationZ, 0, 0, 1.0f);
        // scale the sprite
        Matrix.scaleM(scratchMatrix, 0, dimensions.x * flip, dimensions.y , 1);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(GLState.mvpMatrixHandle, 1, false, scratchMatrix, 0);

        // Draw the sprite
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, GLState.DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, GLState._drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(GLState.positionHandle);
        GLES20.glDisableVertexAttribArray(GLState.textureCoordinateHandle);
    }

    public Object getZ()
    {
        return z;
    }

    public void setZ(Object z) {
        this.z = z;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        setPos(pos, true);
    }


    public void setPos(Point pos, boolean relativeOrigin){
        pos.y *= -1;
        if(relativeOrigin)
            this.pos = pos.plus(origin);
        else
            this.pos = pos;
    }

    public Point getDimenstion() {
        return dimensions;
    }


    public void shift(Point shift) {
        shift.y *= -1;
        this.shift = shift;
        pos.offset(shift);
    }

    private void flip(byte flip) {
        if (this.flip == flip || (flip != 1 && flip != -1))
            return;
        this.flip = (byte) (-this.flip);
        pos.offset(shift.negateSign());
        setPos(pos.minus(origin), false);
        if (this.flip < 0) {
            origin = origin.minus(dimensions.mul(new Point(0.5f, -0.5f)));
            origin.x *= -1;
            origin = origin.plus(dimensions.mul(new Point(-0.5f, -0.5f)));
        } else {
            origin = origin.plus(dimensions.mul(new Point(-0.5f, -0.5f)));
            origin.x *= -1;
            origin = origin.minus(dimensions.mul(new Point(0.5f, -0.5f)));
        }
        setPos(pos);
        shift.x *= -1;
        pos.offset(shift);
    }

    private Point pointToAndroid(Point p) {
        p.x *= -1;
        p = p.plus(dimensions.mul(new Point(0.5f, -0.5f)));
        return p;
    }
}

