package com.horizon.spatialshell

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var glView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            glView = GLSurfaceView(this).apply {
                setEGLContextClientVersion(2)
                setRenderer(SpatialRenderer())
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
            setContentView(glView)

        } catch (t: Throwable) {
            // HARD FAIL SAFE: show error instead of closing app
            val errorView = TextView(this).apply {
                text = """
                    Startup error:
                    
                    ${t::class.java.simpleName}
                    ${t.message}
                """.trimIndent()
                textSize = 14f
            }
            setContentView(errorView)
        }
    }

    override fun onPause() {
        super.onPause()
        glView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView?.onResume()
    }
}
