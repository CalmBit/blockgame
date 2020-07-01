package blockgame.world;

import blockgame.block.EnumRenderLayer;
import blockgame.block.TilePalette;
import blockgame.block.TileState;
import blockgame.gl.ShaderProgram;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import blockgame.render.ShapeHelper;
import blockgame.util.FloatList;
import blockgame.util.IntList;

import java.util.Random;

public class Region {
    private int _rX;
    private int _rY;
    private int _rZ;
    private IntList _region = new IntList(16*16*16);
    private IntList vao = new IntList(EnumRenderLayer.VALUES.length);
    private IntList vbo = new IntList(EnumRenderLayer.VALUES.length);
    private IntList vertSize = new IntList(EnumRenderLayer.VALUES.length);

    private Matrix4f trans;

    public Region(int rX, int rY, int rZ) {
        for(EnumRenderLayer l : EnumRenderLayer.VALUES) {
            vao.set(l.ordinal(),  GL33.glGenVertexArrays());
            GL33.glBindVertexArray(vao.get(l.ordinal()));
            vbo.set(l.ordinal(), GL33.glGenBuffers());
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo.get(l.ordinal()));
        }

        trans = new Matrix4f()
                .translate(rX*16.0f, rY*16.0f, rZ * 16.0f);

        _rX = rX;
        _rY = rY;
        _rZ = rZ;
    }

    public FloatList buildRenderData(World world, int cX, int cZ, EnumRenderLayer l) {
        FloatList verts = new FloatList();
        for (int y = 0;y < 16;y++) {
            for (int z = 0;z < 16;z++) {
                for (int x = 0;x < 16;x++) {
                    TileState t = getTileAt(x, y, z);
                    if (!t.shouldRender() || t.renderLayer() != l)
                        continue;
                    ShapeHelper.appendVerts(world, cX, cZ, _rY, x, y, z, t, verts);
                }
            }
        }
        return verts;
    }

    public void bindData(FloatList verts, EnumRenderLayer l, ShaderProgram prog) {
        vertSize.set(l.ordinal(), verts.getLength());
        GL33.glBindVertexArray(vao.get(l.ordinal()));
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo.get(l.ordinal()));
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, verts.getStore(), GL33.GL_STATIC_DRAW);
        setupAttribs(vao.get(l.ordinal()), vbo.get(l.ordinal()), prog);
    }


    public TileState getTileAt(int x, int y, int z) {
        try {
            return TilePalette.getTileState(_region.get((y*(16*16))+(z*16)+x));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public void setTileAt(int x, int y, int z, int tile) {
        _region.set((y*(16*16))+(z*16)+x, tile);
    }

    public void draw(MemoryStack stack, EnumRenderLayer l, int uniTrans, float timer) {
        if(vertSize.get(l.ordinal()) == 0)
            return;
        GL33.glBindVertexArray(vao.get(l.ordinal()));
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo.get(l.ordinal()));
        GL33.glUniformMatrix4fv(uniTrans, false, trans.get(stack.mallocFloat(16)));
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertSize.get(l.ordinal()) / 8);
    }

    public void setupAttribs(int vao, int vbo, ShaderProgram prog) {
        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        prog.use();

        int posAttrib = GL33.glGetAttribLocation(prog.getProgram(), "position");
        GL33.glEnableVertexAttribArray(posAttrib);
        GL33.glVertexAttribPointer(posAttrib, 3, GL33.GL_FLOAT, false, 8*4, 0L);

        int colAttrib = GL33.glGetAttribLocation(prog.getProgram(), "color");
        GL33.glEnableVertexAttribArray(colAttrib);
        GL33.glVertexAttribPointer(colAttrib, 3, GL33.GL_FLOAT, false, 8*4, 3*4L);

        int texAttrib = GL33.glGetAttribLocation(prog.getProgram(), "texcoord");
        GL33.glEnableVertexAttribArray(texAttrib);
        GL33.glVertexAttribPointer(texAttrib, 2, GL33.GL_FLOAT, false, 8*4, 6*4L);
    }

    public void tick(World world, Random random) {
        for(int i =0;i < 3;i++) {
            int x = random.nextInt(16);
            int y = random.nextInt(16);
            int z = random.nextInt(16);

            TileState t = getTileAt(x, y, z);
            t.block.tick(world, _rX, _rY, _rZ, x, y, z);
        }
    }
}
