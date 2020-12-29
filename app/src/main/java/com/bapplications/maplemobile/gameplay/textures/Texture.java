package com.bapplications.maplemobile.gameplay.textures;

import android.opengl.Matrix;
import android.opengl.GLUtils;
import android.graphics.Bitmap;
import static android.opengl.GLES20.*;

import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;

import java.util.HashMap;
import java.util.Map;

public class Texture {

    private static Point drawingPos = new Point();

    private Point pos;
    protected Point origin;
    private Point shift = new Point();

    protected Object z;
    protected byte flip = 1;
    protected Point dimensions;
    protected Point half_dimensions_glratio;
    protected int textureDataHandle;
    protected float _rotationZ = 0.0f;

    private float[] scratchMatrix = new float[16];
    private static Map<Integer, Integer> bitmapToTextureMap = new HashMap<>();
    private Bitmap bmap;

    public Texture(){}

    public Texture(NXNode src) {
        this(src, true);
    }

    public Texture(NXNode src, boolean initGL) {
        initTexture(src, initGL);
    }

    public Texture(Bitmap bitmap) {
        bmap = bitmap;
        setZ("0");
        this.origin = new Point();
        dimensions = new Point(bmap.getWidth(), bmap.getHeight());
        half_dimensions_glratio = dimensions.scalarMul(0.5f).toGLRatio();
        origin = pointToAndroid(origin);
        setPos(new Point());
        loadGLTexture();
    }

    public void initTexture(NXNode src){
        initTexture(src, true);
    }

    public void initTexture(NXNode src, boolean initGL){
        if (!(src instanceof NXBitmapNode)) {
            throw new IllegalArgumentException("NXNode must be NXBitmapNode in Texture instance");
        }
        bmap = ((NXBitmapNode) src).get();
        setZ(src.getChild("z").get("0"));
        if (getZ().equals("0")) {
            setZ(src.getChild("zM").get("0"));
        }

        this.origin = new Point(src.getChild("origin"));
        dimensions = new Point(bmap.getWidth(), bmap.getHeight());
        half_dimensions_glratio = dimensions.scalarMul(0.5f).toGLRatio();
        origin = pointToAndroid(origin);
        setPos(new Point());
        if(initGL)
            loadGLTexture();
    }

    public void loadGLTexture() {
        textureDataHandle = loadGLTexture(bmap);
    }

    public static int loadGLTexture(Bitmap bmap)
    {
        Integer cachedTextureId = bitmapToTextureMap.get(bmap.hashCode());
        if (cachedTextureId != null)
        {
            return cachedTextureId;
        }

        // generate one texture pointer and bind it to our handle
        int[] textureHandle = new int[1];
        glGenTextures(1, textureHandle, 0);
        glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

        // create nearest filtered texture
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmap, 0);

        bitmapToTextureMap.put(bmap.hashCode(), textureHandle[0]);
        bmap.recycle();
        return textureHandle[0];
    }

    public static void clear(){
        int size = bitmapToTextureMap.values().size();
        int[] textureArr = bitmapToTextureMap.values().stream().mapToInt(i->i).toArray();
        glDeleteTextures(size, textureArr, 0);
        bitmapToTextureMap.clear();
    }

    public void draw (DrawArgument args) {
        // flip must be b4 drawingPos because <pos> is changed in flip function
        flip(args.getDirection());
        drawingPos.copy(pos).offset(args.getPos()).toGLRatio();
        if(!(drawingPos.x + half_dimensions_glratio.x > -1
                || drawingPos.x - half_dimensions_glratio.x < 1
                || drawingPos.y - half_dimensions_glratio.y > -1
                || drawingPos.y - half_dimensions_glratio.y < 1)) {
            return;
        }

        System.arraycopy(GLState._MVPMatrix, 0, scratchMatrix, 0, 16);

        bindTexture();

        // translate the sprite to it's current position
        Matrix.translateM(scratchMatrix, 0, drawingPos.x , drawingPos.y, 1);

        float angle = _rotationZ + args.getAngle();
        // rotation took 8% of running time, and most of the time is unnecessary
        // so i avoid using that in those cases
        if(angle != 0) {
            // rotate the sprite
            Matrix.rotateM(scratchMatrix, 0, angle, 0, 0, 1 );
        }
        // scale the sprite
        Matrix.scaleM(scratchMatrix, 0, dimensions.x * flip, dimensions.y, 1);

        // Apply the projection and view transformation
        glUniformMatrix4fv(GLState.mvpMatrixHandle, 1, false, scratchMatrix, 0);

        // Draw the sprite
        glDrawElements(GL_TRIANGLES, GLState.DRAW_ORDER.length, GL_UNSIGNED_SHORT, GLState._drawListBuffer);

        // Disable vertex array
        glDisableVertexAttribArray(GLState.positionHandle);
        glDisableVertexAttribArray(GLState.textureCoordinateHandle);
    }

    void bindTexture() {

        // Add program to OpenGL environment
        glUseProgram(GLState._programHandle);

        // Enable a handle to the vertices
        glEnableVertexAttribArray(GLState.positionHandle);
        GLState._vertexBuffer.position(0);
        // Prepare the coordinate data
        glVertexAttribPointer(GLState.positionHandle, GLState.COORDS_PER_VERTEX, GL_FLOAT, false, GLState.VERTEX_STRIDE, GLState._vertexBuffer);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureDataHandle);

        GLState._textureBuffer.position(0);
        glEnableVertexAttribArray(GLState.textureCoordinateHandle);
        glVertexAttribPointer(GLState.textureCoordinateHandle, 2, GL_FLOAT, false, 0, GLState._textureBuffer);
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

    public Point calculateDrawingPos(Point pos)  {
        return pos.flipY();//.plus(origin);
    }

    public Point getDimension() {
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

    public void shiftY(float y) {
        this.pos.y += y;
    }

    public void shiftX(float x) {
        this.pos.x += x;
    }
}

