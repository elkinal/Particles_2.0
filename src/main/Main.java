package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    // TODO: 12/02/2020 A Scale must be added

    public static Display d;
    public static ParticleController p = new ParticleController(100, false, new ArrayList<Particle>(), 6.67430 * Math.pow(10, -11), 0.00000001);

    Point2D[] particlePositions = new Point2D[2];

    public static boolean drawPath = false;

    //All graphics are drawn using the GraphicsContext
    private GraphicsContext gc;

    //Formation of binary stars vs one thicc cluster

    @Override
    public void start(Stage primaryStage) throws Exception{

        d = new Display((int) Screen.getPrimary().getBounds().getWidth(),
                (int) Screen.getPrimary().getBounds().getHeight(), 1, false, true);



        //TESTING AREA
        //F=MA TEST ---------------------------------------------------------
        /*particles.add(new Particle(300, Color.RED, new Point2D(500, 500)));
        particles.add(new Particle(100, Color.RED, new Point2D(800, 500)));
        particles.add(new Particle(100, Color.RED, new Point2D(1000, 500)));
        particles.add(new Particle(100, Color.BLUE, new Point2D(650, 500)));
        particles.get(0).setVelocity(new Point2D(1, 0));
        particles.get(1).setVelocity(new Point2D(-1, 0));
        particles.get(2).setVelocity(new Point2D(-1, 0));
        particles.get(3).setVelocity(new Point2D(-1, 0));*/

        //BROKEN ORBITALS TEST -------------------------------------------------
/*        particles.add(new Particle(100000, Color.RED, new Point2D(1000, 500)));

        particles.add(new Particle(1000000, Color.RED, new Point2D(600, 700)));*/


        // TODO: 18/02/2020 add temperature sensitivity scaling. allow the user to change the sensitivity of the temperature.

        //RANDOM PARTICLE TEST
        for (int i = 0; i < 100; i++) { // TODO: 13/02/2020 Scale this appropriately
            p.addParticle(new Particle(
                    (int) rand(2,200), //MASS
                    Color.BLACK, //COLOR
                    /*new Point2D(rand(
                            (d.getScreenWidth()-d.getScreenHeight())/2,
                            (d.getScreenWidth()-(d.getScreenWidth()-d.getScreenHeight())/2)
                    ), rand(0, d.getScreenHeight()))*/
                    new Point2D(rand( //Adding particles beyond the screen
                            Main.d.getScreenWidth() * -5,
                            Main.d.getScreenWidth() * 5
                    ), rand(Main.d.getScreenHeight()*-5, d.getScreenHeight()*5))
            ));
        }


        //Forces the game to be played full-screen
        primaryStage.setTitle("N-Body Simulation");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        //Structural stuff for the window
        Group root = new Group();
        Scene scene = new Scene(root);



        //Responding to Keystrokes
        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.SPACE)
                p.unpause();
            if(event.getCode() == KeyCode.M)
                d.flipDrawMesh();
            if(event.getCode() == KeyCode.T)
                p.flipDrawParticles();
            if(event.getCode() == KeyCode.Q)
                p.flipShowData();

            if(event.getCode() == KeyCode.E)
                p.incParticleSize(100);
            if(event.getCode() == KeyCode.Q)
                p.incParticleSize(-100);

            if(event.getCode() == KeyCode.W)
                p.incDisplacement(0, -20);
            if(event.getCode() == KeyCode.A)
                p.incDisplacement(-20, 0);
            if(event.getCode() == KeyCode.S)
                p.incDisplacement(0, 20);
            if(event.getCode() == KeyCode.D)
                p.incDisplacement(20, 0);
        });


        //Responding to when a mouse button is pressed
        scene.setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                p.addParticle(new Particle(p.getParticleSize(), Color.BLUE, new Point2D(event.getX()/p.getScale() - p.getDisplacement().getX()/p.getScale(), event.getY()/p.getScale() - p.getDisplacement().getY()/p.getScale())));
            else if(event.getButton() == MouseButton.SECONDARY) {
                particlePositions[0] = new Point2D(event.getX()/p.getScale() - p.getDisplacement().getX()/p.getScale(), event.getY()/p.getScale() - p.getDisplacement().getY()/p.getScale());
                drawPath = true;
            }
        });

        //Responding to when the right mouse button is released
        scene.setOnMouseReleased(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                particlePositions[1] = new Point2D(event.getX()/p.getScale() - p.getDisplacement().getX()/p.getScale(), event.getY()/p.getScale() - p.getDisplacement().getY()/p.getScale());
                drawPath = false;
                p.addParticle(new Particle(p.getParticleSize(), Color.DARKBLUE, particlePositions[0], new Point2D(
                        ((particlePositions[1].getX() - particlePositions[0].getX()) / (50)) * p.getGravConstant(),
                        ((particlePositions[1].getY() - particlePositions[0].getY()) / (50)) * p.getGravConstant()
                )));
            }
        });


        //Allowing the user to change the size of the created particle using the scroll wheel
        scene.setOnScroll(event -> {
            if(event.getDeltaY() < 0) {
                p.incScale(0.01);
                p.incDisplacement(0, -d.getScreenHeight()*0.005);
                p.incDisplacement(-d.getScreenWidth()*0.005, 0);
            }
            else if (p.getScale() > 0.01) {
                p.incScale(-0.01);
                p.incDisplacement(0, d.getScreenHeight()*0.005);
                p.incDisplacement(d.getScreenWidth()*0.005, 0);
            }
        });


        //Graphical operations
        primaryStage.setScene(scene);
        Canvas canvas = new Canvas(d.getScreenWidth(), d.getScreenHeight());
        root.getChildren().add(canvas);
        gc = canvas.getGraphicsContext2D();


        //This is the main game loop - everything is controlled from here
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long lastFrameTime = System.nanoTime(); //stores the time before each frame
                update(); //handles all of the calculations
                render(gc); //handles all of the graphical operations
                d.setDeltaTime(System.nanoTime() - lastFrameTime); //stores the time taken for each frame
            }
        }.start();
        primaryStage.show();
    }


    private void render(GraphicsContext graphics) {

        //Clearing the screen
        graphics.clearRect(0, 0, d.getScreenWidth(), d.getScreenHeight());

        //Drawing all of the Particles
        p.getParticles().forEach(p -> p.draw(graphics));

        //Drawing the average FPS in the corner of the screen
        graphics.setFill(Color.GREEN);
        graphics.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        graphics.fillText("FPS: " + getFPS(), d.getScreenWidth()-270, 12);
        graphics.fillText("Particles: " + p.getParticleNumber(), d.getScreenWidth()-270, 24);
        graphics.fillText("Time Scaling: x" + p.getTimeScale(), d.getScreenWidth()-270, 36);

/*        //Drawing a line to show the path a particle will take when the user creates a particle with an initial velocity
        if(drawPath)
            drawPathLine(graphics);*/


        //Drawing the mesh
        if(d.isDrawMesh())
            drawMesh(graphics);

/*        if(d.isDrawPoles());
            drawPoles(graphics);*/

    }

    /*
    Turn the displacement into an object with parameters representing the actual displacement along with the displacement affected by the screen dimensions
     */

/*    private void drawPoles(GraphicsContext graphics) {
        graphics.setLineWidth(1);
        graphics.setStroke(Color.PURPLE);
        graphics.strokeLine(
                0, d.getScreenHeight()/2 + p.getDisplacement().getY(), d.getScreenWidth(), d.getScreenHeight()/2 + p.getDisplacement().getY()//Horizontal Line
        );
        graphics.strokeLine(
                d.getScreenWidth()/2 + p.getDisplacement().getX(), 0, d.getScreenWidth()/2 + p.getDisplacement().getX(), d.getScreenHeight()
        );
    }*/

    private void drawMesh(GraphicsContext graphics) {
        graphics.setLineWidth(0.5);
        int particleNumber = p.getParticleNumber();

        for (int i = 0; i < particleNumber; i++) {
            for (int j = 0; j < particleNumber; j++) {
                if(p.getParticle(i) != p.getParticle(j)) {

                    graphics.setStroke(Color.GREEN);
                    graphics.strokeLine(
                            p.getParticle(i).getScaledLocation().getX() + p.getDisplacement().getX(),
                            p.getParticle(i).getScaledLocation().getY() + p.getDisplacement().getY(),
                            p.getParticle(j).getScaledLocation().getX() + p.getDisplacement().getX(),
                            p.getParticle(j).getScaledLocation().getY() + p.getDisplacement().getY()
                    );

                }
            }
        }
        graphics.setLineWidth(1);
    }

/*    private void drawPathLine(GraphicsContext graphics) {
        graphics.setStroke(Color.RED);
        graphics.strokeLine( // TODO: 11-Jul-19 This function makes the screen freeze for a few milliseconds when it is run
                particlePositions[0].getX(), particlePositions[0].getY(),
                particlePositions[0].getX() - (particlePositions[0].getX() - MouseInfo.getPointerInfo().getLocation().getX())/5,
                particlePositions[0].getY() - (particlePositions[0].getY() - MouseInfo.getPointerInfo().getLocation().getY())/5
        );
    }*/

    private double getFPS() {
        return Math.round(1/(d.getDeltaTime()+Float.MIN_VALUE) * 10000000 * 10000.0)/1000.0;
    }

    private void update() {
        //All calculations go here
        //Physics Calculations - MOVEMENT
        if(!p.isPaused()) {
            for (int i = 0; i < p.getParticleNumber(); i++) {
                for (int j = 0; j < p.getParticleNumber(); j++) {
                    //Physics Calculations - COLLISION DETECTION
                    // TODO: 05-Jul-19 unexpected ArrayIndexOutOfBoundsException can rarely arise in the collision detection part -------------------------------------
                    if ((p.getParticle(j).getLocation().distance(p.getParticle(i).getLocation())) // TODO: 12/02/2020 SCALING NEEDS TO BE ADDED ON THIS LINE
                            < (p.getParticle(j).getRealDimensions() / 2 + p.getParticle(i).getRealDimensions() / 2)
                            && p.getParticle(i) != p.getParticle(j)) {

                        if (p.getParticle(j).getMass() > p.getParticle(i).getMass()) {
                            //Larger particle changes its trajectory according to Newton's Third Law
                            p.getParticle(j).setVelocity(
                                    new Point2D(
                                            (p.getParticle(j).getMass() * p.getParticle(j).getVelocity().getX() +
                                                    p.getParticle(i).getMass() * p.getParticle(i).getVelocity().getX()) /
                                                    (p.getParticle(j).getMass() + p.getParticle(i).getMass()),

                                            (p.getParticle(j).getMass() * p.getParticle(j).getVelocity().getY() +
                                                    p.getParticle(i).getMass() * p.getParticle(i).getVelocity().getY()) /
                                                    (p.getParticle(j).getMass() + p.getParticle(i).getMass())
                                    )
                            );
                            //Larger particle absorbs smaller particle
                            double initialKineticEnergy = p.getParticle(i).getKE() + p.getParticle(j).getKE();

                            p.getParticle(j).addMass(p.getParticle(i).getMass()); // TODO: 11/02/2020 turn the kinetic energy into temperature

                            double finalKineticEnergy = p.getParticle(j).getKE();
                            p.getParticle(j).incTemperature((initialKineticEnergy - finalKineticEnergy)/(p.getParticle(j).getMass()*1000));

                            p.destroyParticle(i);
                        } else {

                            //Larger particle changes its trajectory according to Newton's Third Law
                            p.getParticle(i).setVelocity(
                                    new Point2D(
                                            (p.getParticle(j).getMass() * p.getParticle(j).getVelocity().getX() +
                                                    p.getParticle(i).getMass() * p.getParticle(i).getVelocity().getX()) /
                                                    (p.getParticle(i).getMass() + p.getParticle(j).getMass()),

                                            (p.getParticle(j).getMass() * p.getParticle(j).getVelocity().getY() +
                                                    p.getParticle(i).getMass() * p.getParticle(i).getVelocity().getY()) /
                                                    (p.getParticle(i).getMass() + p.getParticle(j).getMass())
                                    )
                            );
                            //Larger particle absorbs smaller particle
                            double initialKineticEnergy = p.getParticle(i).getKE() + p.getParticle(j).getKE();

                            p.getParticle(i).addMass(p.getParticle(j).getMass());

                            double finalKineticEnergy = p.getParticle(i).getKE();
                            p.getParticle(i).incTemperature((initialKineticEnergy - finalKineticEnergy)/(p.getParticle(i).getMass()*1000));

                            p.destroyParticle(j);
                        }
//                        p.addRandomParticle(); //Adding a replacement particle whenever a particle is destroyed

                    }
                    else {
                        //This calculates the total force between two particles. If the two particles are the same, the returned force is -1
                        // TODO: 04-Jul-19 There seems to be an element of randomness in the way the particles behave. This should not be the case. This is almost certainly linked to the variation in the FPS
                        //EXPERIMENTAL CODE ------------------------------------------------------------------------------------
                        // TODO: 06/01/2020 try eliminating negative values from atan2(), and give them back with signum()
                        double xDifference = p.getParticle(i).getLocation().getX() - p.getParticle(j).getLocation().getX();
                        double yDifference = p.getParticle(i).getLocation().getY() - p.getParticle(j).getLocation().getY();

                        double force = 0;
                        double xF = 0;
                        double yF = 0;

                        if(p.getParticle(i) != p.getParticle(j)) {

                            force = p.getGravConstant() * (p.getParticle(i).getMass() * p.getParticle(j).getMass() /
                                    (Math.pow(p.getParticle(j).getLocation().distance(p.getParticle(i).getLocation()), 2) + p.getDampening())); //always positive

                            double alpha = Math.atan2(xDifference, yDifference); //only place where minus could arise
                            double angle = Math.toDegrees(alpha);

                            xF = force * Math.sin(alpha);
                            yF = force * Math.cos(alpha);

                            p.getParticle(j).accelerate(new Point2D(xF/p.getParticle(j).getMass(), yF/p.getParticle(j).getMass()));

                            // TODO: 06/01/2020 The xF and yF seem to reverse randomly when two particles are remaining
                            /**
                             * This effect only happens when a foreign particle is placed
                             * The repulsion only happens between the large particle and the foreign particles
                             * THE ERROR ONLY HAPPENS WHEN THE FOREIGN PARTICLE IS LARGER THAN A CERTAIN SIZE
                             * THE ERROR OCCURS WHEN TWO PARTICLES JOIN WHILE ATTRACTED TO A THIRD
                             * does it only happen at a certain size, or is it only noticeable at a certain size
                             * for this interaction to occur, 3 particles need to reorder
                             *
                             * The angle is always calculated correctly*/
                        }
                    }
                }
            }
        }



        //Ticking the Particles
        p.getParticles().forEach(p -> p.tick());

        //Incrementing the number of elapsed frames (for development purposes)
        d.incFrames();
    }









    //Generates a random number between "max" and "min"
    public static float rand(float min, float max) {
        return new Random().nextInt((int) (max - min + 1)) + min;
    }

    //Launches the Main function
    public static void main(String[] args) {
        launch(args);
    }

}