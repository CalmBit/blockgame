package render;

import block.EnumDirection;
import block.TileState;
import gl.UVPair;
import util.FloatList;
import world.World;

public class ShapeHelper {
    private static final float UV_SIZE = 0.0625f;
    private static final float OFFSET = 0.00048828125f;


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

        float emit = tileState.getEmittance();


        float light = 1.0f;//world.worldType.light;

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.SOUTH)) {
            // South
            verts.appendAll(
                    vX,       vY,       vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY+1.0f,  vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE) + OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE) + OFFSET, (v*UV_SIZE) + OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE) + OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET,
                    vX,       vY,       vZ,       (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.NORTH)) {
            // North
            verts.appendAll(
                    vX,       vY,       vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE -OFFSET, (v*UV_SIZE)+UV_SIZE -  OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+UV_SIZE - OFFSET, (v*UV_SIZE)+OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.6f * light) + emit, (0.6f * light) + emit, (0.6f * light) + emit, (u*UV_SIZE)+OFFSET, (v*UV_SIZE)+UV_SIZE - OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.EAST)) {
            // East
            verts.appendAll(
                    vX,       vY+1.0f,  vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET,
                    vX,       vY+1.0f,  vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + OFFSET,
                    vX,       vY,       vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY,       vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + UV_SIZE - OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE) + OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.WEST)) {
            // West
            verts.appendAll(
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+UV_SIZE- OFFSET, (v*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (0.8f * light) + emit, (0.8f * light) + emit, (0.8f * light) + emit, (u*UV_SIZE)+ OFFSET, (v*UV_SIZE)+ OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.DOWN)) {
            // Bottom
            verts.appendAll(
                    vX,       vY,       vZ,       (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY,       vZ,       (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY,       vZ+1.0f,  (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+ OFFSET,
                    vX,       vY,       vZ+1.0f,  (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+ OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY,       vZ,       (0.5f * light) + emit, (0.5f * light) + emit, (0.5f * light) + emit, (bu*UV_SIZE)+UV_SIZE- OFFSET, (bv*UV_SIZE)+UV_SIZE- OFFSET);
        }

        if(tileState.block.shouldRenderFace(world, cX, cZ, rY, x, y, z, EnumDirection.UP)) {
            // Top
            verts.appendAll(
                    vX,       vY+1.0f,  vZ,       (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+UV_SIZE- OFFSET, (tv*UV_SIZE)+ OFFSET,
                    vX,       vY+1.0f,  vZ+1.0f,  (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+ OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ+1.0f,  (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+ OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX+1.0f,  vY+1.0f,  vZ,       (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+UV_SIZE - OFFSET, (tv*UV_SIZE)+UV_SIZE- OFFSET,
                    vX,       vY+1.0f,  vZ,       (1.0f * light) + emit, (1.0f * light) + emit, (1.0f * light) + emit, (tu*UV_SIZE)+UV_SIZE - OFFSET, (tv*UV_SIZE)+ OFFSET);
        }
    }
}
