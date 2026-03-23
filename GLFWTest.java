import org.lwjgl.glfw.GLFW;

public class GLFWTest {
    public static void main(String[] args) {
        System.out.println("Attempting to initialize GLFW...");
        try {
            if (!GLFW.glfwInit()) {
                System.err.println("GLFW.glfwInit() returned false.");
                System.exit(1);
            }
            System.out.println("GLFW initialized successfully!");
            GLFW.glfwTerminate();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
