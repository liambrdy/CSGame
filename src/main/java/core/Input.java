package core;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static boolean[] keysDown;
    private static boolean[] keysPressed;

    private static long windowHandle;

    public static void Init(long handle) {
        windowHandle = handle;

        keysDown = new boolean[Key.MAX.ordinal()];
        keysPressed = new boolean[Key.MAX.ordinal()];

        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            int idx = glfwToEnum(key).ordinal();

            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                if (!keysDown[idx]) {
                    keysPressed[idx] = true;
                }
                keysDown[idx] = true;

                if (key == GLFW_KEY_ESCAPE)
                    glfwSetWindowShouldClose(handle, true);
            } else {
                keysDown[idx] = false;
                keysPressed[idx] = false;
            }
        });
    }

    public static void Update() {
        Arrays.fill(keysPressed, false);
    }

    private static int enumToGLFW(Key k) {
        if (k.compareTo(Key.A) >= 0 && k.compareTo(Key.Z) <= 0) {
            return k.ordinal() - Key.A.ordinal() + GLFW_KEY_A;
        } else if (k.compareTo(Key.N0) >= 0 && k.compareTo(Key.N9) <= 0) {
            return k.ordinal() - Key.N0.ordinal() + GLFW_KEY_0;
        } else {
            return switch (k) {
                case Tab -> GLFW_KEY_TAB;
                case Space -> GLFW_KEY_SPACE;
                case Enter -> GLFW_KEY_ENTER;
                case Escape -> GLFW_KEY_ESCAPE;
                case Backspace -> GLFW_KEY_BACKSPACE;
                case Up -> GLFW_KEY_UP;
                case Down -> GLFW_KEY_DOWN;
                case Left -> GLFW_KEY_LEFT;
                case Right -> GLFW_KEY_RIGHT;
                default -> GLFW_KEY_UNKNOWN;
            };
        }
    }

    private static Key glfwToEnum(int k) {
        if (k >= GLFW_KEY_A && k <= GLFW_KEY_Z) {
            return Key.values()[k - GLFW_KEY_A + Key.A.ordinal()];
        } else if (k >= GLFW_KEY_0 && k <= GLFW_KEY_9) {
            return Key.values()[k - GLFW_KEY_0 + Key.N0.ordinal()];
        } else {
            return switch (k) {
                case GLFW_KEY_TAB -> Key.Tab;
                case GLFW_KEY_SPACE -> Key.Space;
                case GLFW_KEY_ENTER -> Key.Enter;
                case GLFW_KEY_ESCAPE -> Key.Escape;
                case GLFW_KEY_BACKSPACE -> Key.Backspace;
                case GLFW_KEY_UP -> Key.Up;
                case GLFW_KEY_DOWN -> Key.Down;
                case GLFW_KEY_LEFT -> Key.Left;
                case GLFW_KEY_RIGHT -> Key.Right;
                default -> Key.Unknown;
            };
        }
    }

    public static boolean getKey(Key k) { return glfwGetKey(windowHandle, enumToGLFW(k)) == GLFW_PRESS; }

    public static boolean isKeyDown(Key k) { return keysDown[k.ordinal()]; }
    public static boolean isKeyPressed(Key k) { return keysPressed[k.ordinal()]; }
}
