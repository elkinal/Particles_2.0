package main;

public class Display {
    private int screenHeight;
    private int screenWidth;
    private double scale;

    private long deltaTime;
    private long frames;

    public Display() {

    }

    public Display(int screenWidth, int screenHeight, double scale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scale = scale;
    }

    //Setters and Mutators
    public void incFrames() {
        frames++;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
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

    public long getFrames() {
        return frames;
    }
}
