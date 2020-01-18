package main;

public class Display {
    private int screenHeight;
    private int screenWidth;
    private double scale;

    private long deltaTime;
    private long frames;

    private boolean drawMesh;

    public Display() {

    }

    public Display(int screenWidth, int screenHeight, double scale, boolean drawMesh) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scale = scale;
        this.drawMesh = drawMesh;
    }

    //Setters and Mutators
    public void incFrames() {
        frames++;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    public void setDrawMesh(boolean drawMesh) {
        this.drawMesh = drawMesh;
    }

    public void flipDrawMesh() {
        this.drawMesh = !this.drawMesh;
    }

    //Getters for all the variables
    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public double getScale() {
        return scale;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public boolean isDrawMesh() {
        return drawMesh;
    }
    public long getFrames() {
        return frames;
    }
}
