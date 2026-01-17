class MainActivity : Activity() {

    private lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(3)
        glView.setRenderer(SpatialRenderer())
        glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        setContentView(glView)
    }
}
