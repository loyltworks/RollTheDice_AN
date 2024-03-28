package com.example.dice

import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.dice.databinding.ActivityMainBinding
import kotlin.random.Random


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding:ActivityMainBinding

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var myGLRenderer: MyGLRenderer
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myGLRenderer = MyGLRenderer(this)
        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setZOrderOnTop(true)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceView.setRenderer(myGLRenderer)

        glSurfaceView.holder.setFormat(PixelFormat.RGBA_8888)
        glSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)


        binding.one.setOnClickListener(this)
        binding.two.setOnClickListener(this)
        binding.three.setOnClickListener(this)
        binding.four.setOnClickListener(this)
        binding.five.setOnClickListener(this)
        binding.six.setOnClickListener(this)
        binding.random.setOnClickListener(this)



    }

    override fun onClick(p0: View?) {

        when(p0!!.id){
            R.id.one -> {
                myGLRenderer.startRotationRandomly(1)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
            R.id.two -> {
                myGLRenderer.startRotationRandomly(2)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
            R.id.three -> {
                myGLRenderer.startRotationRandomly(3)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
            R.id.four -> {
                myGLRenderer.startRotationRandomly(4)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
            R.id.five -> {
                myGLRenderer.startRotationRandomly(5)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
            R.id.six -> {
                myGLRenderer.startRotationRandomly(6)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }

            R.id.random -> {
                myGLRenderer.startRotationRandomly(Random.nextInt(6) + 1)
                mediaPlayer = MediaPlayer.create(this,R.raw.shake_dice)
                mediaPlayer.start()
            }
        }

    }
}