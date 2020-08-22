package com.bapplications.maplemobile.opengl;

import android.opengl.GLES20;

import com.bapplications.maplemobile.constatns.Loaded;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLState {

    public static final float[] _MVPMatrix = new float[16];
    public static final float[] _projectionMatrix = new float[16];
    public static final float[] _viewMatrix = new float[16];
    //region opengl stuff
    public static final String MVPMATRIX_PARAM = "uMVPMatrix";
    public static final String POSITION_PARAM = "vPosition";
    public static final String TEXTURE_COORDINATE_PARAM = "aTextureCoordinate";

    public static final float SCALE = 1;
    public static int _programHandle;
    public static int positionHandle = -1;
    public static int mvpMatrixHandle = -1;
    public static int textureCoordinateHandle = -1;

    /** number of coordinates per vertex in this array */
    public static final int COORDS_PER_VERTEX = 3;
    public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public static final String VERTEX_SHADER_CODE =
            "uniform mat4 " + MVPMATRIX_PARAM + ";" +
                    "attribute vec4 " + POSITION_PARAM + ";" +
                    "attribute vec2 " + TEXTURE_COORDINATE_PARAM + ";" +
                    "varying vec2 vTexCoordinate;" +
                    "void main() {" +
                    "  gl_Position = " + MVPMATRIX_PARAM + " * " + POSITION_PARAM + ";" +
                    "  vTexCoordinate = " + TEXTURE_COORDINATE_PARAM + ";" +
                    "}";

    public static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform sampler2D uTexture;" +
                    "varying vec2 vTexCoordinate;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTexture, vTexCoordinate); " +
                    "}";

    
    public static FloatBuffer _vertexBuffer;
    public static ShortBuffer _drawListBuffer;
    public static FloatBuffer _textureBuffer;

    /** Order to draw the sprite square */
    public static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };

    /** Mapping coordinates for the sprite square */
    public static float SQUARE_COORDINATES[] =
            {
                    -1.0f, -1.0f, 0.0f,
                    1.0f, -1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f
            };
    /** Mapping coordinates for the texture */
    public static final float TEXTURE_COORDINATES[] =
            {
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f
            };

    public static void initGL() {
        // initialize vertex byte buffer for shape coordinates

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        _drawListBuffer = dlb.asShortBuffer();
        _drawListBuffer.put(DRAW_ORDER);
        _drawListBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        _programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(_programHandle, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(_programHandle, fragmentShader); // add the fragment shader to program

        GLES20.glBindAttribLocation(_programHandle, 0, TEXTURE_COORDINATE_PARAM);
        GLES20.glLinkProgram(_programHandle);

        positionHandle = GLES20.glGetAttribLocation(_programHandle, POSITION_PARAM);
        textureCoordinateHandle = GLES20.glGetAttribLocation(_programHandle, TEXTURE_COORDINATE_PARAM);
        mvpMatrixHandle = GLES20.glGetUniformLocation(_programHandle, MVPMATRIX_PARAM);

    }
    
    public static int loadShader (int type, String shaderCode)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void setSpriteSquareRes() {
        for(int i = 0 ; i < SQUARE_COORDINATES.length ; i += 3){
            SQUARE_COORDINATES[i] = SCALE * SQUARE_COORDINATES[i] / Loaded.SCREEN_WIDTH;
            SQUARE_COORDINATES[i + 1] = SCALE * SQUARE_COORDINATES[i + 1] / Loaded.SCREEN_HEIGHT;
            SQUARE_COORDINATES[i + 2] = 0;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(SQUARE_COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        _vertexBuffer = bb.asFloatBuffer();
        _vertexBuffer.put(SQUARE_COORDINATES);
        _vertexBuffer.position(0);

        // initialize texture byte buffer for texture coordinates
        bb = ByteBuffer.allocateDirect(TEXTURE_COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        _textureBuffer = bb.asFloatBuffer();
        _textureBuffer.put(TEXTURE_COORDINATES);
        _textureBuffer.position(0);

    }
}
