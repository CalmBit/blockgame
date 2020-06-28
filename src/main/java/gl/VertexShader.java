package gl;

import org.lwjgl.opengl.GL31;

import java.io.File;
import java.io.IOException;

public class VertexShader extends Shader {
    public VertexShader(File file) throws IOException {
        super(GL31.GL_VERTEX_SHADER, file);
    }
}
