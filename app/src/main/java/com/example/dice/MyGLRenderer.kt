package com.example.dice

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.widget.Toast
import com.lightricks.efraim.opengl.Cube
import java.util.logging.Handler
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class MyGLRenderer(val context: Context): GLSurfaceView.Renderer {

    @Volatile
    var angle = 0f
    var rotationAxis = floatArrayOf(1f, 0f, 0f) // Axis of rotation
    private var angularVelocity = 0f
    private var isRotating = false
    private val decelerationRate = 0.998f
    private var direction = 1

    private lateinit var mCube: Cube
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    var face = 4





    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // initialize a triangle
        mCube = Cube(context)
    }

    override fun onDrawFrame(unused: GL10) {


        //val scratch = FloatArray(16)
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1.3f, 0f, 0f, 1f, 0.0f, 1.0f, 0.0f)


        if (isRotating) {

            angularVelocity *= decelerationRate
            Log.d("dfdafa","$angularVelocity")
            angle += angularVelocity*direction
            if (Math.abs(angularVelocity) < 30f) {

                if(face == 1){
                    rotationAxis =floatArrayOf(1.0f, 0.0f, 0.0f)
                    angle = 0f
                }else if(face == 2){
                    rotationAxis =floatArrayOf(1.0f, 0.0f, 0.0f)
                    angle = 180f
                }else if(face == 3){
                    rotationAxis =floatArrayOf(1.0f, 0.0f, 0.0f)
                    angle = 90f
                }else if(face == 4){
                    rotationAxis =floatArrayOf(1.0f, 0.0f, 0.0f)
                    angle = 270f
                }else if(face == 5){
                    rotationAxis =floatArrayOf(0.0f, 1.0f, 0.0f)
                    angle = -90f
                }else if(face == 6){
                    rotationAxis =floatArrayOf(0.0f, 1.0f, 0.0f)
                    angle = 90f
                }


                isRotating = false
            }
        }


        Matrix.setRotateM(rotationMatrix, 0, angle, rotationAxis[0], rotationAxis[1], rotationAxis[2])


        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(rotationMatrix, 0, vPMatrix, 0, rotationMatrix, 0)

        // Draw shape
        mCube.draw(rotationMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
    }

    fun startRotationRandomly(face:Int) {
        this.face = face
       // val axis = floatArrayOf(0.29770184f, 0.8338775f, 0.5537736f)
        val axis = floatArrayOf(Random.nextFloat(),Random.nextFloat(),Random.nextFloat())
        val direction = if (Random.nextBoolean()) 1 else -1
        val velocity = 35f

        startRotation(velocity, axis, direction)
    }

    private fun startRotation(velocity: Float, axis: FloatArray, direction: Int) {
        angularVelocity = velocity
        rotationAxis = axis
        this.direction = direction
        isRotating = true
    }





}