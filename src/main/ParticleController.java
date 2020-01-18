package main;

import java.util.ArrayList;

public class ParticleController {
    private ArrayList<Particle> particles;

    private int particleSize;

    public ParticleController(int particleSize) {
        this.particleSize = particleSize;
    }

    //Setters and Mutators
    public void setParticleSize(int particleSize) {
        this.particleSize = particleSize;
    }

    public void incParticleSize(int particleSize) {
        if(particleSize < 0)
            this.particleSize += (this.particleSize > 50) ? particleSize : 0;
        else this.particleSize += particleSize;
    }

    //Getters for all methods
    public int getParticleSize() {
        return particleSize;
    }
}
