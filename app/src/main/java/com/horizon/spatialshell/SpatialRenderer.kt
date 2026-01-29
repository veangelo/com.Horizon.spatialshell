package com.horizon.spatialshell

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SpatialRenderer : GLSurfaceView.Renderer {

    private lateinit var vertexBuffer: FloatBuffer
    private var program = 0

    // Big, obvious triangle
    private val triangleCoords = floatArrayOf(
        0.0f,  0.6f, 0.0f,   // top
       -0.6f, -0.6f, 0.0f,   // bottom left
        0.6f, -0.6f, 0.0f    // bottom right
    )

    private val vertexShaderCode = """
        attribute vec4 aPosition;
        void main() {
          gl_Position = aPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
          gl_FragColor = vec4(0.0, 1.0, 0.3, 1.0); // bright green
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Bright background so you can tell it’s drawing
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        vertexBuffer = ByteBuffer
            .allocateDirect(triangleCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(triangleCoords)
                position(0)
            }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(
            posHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun loadShader(type: Int, code: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
        }
    }
}
