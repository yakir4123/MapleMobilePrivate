//package com.bapplications.maplemobile.opengl;
//
//import android.opengl.GLES20;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//import java.nio.ShortBuffer;
//
//public class GLState {
//
//    private static FloatBuffer _vertexBuffer;
//    private static ShortBuffer _drawListBuffer;
//    private static FloatBuffer _textureBuffer;
//
//
//    /** Mapping coordinates for the sprite square */
//    private static float SQUARE_COORDINATES[] =
//            {
//                    -1.0f, -1.0f, 0.0f,
//                    1.0f, -1.0f, 0.0f,
//                    1.0f, 1.0f, 0.0f,
//                    -1.0f, 1.0f, 0.0f
//            };
//
//    /** Mapping coordinates for the texture */
//    private static final float TEXTURE_COORDINATES[] =
//            {
//                    0.0f, 1.0f,
//                    1.0f, 1.0f,
//                    1.0f, 0.0f,
//                    0.0f, 0.0f
//            };
//
//    public static void initGL() {
//        // initialize vertex byte buffer for shape coordinates
//        ByteBuffer bb = ByteBuffer.allocateDirect(SQUARE_COORDINATES.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        _vertexBuffer = bb.asFloatBuffer();
//        _vertexBuffer.put(SQUARE_COORDINATES);
//        _vertexBuffer.position(0);
//
//        // initialize texture byte buffer for texture coordinates
//        bb = ByteBuffer.allocateDirect(TEXTURE_COORDINATES.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        _textureBuffer = bb.asFloatBuffer();
//        _textureBuffer.put(TEXTURE_COORDINATES);
//        _textureBuffer.position(0);
//
//        // initialize byte buffer for the draw list
//        ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        _drawListBuffer = dlb.asShortBuffer();
//        _drawListBuffer.put(DRAW_ORDER);
//        _drawListBuffer.position(0);
//
//        int vertexShader = GameGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
//        int fragmentShader = GameGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
//        _programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
//        GLES20.glAttachShader(_programHandle, vertexShader);   // add the vertex shader to program
//        GLES20.glAttachShader(_programHandle, fragmentShader); // add the fragment shader to program
//
//        GLES20.glBindAttribLocation(_programHandle, 0, TEXTURE_COORDINATE_PARAM);
//        GLES20.glLinkProgram(_programHandle);
//
//        positionHandle = GLES20.glGetAttribLocation(_programHandle, POSITION_PARAM);
//        textureCoordinateHandle = GLES20.glGetAttribLocation(_programHandle, TEXTURE_COORDINATE_PARAM);
//        mvpMatrixHandle = GLES20.glGetUniformLocation(_programHandle, MVPMATRIX_PARAM);
//
//    }
//
//    /** Order to draw the sprite square */
//    private static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };
//}
