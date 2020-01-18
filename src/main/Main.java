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

    public static Display d;
    public static ParticleController p = new ParticleController(100);

    //This list stores all of particles in the entire simulation todo [ParticleController]
    private ArrayList<Particle> particles = new ArrayList<>();

    //The size of the particle created when the user clicks on the screen todo [ParticleController]
//    public static int particleSize = 100;

    //Stores the points at which the user clicks and releases the right mouse button respectively todo [ParticleController]
    Point2D[] particlePositions = new Point2D[2];

    //Self-Explanatory todo [ParticleController]
    public static boolean paused = false;
    public static boolean drawPath = false;

    //Gravitational Constant todo [ParticleController]
    public static double GRAV_CONSTANT = 1;
    public static final double DAMPENING = 0.00000001;



    //All graphics are drawn using the GraphicsContext
    private GraphicsContext gc;



    @Override
    public void start(Stage primaryStage) throws Exception{
        //Detecting the screen width and height of the monitor to make the game more responsive
        /*SCREENHEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
        SCREENWIDTH = (int) Screen.getPrimary().getBounds().getWidth();*/

        d = new Display((int) Screen.getPrimary().getBounds().getWidth(),
                (int) Screen.getPrimary().getBounds().getHeight(), 1, false);


//        System.out.println(d.getScreenWidth() + " - " + d.getScreenHeight());
        //TESTING AREA
        /*particles.add(new Particle(300, Color.RED, new Point2D(500, 500)));
        particles.add(new Particle(100, Color.RED, new Point2D(800, 500)));
        particles.add(new Particle(100, Color.RED, new Point2D(1000, 500)));
        particles.add(new Particle(100, Color.BLUE, new Point2D(650, 500)));
        particles.get(0).setVelocity(new Point2D(1, 0));
        particles.get(1).setVelocity(new Point2D(-1, 0));
        particles.get(2).setVelocity(new Point2D(-1, 0));
        particles.get(3).setVelocity(new Point2D(-1, 0));*/

        //ORBITALS TEST
/*        particles.add(new Particle(100000, Color.RED, new Point2D(1000, 500)));

        particles.add(new Particle(1000000, Color.RED, new Point2D(600, 700)));*/


        //RANDOM PARTICLE TEST
        for (int i = 0; i < 200; i++) {
            particles.add(new Particle((int)rand(2,2), Color.BLACK, new Point2D(rand(0, d.getScreenWidth()), rand(0, d.getScreenHeight()))));
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
                paused = !paused;
            if(event.getCode() == KeyCode.M)
                d.flipDrawMesh();
        });


        //Responding to when a mouse button is pressed
        scene.setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                particles.add(new Particle(p.getParticleSize(), Color.BLUE, new Point2D(event.getX(), event.getY())));
            else if(event.getButton() == MouseButton.SECONDARY) {
                particlePositions[0] = new Point2D(event.getX(), event.getY());
                drawPath = true;
            }
        });

        //Responding to when the right mouse button is released
        scene.setOnMouseReleased(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                particlePositions[1] = new Point2D(event.getX(), event.getY());
                drawPath = false;
                particles.add(new Particle(p.getParticleSize(), Color.DARKBLUE, particlePositions[0], new Point2D(
                        (particlePositions[1].getX() - particlePositions[0].getX()) / 50,
                        (particlePositions[1].getY() - particlePositions[0].getY()) / 50
                )));
            }
        });


        //Allowing the user to change the size of the created particle using the scroll wheel
        scene.setOnScroll(event -> {
            if(event.getDeltaY() < 0)
                p.incParticleSize(50);
            else
                p.incParticleSize(-50); // TODO: 18/01/2020 Mutator protection is needed here
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
        particles.forEach(p -> p.draw(graphics));

        //Drawing the average FPS in the corner of the screen
        graphics.setFill(Color.GREEN);
        graphics.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        graphics.fillText("FPS: " + getFPS(), d.getScreenWidth()-65, 12);
        graphics.fillText("Particles: " + particles.size(), d.getScreenWidth()-100, 24);

        //Drawing a line to show the path a particle will take when the user creates a particle with an initial velocity
        if(drawPath)
            drawPathLine(graphics);


        //Drawing the mesh
        if(d.isDrawMesh())
            drawMesh(graphics);

    }

    private void drawMesh(GraphicsContext graphics) {
        graphics.setLineWidth(0.5);
        for (int i = 0; i < particles.size(); i++) {
            for (int j = 0; j < particles.size(); j++) {
                if(particles.get(i) != particles.get(j)) {
                    graphics.setStroke(Color.GREEN);
                    graphics.strokeLine(particles.get(i).getLocation().getX(), particles.get(i).getLocation().getY(), particles.get(j).getLocation().getX(), particles.get(j).getLocation().getY());
                }
            }
        }
        graphics.setLineWidth(1);
    }

    private void drawPathLine(GraphicsContext graphics) {
        graphics.setStroke(Color.RED);
        graphics.strokeLine( // TODO: 11-Jul-19 This function makes the screen freeze for a few milliseconds when it is run
                particlePositions[0].getX(), particlePositions[0].getY(),
                particlePositions[0].getX() - (particlePositions[0].getX() - MouseInfo.getPointerInfo().getLocation().getX())/5,
                particlePositions[0].getY() - (particlePositions[0].getY() - MouseInfo.getPointerInfo().getLocation().getY())/5
        );
    }

    private double getFPS() {
        return Math.round(1/(d.getDeltaTime()+Float.MIN_VALUE) * 10000000 * 10000.0)/1000.0;
    }

    private void update() {
        //All calculations go here
        //Physics Calculations - MOVEMENT
        if(!paused) {
            for (int i = 0; i < particles.size(); i++) {
                for (int j = 0; j < particles.size(); j++) {
                    //Physics Calculations - COLLISION DETECTION
                    // TODO: 05-Jul-19 unexpected ArrayIndexOutOfBoundsException can rarely arise in the collision detection part -------------------------------------
                    if (particles.get(j).getLocation().distance(particles.get(i).getLocation())
                            < (particles.get(j).getDimensions() / 2 + particles.get(i).getDimensions() / 2)
                            && particles.get(i) != particles.get(j)) {
                        if (particles.get(j).getMass() > particles.get(i).getMass()) {
                            //Larger particle changes its trajectory according to Newton's Third Law
                            particles.get(j).setVelocity(
                                    new Point2D(
                                            (particles.get(j).getMass() * particles.get(j).getVelocity().getX() +
                                                    particles.get(i).getMass() * particles.get(i).getVelocity().getX()) /
                                                    (particles.get(j).getMass() + particles.get(i).getMass()),

                                            (particles.get(j).getMass() * particles.get(j).getVelocity().getY() +
                                                    particles.get(i).getMass() * particles.get(i).getVelocity().getY()) /
                                                    (particles.get(j).getMass() + particles.get(i).getMass())
                                    )
                            );
                            //Larger particle absorbs smaller particle
                            particles.get(j).addMass(particles.get(i).getMass());
                            particles.remove(particles.get(i));
                        } else {

                            //Larger particle changes its trajectory according to Newton's Third Law
                            particles.get(i).setVelocity(
                                    new Point2D(
                                            (particles.get(j).getMass() * particles.get(j).getVelocity().getX() +
                                                    particles.get(i).getMass() * particles.get(i).getVelocity().getX()) /
                                                    (particles.get(i).getMass() + particles.get(j).getMass()),

                                            (particles.get(j).getMass() * particles.get(j).getVelocity().getY() +
                                                    particles.get(i).getMass() * particles.get(i).getVelocity().getY()) /
                                                    (particles.get(i).getMass() + particles.get(j).getMass())
                                    )
                            );
                            //Larger particle absorbs smaller particle
                            particles.get(i).addMass(particles.get(j).getMass());
                            particles.remove(particles.get(j));
                        }

                    }
                    else {
                        //This calculates the total force between two particles. If the two particles are the same, the returned force is -1
                        // TODO: 04-Jul-19 There seems to be an element of randomness in the way the particles behave. This should not be the case. This is almost certainly linked to the variation in the FPS
                        //EXPERIMENTAL CODE ------------------------------------------------------------------------------------
                        // TODO: 06/01/2020 try eliminating negative values from atan2(), and give them back with signum()
                        double xDifference = particles.get(i).getLocation().getX() - particles.get(j).getLocation().getX();
                        double yDifference = particles.get(i).getLocation().getY() - particles.get(j).getLocation().getY();

                        double force = 0;
                        double xF = 0;
                        double yF = 0;

                        if(particles.get(i) != particles.get(j)) {

                            force = GRAV_CONSTANT * (particles.get(i).getMass() * particles.get(j).getMass() /
                                    (Math.pow(particles.get(j).getLocation().distance(particles.get(i).getLocation()), 2) + DAMPENING)); //always positive

                            double alpha = Math.atan2(xDifference, yDifference); //only place where minus could arise
                            double angle = Math.toDegrees(alpha);

                            xF = force * Math.sin(alpha); //Don't even know why this works properly
                            yF = force * Math.cos(alpha);

                            particles.get(j).accelerate(new Point2D(xF/particles.get(j).getMass(), yF/particles.get(j).getMass()));

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
        particles.forEach(p -> p.tick());

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