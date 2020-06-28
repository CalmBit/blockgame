package gl;

import org.lwjgl.opengl.GL31;

import java.io.File;
import java.io.IOException;

public class FragmentShader extends Shader {
    public FragmentShader(File file) throws IOException {
        super(GL31.GL_FRAGMENT_SHADER, file);
    }
}
