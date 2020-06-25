package client

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class Camera {
    var pos = Vector3f(16.0f, 84.0f, 16.0f)
    var front = Vector3f(0.0f, 0.0f, 1.0f)
    var up = Vector3f(0.0f, 1.0f, 0.0f);

    var yaw = 0.0
    var pitch = 0.0

    var mlastX = 400.0
    var mlastY = 300.0

    var mode = CameraMode.FIRST

    fun updateCameraRotation(xPos: Double, yPos: Double) {
        var xOffset = xPos - mlastX
        var yOffset = yPos - mlastY
        mlastX = xPos
        mlastY = yPos

        val sensitivity = 0.05
        xOffset *= sensitivity
        yOffset *= sensitivity

        yaw += xOffset
        pitch -= yOffset

        if (pitch > 89.0)
            pitch = 89.0
        if (pitch < -89.0)
            pitch = -89.0

        var f = Vector3f()

        f.x = (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))).toFloat()
        f.y = (Math.sin(Math.toRadians(pitch))).toFloat()
        f.z = (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))).toFloat()
        front = f.normalize()
    }

    fun updatePosition(keyStates: Array<Boolean>) {
        if (keyStates[GLFW.GLFW_KEY_W]) pos.add(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
        if (keyStates[GLFW.GLFW_KEY_S]) pos.sub(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f))
        if (keyStates[GLFW.GLFW_KEY_A])
            pos.sub(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f).cross(up))
        if (keyStates[GLFW.GLFW_KEY_D])
            pos.add(Vector3f(front.x * 0.5f, front.y * 0.5f, front.z * 0.5f).cross(up))
    }

    fun generateViewProj(renderDistance: Float): Pair<Matrix4f, Matrix4f> {
        return Pair(Matrix4f()
            .lookAt(
                pos,
                Vector3f(pos.x + front.x, pos.y + front.y, pos.z + front.z),
                Vector3f(0.0f,1.0f,0.0f)),
            Matrix4f()
                .perspective(Math.toRadians(90.0).toFloat(),800.0f/600.0f,1.0f,renderDistance))
    }
}