class SpatialRenderer : GLSurfaceView.Renderer {

    private var time = 0f

    override fun onDrawFrame(gl: GL10?) {
        time += 0.016f

        val floatY = kotlin.math.sin(time) * 0.04f
        val floatX = kotlin.math.cos(time) * 0.02f

        // Use floatX, floatY to move your panel matrix
        drawFloatingPanel(floatX, floatY)
    }
}
