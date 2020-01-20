package main;

import java.util.ArrayList;

public class ParticleController {
    private ArrayList<Particle> particles;

    private int particleSize;

    private boolean paused;

    public ParticleController(int particleSize, boolean paused) {
        this.particleSize = particleSize;
        this.paused = paused;
    }

    //Setters and Mutators

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

    //Getters for all methods
    public int getParticleSize() {
        return particleSize;
    }

    public boolean isPaused() {
        return paused;
    }

}
