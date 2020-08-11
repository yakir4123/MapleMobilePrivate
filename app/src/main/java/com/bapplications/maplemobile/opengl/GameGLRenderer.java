package com.bapplications.maplemobile.opengl;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.Range;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.game.Stage;
import com.bapplications.maplemobile.opengl.utils.Color;
import com.bapplications.maplemobile.opengl.utils.Offset;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Quad;
import com.bapplications.maplemobile.opengl.utils.QuadsBuffer;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;

import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GameGLRenderer";
    private static final int ATLASW = 8192;
    private static final int ATLASH = 8192;
    private static final byte MINLOSIZE = 32;
    private static GameGLRenderer instance;

    //region opengl stuff
    private static final String MVPMATRIX_PARAM = "uMVPMatrix";
    private static final String POSITION_PARAM = "vPosition";
    private static final String TEXTURE_COORDINATE_PARAM = "aTextureCoordinate";

    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 coord;" +
			"attribute vec4 color;" +
                    "varying vec2 texpos;" +
                    "varying vec4 colormod;" +
                    "uniform vec2 screensize;" +
                    "uniform int yoffset;" +

                    "void main(void)" +
                    "{" +
                    "	float x = -1.0 + coord.x * 2.0 / screensize.x;" +
                    "	float y = 1.0 - (coord.y + float(yoffset)) * 2.0 / screensize.y;" +
                    "   gl_Position = vec4(x, y, 0.0, 1.0);" +
                    "	texpos = coord.zw;" +
                    "	colormod = color;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "varying vec2 texpos;" +
                    "varying vec4 colormod;" +
                    "uniform sampler2D texture;" +
                    "uniform vec2 atlassize;" +
                    "uniform int fontregion;" +

                    "void main(void)" +
                    "{" +
                    "	if (texpos.y == 0.0)" +
                    "	{" +
                    "		gl_FragColor = colormod;" +
                    "	}" +
                    "	else if (texpos.y <= float(fontregion))" +
                    "	{" +
                    "		gl_FragColor = vec4(1, 1, 1, texture2D(texture, texpos / atlassize).r) * colormod;" +
                    "	}" +
                    "	else" +
                    "	{" +
                    "		gl_FragColor = texture2D(texture, texpos / atlassize) * colormod;" +
                    "	}" +
                    "}";

    int[] VBOs = new int[1];
    int VBO;
    int[] taxtures = new int[1];
    int atlas;
    int shaderProgram;
    int attribute_coord;
    int attribute_color;
    int uniform_texture;
    int uniform_atlassize;
    int uniform_screensize;
    int uniform_yoffset;
    int uniform_fontregion;

    private Stage stage;
    private Rectangle ScreenRect;
    private HashMap<Integer, Offset>  offsets;
    private Offset nulloffset = new Offset();
    private QuadsBuffer quads;
    private GameGLSurfaceView surface;
    private Point border;
    private Range<Integer> yrange;
    private int wasted = 0 ;
    private int fontymax;

    public static GameGLRenderer createInstance(GameGLSurfaceView surface){
        instance = new GameGLRenderer(surface);
        return instance;
    }

    public static GameGLRenderer getInstance(){
        return instance;
    }

    private GameGLRenderer(GameGLSurfaceView surface){
        this.surface = surface;
        stage = new Stage();
        quads = new QuadsBuffer();
        offsets = new HashMap<>();
        border = new Point(0, 0);
        yrange = new Range<>(0, 0);
    }

    boolean locked = false;
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        ScreenRect = new Rectangle(0, Loaded.SCREEN_WIDTH, 0, Loaded.SCREEN_HEIGHT);

        GLES32.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        init();
        stage.init();
        stage.load(910000000,  0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        clearScene();
        stage.draw(1f);
//        UI::get().draw(alpha);
        flush(1f);
    }

    private void flush(float opacity) {

        boolean coverscene = opacity != 1.0f;

        if (coverscene)
        {
            float complement = 1.0f - opacity;
            Color color = new Color(0.0f, 0.0f, 0.0f, complement);

            quads.put(0, Loaded.SCREEN_WIDTH, 0, Loaded.SCREEN_HEIGHT, nulloffset, color, 0.0f);
        }

        GLES32.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

        int fsize = quads.size() / Quad.LENGTH;

        GLES32.glEnableVertexAttribArray(attribute_coord);
        GLES32.glEnableVertexAttribArray(attribute_color);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, VBO);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, quads.size(), quads.data(), GLES32.GL_STREAM_DRAW);

        GLES32.glDrawArrays(GLES32.GL_QUADS, 0, fsize);

        GLES32.glDisableVertexAttribArray(attribute_coord);
        GLES32.glDisableVertexAttribArray(attribute_color);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0);

    }

    private void clearScene() {
        if (!locked)
            quads.clear();
    }


    public void draw(NXBitmapNode bmp, final Rectangle rect, final Range<Short> vertical, final Color color, float angle)
    {
        if (locked)
            return;

        if (color.alpha() == 0)
            return;

        if (!rect.overlaps(ScreenRect))
            return;

        Offset offset = getoffset(bmp);

        offset.top += vertical.getLower();
        offset.bottom -= vertical.getUpper();
//
        quads.put(rect.left(), rect.right(), rect.top() + vertical.getLower(), rect.bottom() - vertical.getUpper()  , offset, color, angle);
    }

	Offset getoffset(NXBitmapNode bmp)
    {
        int id = bmp.hashCode();
        Offset offiter = offsets.get(id);

        if (offiter != null)
            return offiter;

        int x = 0;
        int y = 0;
        int width = bmp.get().getWidth();
        int height = bmp.get().getHeight();

        if (width <= 0 || height <= 0)
            return nulloffset;

//        Leftover value = new Leftover(x, y, width, height);

//        int lid = leftovers.findnode(
//                value,
//                [](const Leftover& val, const Leftover& leaf)
//        {
//            return val.width() <= leaf.width() && val.height() <= leaf.height();
//        }
//		);

//        if (lid > 0)
//        {
//			const Leftover& leftover = leftovers[lid];
//
//            x = leftover.left;
//            y = leftover.top;
//
//            GLshort width_delta = leftover.width() - width;
//            GLshort height_delta = leftover.height() - height;
//
//            leftovers.erase(lid);
//
//            wasted -= width * height;
//
//            if (width_delta >= MINLOSIZE && height_delta >= MINLOSIZE)
//            {
//                leftovers.add(rlid, Leftover(x + width, y + height, width_delta, height_delta));
//                rlid++;
//
//                if (width >= MINLOSIZE)
//                {
//                    leftovers.add(rlid, Leftover(x, y + height, width, height_delta));
//                    rlid++;
//                }
//
//                if (height >= MINLOSIZE)
//                {
//                    leftovers.add(rlid, Leftover(x + width, y, width_delta, height));
//                    rlid++;
//                }
//            }
//            else if (width_delta >= MINLOSIZE)
//            {
//                leftovers.add(rlid, Leftover(x + width, y, width_delta, height + height_delta));
//                rlid++;
//            }
//            else if (height_delta >= MINLOSIZE)
//            {
//                leftovers.add(rlid, Leftover(x, y + height, width + width_delta, height_delta));
//                rlid++;
//            }
//        }
//        else
//        {
            if (border.x + width > ATLASW)
            {
                border.x = 0;
                border.offset(0, yrange.getUpper());

                if (border.y + height > ATLASH)
                    clearinternal();
                else
                    yrange = new Range<Integer>(0, 0);
            }

            x = (short) border.x;
            y = (short) border.y;

            border.offset(width, 0);

            if (height > yrange.getUpper())
            {
                if (x >= MINLOSIZE && height - yrange.getUpper() >= MINLOSIZE)
                {
//                    leftovers.add(rlid, Leftover(0, yrange.first(), x, height - yrange.second()));
//                    rlid++;
                }

                wasted += x * (height - yrange.getUpper());

                if (y < 0) {
                    yrange = new Range<>(y + height, height);
                } else {
                    yrange = new Range<>(height, y + height);

                }
            }
            else if (height < yrange.getLower() - y)
            {
                if (width >= MINLOSIZE && yrange.getLower() - y - height >= MINLOSIZE)
                {
//                    leftovers.add(rlid, new Leftover(x, y + height, width, yrange.getLower() - y - height));
//                    rlid++;
                }

                wasted += width * (yrange.getLower() - y - height);
            }
//        }

        //size_t used = ATLASW * border.y() + border.x() * yrange.second();
        //
        //double usedpercent = static_cast<double>(used) / (ATLASW * ATLASH);
        //double wastedpercent = static_cast<double>(wasted) / used;
        //
        //std::cout << "Used: [" << usedpercent << "] Wasted: [" << wastedpercent << "]" << std::endl;

        GLUtils.texSubImage2D(GLES32.GL_TEXTURE_2D, 0, x, y, bmp.get());


//        return offsets.emplace(
//                std::piecewise_construct,
//                std::forward_as_tuple(id),
//            std::forward_as_tuple(x, y, width, height)
//		).first->second;
        return new Offset(x, y, width, height);
    }


    void clearinternal()
    {
        border = new Point(0, fontymax);
        yrange = new Range(0, 0);

        offsets.clear();
//        leftovers.clear();
//        rlid = 1;
        wasted = 0;
    }

    int init()
    {
        // Setup parameters
        // ----------------

		int bufSize = 512;
        final int[] success = new int[1];
        char[] infoLog = new char[bufSize];

        // Vertex Shader
        int vertexShader = GLES32.glCreateShader(GLES32.GL_VERTEX_SHADER);
        GLES32.glShaderSource(vertexShader, VERTEX_SHADER_CODE);
        GLES32.glCompileShader(vertexShader);

        // Check for shader compile errors
        GLES32.glGetShaderiv(vertexShader, GLES32.GL_COMPILE_STATUS, success, 0);

        if (success[0] == 0)
        {
            Log.e(TAG, "Could not compile shader " + GLES32.GL_VERTEX_SHADER + ":");
            Log.e(TAG, " " + GLES32.glGetShaderInfoLog(vertexShader));
            GLES32.glDeleteShader(vertexShader);
            return -1;
        }

        // Fragment Shader
        int fragmentShader = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER);
        GLES32.glShaderSource(fragmentShader, FRAGMENT_SHADER_CODE);
        GLES32.glCompileShader(fragmentShader);

        // Check for shader compile errors
        GLES32.glGetShaderiv(fragmentShader, GLES32.GL_COMPILE_STATUS, success, 0);

        if (success[0] == 0)
        {
            Log.e(TAG, "Could not compile shader " + GLES32.GL_FRAGMENT_SHADER + ":");
            Log.e(TAG, " " + GLES32.glGetShaderInfoLog(fragmentShader));
            return -1;
        }

        // Link Shaders
        shaderProgram = GLES32.glCreateProgram();
        GLES32.glAttachShader(shaderProgram, vertexShader);
        GLES32.glAttachShader(shaderProgram, fragmentShader);
        GLES32.glLinkProgram(shaderProgram);

        // Check for linking errors
        GLES32.glGetProgramiv(shaderProgram, GLES32.GL_LINK_STATUS, success, 0);

        if (success[0] == 0)
        {
            Log.e(TAG, " " + GLES32.glGetProgramInfoLog(shaderProgram));
            return -1;
        }

        // Validate Program
        GLES32.glValidateProgram(shaderProgram);

        // Check for validation errors
        GLES32.glGetProgramiv(shaderProgram, GLES32.GL_VALIDATE_STATUS, success, 0);

        if (success[0] == 0)
        {
            Log.e(TAG, " " + GLES32.glGetProgramInfoLog(shaderProgram));
            return -1;
        }

        GLES32.glDeleteShader(vertexShader);
        GLES32.glDeleteShader(fragmentShader);

        attribute_coord = GLES32.glGetAttribLocation(shaderProgram, "coord");
        attribute_color = GLES32.glGetAttribLocation(shaderProgram, "color");
        uniform_texture = GLES32.glGetUniformLocation(shaderProgram, "texture");
        uniform_atlassize = GLES32.glGetUniformLocation(shaderProgram, "atlassize");
        uniform_screensize = GLES32.glGetUniformLocation(shaderProgram, "screensize");
        uniform_yoffset = GLES32.glGetUniformLocation(shaderProgram, "yoffset");
        uniform_fontregion = GLES32.glGetUniformLocation(shaderProgram, "fontregion");

        if (attribute_coord == -1 || attribute_color == -1 || uniform_texture == -1 || uniform_atlassize == -1 || uniform_screensize == -1 || uniform_yoffset == -1)
            return -1;

        // Vertex Buffer Object
        GLES32.glGenBuffers(VBOs.length, VBOs, 0);
        VBO = VBOs[0];
        GLES32.glGenTextures(1, taxtures, 0);
        atlas = taxtures[0];

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, atlas);
        GLES32.glPixelStorei(GLES32.GL_UNPACK_ALIGNMENT, 1);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, ATLASW, ATLASH, 0, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, null);

        return 0;
    }
}
