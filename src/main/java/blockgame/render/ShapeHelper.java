package blockgame.render;

import blockgame.block.EnumDirection;
import blockgame.block.TileState;
import blockgame.gl.UVPair;
import blockgame.util.FloatList;
import blockgame.world.World;
import org.joml.Vector3f;

public class ShapeHelper {
    private static final float UV_SIZE = 0.0625f;
    private static final float OFFSET = 0.00048828125f;
    private static final Vector3f WORLD_TINT = new Vector3f(1.0f, 1.0f, 1.0f);


    public static void appendVerts(World world, int cX, int cZ , int rY, int x , int y, int z, TileState tileState, FloatList verts) {
        UVPair side = tileState.getUVForFace(EnumDirection.NORTH);
        int u = side.u;
        int v = side.v;

        UVPair bot = tileState.getUVForFace(EnumDirection.DOWN);
        int bu = bot.u;
        int bv = bot.v;

        UVPair top = tileState.getUVForFace(EnumDirection.UP);
        int tu = top.u;
        int tv = top.v;

        float vX = (float)x;
        float vY = (float)y;
        float vZ = (float)z;

        Vector3f tint = WORLD_TINT;
        if(tileState.getTint(cX, cZ, x, y, z) != null) {
            tint = tileState.getTint(cX, cZ, x, y, z);
        }


        float light = 1.0f;//blockgame.world.worldType.light;

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.SOUTH)) {
            // South
            verts.appendAll(
                    vX,       vY,       vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY+1.0f,  vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE) + OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE) + OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE) + OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET,
                    vX,       vY,       vZ,       (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.NORTH)) {
            // North
            verts.appendAll(
                    vX,       vY,       vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE -OFFSET, (v*UV_SIZE)+UV_SIZE -  OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.6f * light) * tint.x, (0.6f * light) * tint.y, (0.6f * light) * tint.z, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.EAST)) {
            // East
            verts.appendAll(
                    vX,       vY+1.0f,  vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET,
                    vX,       vY+1.0f,  vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + OFFSET,
                    vX,       vY,       vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY,       vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.WEST)) {
            // West
            verts.appendAll(
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.8f * light) * tint.x, (0.8f * light) * tint.y, (0.8f * light) * tint.z, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+ OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.DOWN)) {
            // Bottom
            verts.appendAll(
                    vX,       vY,       vZ,       (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY,       vZ,       (0.5f * light) * tint.x, (0.5f * light) * tint.y, (0.5f * light) * tint.z, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.UP)) {
            // Top
            verts.appendAll(
                    vX,       vY+1.0f,  vZ,       (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+UV_SIZE- OFFSET, (tv*UV_SIZE)+ OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+UV_SIZE - OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY+1.0f,  vZ,       (1.0f * light) * tint.x, (1.0f * light) * tint.y, (1.0f * light) * tint.z, (tu*UV_SIZE)+UV_SIZE - OFFSET, (tv*UV_SIZE)+ OFFSET);
        }
    }
}
