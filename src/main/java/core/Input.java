package core;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;

public class Input {
    private static boolean[] keysDown;
    private static boolean[] keysPressed;

    private static boolean[] buttonsDown;
    private static boolean[] buttonsClicked;

    private static long windowHandle;

    private static final DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
    private static final DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

    private static float scale;
    private static float scroll;
    private static boolean scrolledThisFrame;

    public static void Init(long handle) {
        windowHandle = handle;

        keysDown = new boolean[Key.MAX.ordinal()];
        keysPressed = new boolean[Key.MAX.ordinal()];

        buttonsDown = new boolean[Button.MAX.ordinal()];
        buttonsClicked = new boolean[Button.MAX.ordinal()];
        
        scrolledThisFrame = false;

        try (MemoryStack stack = stackPush()) {
            FloatBuffer xScale = stack.callocFloat(1);
            FloatBuffer yScale = stack.callocFloat(1);
            glfwGetMonitorContentScale(glfwGetPrimaryMonitor(), xScale, yScale);
            float xS = xScale.get(0);
            float yS = yScale.get(0);
            if (xS != yS)
                throw new RuntimeException("Mismatching monitor scales: (" + xS + ", " + yS + ")");

            scale = xS;
        }

        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            int idx = glfwToEnumKey(key).ordinal();

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

        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            int idx = glfwToEnumButton(button).ordinal();

            if (action == GLFW_PRESS) {
                if (!buttonsDown[idx]) {
                    buttonsClicked[idx] = true;
                }
                buttonsDown[idx] = true;
            } else {
                buttonsDown[idx] = false;
                buttonsClicked[idx] = false;
            }
        });

        glfwSetScrollCallback(windowHandle, (window, x, y) -> {
            scroll = (float)y;
            scrolledThisFrame = true;
        });
    }

    public static void Update() {
        Arrays.fill(keysPressed, false);
        Arrays.fill(buttonsClicked, false);
        scrolledThisFrame = false;
        scroll = 0.0f;
    }

    private static int glfwToEnumKey(Key k) {
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
                case Shift -> GLFW_KEY_LEFT_SHIFT;
                default -> GLFW_KEY_UNKNOWN;
            };
        }
    }

    private static Key glfwToEnumKey(int k) {
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
                case GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT -> Key.Shift;
                default -> Key.Unknown;
            };
        }
    }

    private static Button glfwToEnumButton(int b) {
        return switch (b) {
            case GLFW_MOUSE_BUTTON_1 -> Button.Button1;
            case GLFW_MOUSE_BUTTON_2 -> Button.Button2;
            case GLFW_MOUSE_BUTTON_3 -> Button.Button3;
            default -> Button.Unknown;
        };
    }

    public static boolean getKey(Key k) { return glfwGetKey(windowHandle, glfwToEnumKey(k)) == GLFW_PRESS; }

    public static boolean isKeyDown(Key k) { return keysDown[k.ordinal()]; }
    public static boolean isKeyPressed(Key k) { return keysPressed[k.ordinal()]; }

    public static boolean isButtonDown(Button b) { return buttonsDown[b.ordinal()]; }
    public static boolean isButtonClicked(Button b) { return buttonsClicked[b.ordinal()]; }

    public static Vector2f getMousePos() {
        glfwGetCursorPos(windowHandle, xBuffer, yBuffer);
        Vector2f p = new Vector2f((float)xBuffer.get(0), (float)yBuffer.get(0));
        p.div(scale);
        return p;
    }

    public static float getScroll() {
        return scroll;
    }

    public static boolean scrolledThisFrame() {
        return scrolledThisFrame;
    }
}
