package main;

/**
 * Created by alxye on 02-Jul-19.
 */
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static java.lang.Math.*;

public class Particle {
    private int mass;
    private Color color;
    private Point2D location;
    private Point2D velocity;
    private float temperature;

    public Particle(int mass, Color color, Point2D location) {
        this.mass = mass;
        this.color = color;
        this.location = location;
        this.velocity = new Point2D(0, 0);
        this.temperature = 0; // The initial temperature of each particle is zero
    }

    //A constructor that includes the velocity as well as all of the other parameters
    public Particle(int mass, Color color, Point2D location, Point2D velocity) {
        this.mass = mass;
        this.color = color;
        this.location = location;
        this.velocity = velocity;
    }

    //Getting the diameter of the particle (assuming it is 3D) // TODO: 03-Jul-19 double-check if this is correct
    public double getDimensions() {
        return 2 * cbrt(mass * ((float)3/4) * PI) * Main.p.getScale();
    }

    public double getRealDimensions() {
        return 2 * cbrt(mass * ((float)3/4) * PI);
    }

    public Point2D getCenterLocation() {
        return new Point2D(location.getX() - getDimensions()/2, location.getY() - getDimensions()/2);
    }

    public Point2D getScaledCenterLocation() {
        return new Point2D(location.getX()*Main.p.getScale() - getDimensions()/2, location.getY()*Main.p.getScale() - getDimensions()/2);
    }

    //Getter and Setter for the location of the particle
    public Point2D getLocation() {
        return location;
    }

    public Point2D getScaledLocation() {
        return new Point2D(location.getX() * Main.p.getScale(), location.getY() * Main.p.getScale());
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    //Getter for the mass of the particle
    public int getMass() {
        return mass;
    }

    //Adder for the mass of the particle
    public void addMass(double mass) {
        this.mass += mass;
    }

    //Getter for the temperature
    public float getTemperature() {
        return temperature;
    }

    //Setter for the temperature
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    //Mutator incrementer for the temperature
    public void incTemperature(double temperature) {
        this.temperature += temperature;
    }

    //This function changes the velocity off the particle
    public void accelerate(Point2D velocity) {
        this.velocity = this.velocity.add(velocity);
    }

    //Setter for the velocity
    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    //Getter for the velocity
    public Point2D getVelocity() {
        return this.velocity;
    }

    //Getter for the kinetic energy
    public double getKE() {
        return 0.5 * mass * pow(velocity.magnitude(), 2);
    }

    //Checks if the position of the particle is within the boundaries of the screen
    private boolean onScreen() {
        return !(location.getX() + getDimensions() < 0 || location.getX() - getDimensions() > Main.d.getScreenWidth()/Main.p.getScale()
                || location.getY() + getDimensions() < 0 || location.getY() - getDimensions() > Main.d.getScreenHeight()/Main.p.getScale());
    }
    //This method contains all the calculations performed on the particle each frame
    public void tick() {
        //Accelerating the particle
        if(!Main.p.isPaused()) {
            location = location.add(velocity); // TODO: 06/01/2020 Implement DELTATIME
//            location = location.add(velocity.getX() * Main.deltaTime/1000000, velocity.getY() * Main.deltaTime/1000000);

//            System.out.println(location);
        }
    }



    //This method draws the particle on the GraphicsContext each frame
    public void draw(GraphicsContext graphics) {
        if(onScreen()) {
            graphics.setFill(Paint.valueOf("black"));
            graphics.fillOval(getScaledCenterLocation().getX() + Main.p.getDisplacement().getX(), getScaledCenterLocation().getY() + Main.p.getDisplacement().getY(), getDimensions(), getDimensions());


            //Drawing the temperature of the particle next to it
            if(Main.p.isDrawParticles()) {
                graphics.setFill(Paint.valueOf("red"));
                graphics.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
                graphics.fillText(String.valueOf(temperature) + "K", getScaledCenterLocation().getX(), getScaledCenterLocation().getY());
            }
/*            System.out.println(Main.p.getParticles().get(0));
            System.out.println(Main.p.getParticles().get(1));*/
        }
    }
}