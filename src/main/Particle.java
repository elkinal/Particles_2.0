package main;

/**
 * Created by alxye on 02-Jul-19.
 */
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static java.lang.Math.*;

public class Particle {
    private int mass;
    private Color color;
    private Point2D location;
    private Point2D velocity;

    public Particle(int mass, Color color, Point2D location) {
        this.mass = mass;
        this.color = color;
        this.location = location;
        this.velocity = new Point2D(0, 0);
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
        return 2 * cbrt(mass * ((float)3/4) * PI) * Main.d.getScale();
    }

    public Point2D getCenterLocation() {
        return new Point2D(location.getX() - getDimensions()/2, location.getY() - getDimensions()/2);
    }

    //Getter and Setter for the location of the particle
    public Point2D getLocation() {
        return location;
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

    //Checks if the position of the particle is within the boundaries of the screen
    private boolean onScreen() {
        return !(location.getX() + getDimensions() < 0 || location.getX() - getDimensions() > Main.d.getScreenWidth()
                || location.getY() + getDimensions() < 0 || location.getY() - getDimensions() > Main.d.getScreenHeight());
    }
    //This method contains all the calculations performed on the particle each frame
    public void tick() {
        //Accelerating the particle
        if(!Main.paused) {
            location = location.add(velocity); // TODO: 06/01/2020 Implement DELTATIME
//            location = location.add(velocity.getX() * Main.deltaTime/1000000, velocity.getY() * Main.deltaTime/1000000);

//            System.out.println(location);
        }
    }



    //This method draws the particle on the GraphicsContext each frame
    public void draw(GraphicsContext graphics) {
        if(onScreen()) {
            graphics.setFill(Paint.valueOf("black"));
            graphics.fillOval(getCenterLocation().getX(), getCenterLocation().getY(), getDimensions(), getDimensions());
        }
    }

}