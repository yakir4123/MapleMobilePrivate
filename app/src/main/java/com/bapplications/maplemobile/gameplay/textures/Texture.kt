package com.bapplications.maplemobile.gameplay.textures

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.bapplications.maplemobile.opengl.GLState
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.utils.Point
import java.util.*

open class Texture {
    private lateinit var pos: Point
    protected lateinit var origin: Point
    private var shift = Point()
    var z: Any? = null
    protected var flip: Byte = 1
    lateinit var dimenstion: Point
        protected set
    protected lateinit var halfDimensionsGLratio: FloatArray
    public var textureDataHandle = 0
    protected var _rotationZ = 0.0f
    lateinit var bmap: Bitmap
        private set

    constructor() {}

    @JvmOverloads
    constructor(src: NXNode, initGL: Boolean = true, recycle: Boolean = true) {
        initTexture(src, initGL, recycle)
    }

    constructor(bitmap: Bitmap, recycle: Boolean = true) {
        bmap = bitmap
        z = "0"
        origin = Point()
        dimenstion = Point(bmap.width, bmap.height)
        halfDimensionsGLratio = dimenstion.scalarMul(0.5f).toGLRatio()
        origin = pointToAndroid(origin)
        setPos(Point())
        loadGLTexture(recycle)
    }

    @JvmOverloads
    fun initTexture(src: NXNode, initGL: Boolean, recycle: Boolean = true) {
        require(src is NXBitmapNode) { "NXNode must be NXBitmapNode in Texture instance" }
        bmap = src.get()
        z = src.getChild<NXNode>("z").get("0")
        if (z == "0") {
            z = src.getChild<NXNode>("zM").get("0")
        }
        origin = Point(src.getChild("origin"))
        dimenstion = Point(bmap.width, bmap.height)
        halfDimensionsGLratio = dimenstion!!.scalarMul(0.5f).toGLRatio()
        origin = pointToAndroid(origin)
        setPos(Point())
        if (initGL) loadGLTexture(recycle)
    }

    fun loadGLTexture(recycle: Boolean) {
        textureDataHandle = loadGLTexture(bmap, recycle)
    }

    fun draw(args: DrawArgument) {
        flip(args.direction)
        val drawingPos = args.pos.plus(pos)
        val curPos = drawingPos.toGLRatio()
        if (!(curPos[0] + halfDimensionsGLratio[0] > -1 || curPos[0] - halfDimensionsGLratio[0] < 1 || curPos[1] - halfDimensionsGLratio[1] > -1 || curPos[1] - halfDimensionsGLratio[1] < 1)) {
            return
        }
        val scratchMatrix = FloatArray(16)
        System.arraycopy(GLState._MVPMatrix, 0, scratchMatrix, 0, 16)

        // Add program to OpenGL environment
        GLES20.glUseProgram(GLState._programHandle)

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(GLState.positionHandle)

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(GLState.positionHandle, GLState.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, GLState.VERTEX_STRIDE, GLState._vertexBuffer)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle)
        GLState._textureBuffer.position(0)
        GLES20.glEnableVertexAttribArray(GLState.textureCoordinateHandle)
        GLES20.glVertexAttribPointer(GLState.textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, GLState._textureBuffer)
        val angle = _rotationZ + args.angle
        //        angle = angle != 0 ? 45: 0;
        // translate the sprite to it's current position
        Matrix.translateM(scratchMatrix, 0, curPos[0], curPos[1], 1f)
        // rotate the sprite
        Matrix.rotateM(scratchMatrix, 0, angle, 0f, 0f, 1f)
        // scale the sprite
        Matrix.scaleM(scratchMatrix, 0, dimenstion!!.x * flip, dimenstion!!.y, 1f)

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(GLState.mvpMatrixHandle, 1, false, scratchMatrix, 0)

        // Draw the sprite
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, GLState.DRAW_ORDER.size, GLES20.GL_UNSIGNED_SHORT, GLState._drawListBuffer)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(GLState.positionHandle)
        GLES20.glDisableVertexAttribArray(GLState.textureCoordinateHandle)
    }

    fun getPos(): Point? {
        return pos
    }

    fun setPos(pos: Point) {
        setPos(pos, true)
    }

    fun setPos(pos: Point, relativeOrigin: Boolean) {
        pos.y *= -1
        if (relativeOrigin) this.pos = pos.plus(origin) else this.pos = pos
    }

    fun calculateDrawingPos(pos: Point): Point {
        return pos.flipY() //.plus(origin);
    }

    fun shift(shift: Point) {
        shift.y *= -1
        this.shift = shift
        pos.offset(shift)
    }

    private fun flip(flip: Byte) {
        if (this.flip == flip || flip.toInt() != 1 && flip.toInt() != -1) return
        this.flip = (-this.flip).toByte()
        pos.offset(shift.negateSign())
        setPos(pos.minus(origin), false)
        if (this.flip < 0) {
            origin = origin.minus(dimenstion.mul(Point(0.5f, -0.5f)))
            origin.x *= -1
            origin = origin.plus(dimenstion!!.mul(Point(-0.5f, -0.5f)))
        } else {
            origin = origin.plus(dimenstion!!.mul(Point(-0.5f, -0.5f)))
            origin.x *= -1
            origin = origin.minus(dimenstion!!.mul(Point(0.5f, -0.5f)))
        }
        setPos(pos)
        shift.x *= -1
        pos.offset(shift)
    }

    private fun pointToAndroid(p: Point): Point {
        var p = p
        p.x *= -1
        p = p.plus(dimenstion.mul(Point(0.5f, -0.5f)))
        return p
    }

    fun shiftY(y: Float) {
        pos.y += y
    }

    fun shiftX(x: Float) {
        pos.x += x
    }

    fun recycle() {
        bmap.recycle()
    }

    companion object {
        private val bitmapToTextureMap: MutableMap<Int, Int> = HashMap()
        @JvmStatic
        fun loadGLTexture(bmap: Bitmap, recycle: Boolean): Int {
            val cachedTextureId = bitmapToTextureMap[bmap.hashCode()]
            if (cachedTextureId != null) {
                return cachedTextureId
            }

            // generate one texture pointer and bind it to our handle
            val textureHandle = IntArray(1)
            GLES20.glGenTextures(1, textureHandle, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // create nearest filtered texture
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())

            // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmap, 0)
            bitmapToTextureMap[bmap.hashCode()] = textureHandle[0]
            if(recycle) {
                bmap.recycle()
            }
            return textureHandle[0]
        }

        fun clear() {
            val size = bitmapToTextureMap.values.size
            val textureArr = bitmapToTextureMap.values.stream().mapToInt { i: Int? -> i!! }.toArray()
            GLES20.glDeleteTextures(size, textureArr, 0)
            bitmapToTextureMap.clear()
        }
    }
}