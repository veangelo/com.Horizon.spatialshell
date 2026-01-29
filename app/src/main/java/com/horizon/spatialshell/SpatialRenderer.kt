package com.horizon.spatialshell

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class SpatialRenderer : GLSurfaceView.Renderer {

    private var program = 0
    private var aPos = 0

    private lateinit var gridBuf: FloatBuffer
    private var gridVerts = 0

    private lateinit var ringBuf: FloatBuffer
    private var ringVerts = 0

    private lateinit var crossBuf: FloatBuffer
    private var crossVerts = 0

    private var uColor = 0

    private val vShader = """
        attribute vec4 aPosition;
        void main() { gl_Position = aPosition; }
    """.trimIndent()

    private val fShader = """
        precision mediump float;
        uniform vec4 uColor;
        void main() { gl_FragColor = uColor; }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.02f, 0.02f, 0.03f, 1f)

        program = GLES20.glCreateProgram().also { p ->
            val vs = loadShader(GLES20.GL_VERTEX_SHADER, vShader)
            val fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fShader)
            GLES20.glAttachShader(p, vs)
            GLES20.glAttachShader(p, fs)
            GLES20.glLinkProgram(p)
        }

        aPos = GLES20.glGetAttribLocation(program, "aPosition")
        uColor = GLES20.glGetUniformLocation(program, "uColor")

        buildGrid(step = 0.1f)
        buildRing(radius = 0.55f, segments = 128)
        buildCross(size = 0.03f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)
        GLES20.glEnableVertexAttribArray(aPos)

        // Grid (dim)
        GLES20.glUniform4f(uColor, 0.12f, 0.12f, 0.18f, 1f)
        drawLines(gridBuf, gridVerts)

        // Ring boundary (brighter)
        GLES20.glUniform4f(uColor, 0.0f, 0.9f, 0.4f, 1f)
        drawLineStrip(ringBuf, ringVerts)

        // Crosshair (bright)
        GLES20.glUniform4f(uColor, 0.9f, 0.9f, 0.9f, 1f)
        drawLines(crossBuf, crossVerts)

        GLES20.glDisableVertexAttribArray(aPos)
    }

    private fun drawLines(buf: FloatBuffer, count: Int) {
        buf.position(0)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 3 * 4, buf)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, count)
    }

    private fun drawLineStrip(buf: FloatBuffer, count: Int) {
        buf.position(0)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 3 * 4, buf)
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, count)
    }

    private fun buildGrid(step: Float) {
        val lines = ArrayList<Float>()
        var v = -1f
        while (v <= 1.0001f) {
            // vertical line x=v from y=-1 to y=1
            lines.add(v); lines.add(-1f); lines.add(0f)
            lines.add(v); lines.add( 1f); lines.add(0f)
            // horizontal line y=v from x=-1 to x=1
            lines.add(-1f); lines.add(v); lines.add(0f)
            lines.add( 1f); lines.add(v); lines.add(0f)
            v += step
        }
        gridVerts = lines.size / 3
        gridBuf = floatsToBuffer(lines.toFloatArray())
    }

    private fun buildRing(radius: Float, segments: Int) {
        val pts = FloatArray((segments + 1) * 3)
        for (i in 0..segments) {
            val t = (i.toFloat() / segments.toFloat()) * (Math.PI.toFloat() * 2f)
            pts[i * 3 + 0] = radius * cos(t)
            pts[i * 3 + 1] = radius * sin(t)
            pts[i * 3 + 2] = 0f
        }
        ringVerts = pts.size / 3
        ringBuf = floatsToBuffer(pts)
    }

    private fun buildCross(size: Float) {
        val pts = floatArrayOf(
            -size, 0f, 0f,  size, 0f, 0f,
             0f, -size, 0f, 0f,  size, 0f
        )
        crossVerts = pts.size / 3
        crossBuf = floatsToBuffer(pts)
    }

    private fun floatsToBuffer(a: FloatArray): FloatBuffer =
        ByteBuffer.allocateDirect(a.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(a); position(0)
        }

    private fun loadShader(type: Int, code: String): Int =
        GLES20.glCreateShader(type).also { s ->
            GLES20.glShaderSource(s, code)
            GLES20.glCompileShader(s)
        }
}
