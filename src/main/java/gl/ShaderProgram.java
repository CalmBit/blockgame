package gl;

import org.lwjgl.opengl.GL31;

public class ShaderProgram {
    private int _program;

    public ShaderProgram(VertexShader vertexShader, FragmentShader fragmentShader) {
        _program = GL31.glCreateProgram();

        GL31.glAttachShader(_program, vertexShader.getShader());
        GL31.glAttachShader(_program, fragmentShader.getShader());

        GL31.glBindFragDataLocation(_program, 0, "outColor");
        GL31.glLinkProgram(_program);

        use();
    }


    public void use() {
        GL31.glUseProgram(_program);
    }

    public int getProgram() {
        return _program;
    }
}
