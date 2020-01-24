package main;

import java.util.ArrayList;

public class ParticleController {
    private ArrayList<Particle> particles;

    private int particleSize;

    private boolean paused;

    public ParticleController(int particleSize, boolean paused, ArrayList<Particle> particles) {
        this.particleSize = particleSize;
        this.paused = paused;
        this.particles = particles;
    }

    //Setters and Mutators
    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void destroyParticle(int i) {
        particles.remove(particles.get(i));
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

    //Getters for all methods
    public int getParticleSize() {
        return particleSize;
    }

    public boolean isPaused() {
        return paused;
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


}
