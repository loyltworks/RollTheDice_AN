package com.lightricks.efraim.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.dice.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube(context: Context) {
    private val COORDS_PER_VERTEX = 3
    private val COORDS_PER_TEXTURE = 2
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val textureStride = COORDS_PER_TEXTURE * 4 // 4 bytes per texture coordinate

    private var vertexBuffer: FloatBuffer
    //private var mColorBuffer: FloatBuffer
    private var mTextureBuffer: FloatBuffer

    private var mPositionHandle = 0
    //private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private var mTexCoordHandle = 0
    private var mTextureHandle = 0

    private var mProgram = 0

    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                //"attribute vec4 aColor;" +
                "attribute vec2 aTexCoord;" +
                //"varying vec4 vColor;" +
                "varying vec2 vTexCoord;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                //"  vColor = aColor;" +
                "  vTexCoord = aTexCoord;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                //"varying vec4 vColor;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D uTexture;" +
                "void main() {" +
               // "  gl_FragColor = texture2D(uTexture, vTexCoord) * vColor;" +
                "  gl_FragColor = texture2D(uTexture, vTexCoord);" +
                "}"

    private val numFaces = 6
    private val textureIDs = IntArray(numFaces)
    private val bitmap = arrayOfNulls<Bitmap>(numFaces)
    private val imageFileIDs = intArrayOf(
        R.drawable.one,
        R.drawable.two,
        R.drawable.three,
        R.drawable.four,
        R.drawable.five,
        R.drawable.six
    )

    fun loadShader(type: Int, shaderCode: String?): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }


    init {
        val vertexByteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        vertexByteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = vertexByteBuffer.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)

//        val colorByteBuffer = ByteBuffer.allocateDirect(color.size * 4)
//        colorByteBuffer.order(ByteOrder.nativeOrder())
//        mColorBuffer = colorByteBuffer.asFloatBuffer()
//        mColorBuffer.put(color)
//        mColorBuffer.position(0)

        val textureByteBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4)
        textureByteBuffer.order(ByteOrder.nativeOrder())
        mTextureBuffer = textureByteBuffer.asFloatBuffer()
        mTextureBuffer.put(textureCoords)
        mTextureBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)

        for (face in 0 until numFaces) {
            bitmap[face] = BitmapFactory.decodeStream(context.resources.openRawResource(imageFileIDs[face]))
            GLES20.glGenTextures(1, textureIDs, face)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[face])
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap[face], 0)
            bitmap[face]?.recycle()
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

//        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor")
//        GLES20.glEnableVertexAttribArray(mColorHandle)
//        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer)

        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord")
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false, textureStride, mTextureBuffer)

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        for (i in 0 until 6) {
            mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture")
            GLES20.glUniform1i(mTextureHandle, i)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[i])
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, i * 4, 4)
        }

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        //GLES20.glDisableVertexAttribArray(mColorHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)
    }

    companion object {
        private val triangleCoords = floatArrayOf(
            // Front face
            -0.15f, 0.15f, 0.15f,  // top left
            -0.15f, -0.15f, 0.15f,  // bottom left
            0.15f, -0.15f, 0.15f,  // bottom right
            0.15f, 0.15f, 0.15f,  // top right
            // Back face
            -0.15f, 0.15f, -0.15f,  // top left
            -0.15f, -0.15f, -0.15f,  // bottom left
            0.15f, -0.15f, -0.15f,  // bottom right
            0.15f, 0.15f, -0.15f,  // top right
            // Top face
            -0.15f, 0.15f, -0.15f,  // top left
            -0.15f, 0.15f, 0.15f,  // bottom left
            0.15f, 0.15f, 0.15f,  // bottom right
            0.15f, 0.15f, -0.15f,  // top right
            // Bottom face
            -0.15f, -0.15f, -0.15f,  // top left
            -0.15f, -0.15f, 0.15f,  // bottom left
            0.15f, -0.15f, 0.15f,  // bottom right
            0.15f, -0.15f, -0.15f,  // top right
            // Right face
            0.15f, 0.15f, -0.15f,  // top left
            0.15f, -0.15f, -0.15f,  // bottom left
            0.15f, -0.15f, 0.15f,  // bottom right
            0.15f, 0.15f, 0.15f,  // top right
            // Left face
            -0.15f, 0.15f, -0.15f,  // top left
            -0.15f, -0.15f, -0.15f,  // bottom left
            -0.15f, -0.15f, 0.15f,  // bottom right
            -0.15f, 0.15f, 0.15f   // top right
        )

        private val textureCoords = floatArrayOf(
            // Front face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            // Back face
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            // Top face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            // Bottom face
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            // Right face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            // Left face
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
        )

        private val color = floatArrayOf(
            // Front face (red)
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            // Back face (green)
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            // Top face (blue)
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            // Bottom face (yellow)
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            // Right face (cyan)
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            // Left face (magenta)
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f
        )
    }
}
