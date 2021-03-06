package main;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ParticleController {

    private ArrayList<Particle> particles;
    private int particleSize;
    private double gravConstant;
    private double dampening;

    private boolean paused;
    private boolean drawParticles;
    private boolean showData;

    private double scale;
    private Point2D displacement;
    private double timeScale;



    public ParticleController(int particleSize, boolean paused, ArrayList<Particle> particles, double gravConstant, double dampening, double scale) {
        this.particleSize = particleSize;
        this.paused = paused;
        this.particles = particles;
        this.gravConstant = gravConstant;
        this.dampening = dampening;

        this.scale = scale;
//        this.timeScale = 1/(gravConstant*1000000);
        this.timeScale = 1/(gravConstant);
        this.displacement = new Point2D(0, 0);
        this.showData = false;
    }

    //Setters and Mutators
    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void destroyParticle(int i) {
        particles.remove(particles.get(i));
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void incScale(double scale) {
        this.scale += scale;
    }

    public void incDisplacement(double x, double y) {
        displacement = displacement.add(x, y);
    }

    //Particle sizes
    public void setParticleSize(int particleSize) {
        this.particleSize = particleSize;
    }

    public void incParticleSize(int particleSize) {
        if(particleSize < 0)
            this.particleSize += (this.particleSize > 50) ? particleSize : 0;
        else this.particleSize += particleSize;
    }


    //Boolean Toggles
    public void unpause() {
        paused = !paused;
    }

    public void pause() {
        paused = true;
    }

    public void flipDrawParticles() {
        drawParticles = !drawParticles;
    }

    public void flipShowData() {
        showData = !showData;
    }

    //Getters for all methods
    public int getParticleSize() {
        return particleSize;
    }

    public void setDisplacement(Point2D displacement) {
        this.displacement = displacement;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isDrawParticles() {
        return drawParticles;
    }

    public double getScale() {
        return scale;
    }

    public double getTimeScale() {
        return timeScale;
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public int getParticleNumber() {
        return particles.size();
    }

    public Particle getParticle(int i) {
        return particles.get(i);
    }

    public double getGravConstant() {
        return gravConstant;
    }

    public double getDampening() {
        return dampening;
    }

    public Point2D getDisplacement() {
        return displacement;
    }

    public boolean isShowData() {
        return showData;
    }

    public void addRandomParticle() {
        Main.p.addParticle(new Particle(
                (int) Main.rand(2,200), //MASS
                Color.BLACK, //COLOR
                    /*new Point2D(rand(
                            (d.getScreenWidth()-d.getScreenHeight())/2,
                            (d.getScreenWidth()-(d.getScreenWidth()-d.getScreenHeight())/2)
                    ), rand(0, d.getScreenHeight()))*/
                new Point2D(Main.rand(
                        Main.d.getScreenWidth() * -5,
                        Main.d.getScreenWidth() * 5
                ), Main.rand(Main.d.getScreenHeight()*-5, Main.d.getScreenHeight()*5))
        ));
    }
}