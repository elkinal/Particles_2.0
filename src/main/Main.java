package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main extends Application {

    // TODO: 12/02/2020 A Scale must be added + occasional moderate FPS drops. Most significant when there are only two objects orbiting consistently in a stable orbit
    // TODO: 26/02/2020 There is an issue with the border detection which sometimes prevents particles from rendering when they should

    /**
     * How the decrease in the number of particles varies over time
     * How the initial speed of the particles affect the decrease in particles over time
     * How the initial speed of the particles affects the probability of a binary star system developing
     * Conditions for orbits and binary interactions
     * */

    public static Display d;
    public static ParticleController p = new ParticleController(100, false, new ArrayList<Particle>(), 6.67430 * Math.pow(10, -11), 0.00000001, 1);
    public static Data data = new Data();

    Point2D[] particlePositions = new Point2D[2];

    public static boolean drawPath = false;

    //All graphics are drawn using the GraphicsContext
    private GraphicsContext gc;

    //Formation of binary stars vs one thicc cluster
    // TODO: 03/03/2020 BUG FIXES
    private static int framesToRecord = 100;
    private static final int timesToRecord = 21;
    private static int timesRecorded = 0;
    public static final String[][] dataArray = new String[timesToRecord+1][framesToRecord+2];
    private int ETA = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        d = new Display((int) Screen.getPrimary().getBounds().getWidth(),
                (int) Screen.getPrimary().getBounds().getHeight(), 1, false, true);





        //TESTING AREA
        //F=MA TEST ---------------------------------------------------------
/*        p.addParticle(new Particle(300, Color.RED, new Point2D(500, 500)));
        p.addParticle(new Particle(100, Color.RED, new Point2D(800, 500)));
        p.addParticle(new Particle(100, Color.RED, new Point2D(1000, 500)));
        p.addParticle(new Particle(100, Color.BLUE, new Point2D(650, 500)));
        p.getParticle(0).setVelocity(new Point2D(1, 0));
        p.getParticle(1).setVelocity(new Point2D(-1, 0));
        p.getParticle(2).setVelocity(new Point2D(-1, 0));
        p.getParticle(3).setVelocity(new Point2D(-1, 0));*/

        //BROKEN ORBITALS TEST -------------------------------------------------
/*        particles.add(new Particle(100000, Color.RED, new Point2D(1000, 500)));
        particles.add(new Particle(1000000, Color.RED, new Point2D(600, 700)));*/


        // TODO: 18/02/2020 add temperature sensitivity scaling. allow the user to change the sensitivity of the temperature.

        //RANDOM PARTICLE TEST
//        addParticles(timesRecorded);

        //todo MODELLING THE SOLAR SYSTEM
/*        p.setScale(1/Math.pow(10, 8));

        //Initial Displacement
        p.setDisplacement(new Point2D(d.getScreenWidth()/2, d.getScreenHeight()/2));

        //The SUN
        p.addParticle(
                new Particle(1.989 * Math.pow(10, 30), Color.RED, new Point2D(0, 0))
        );

        //The EARTH
        p.addParticle(
                new Particle(5.972 * Math.pow(10, 24), Color.RED, new Point2D(148.11 * Math.pow(10, 9), 0), new Point2D(0, 100))
        );*/


        //RECORDING THE INITIAL DATA OF THE EXPERIMENT
        /** Recording the Initial Kinetic and Thermal Energy*/
        double totalKE = 0;
        double totalTE = 0;
        double totalGPE = 0;

        for (int i = 0; i < p.getParticles().size(); i++) { //Getting the Kinetic and Thermal Energy
            totalKE += p.getParticle(i).getKE();
            totalTE += p.getParticle(i).getThermalEnergy();
        }

        data.setInitialKE(totalKE);
        data.setInitialTemperature(totalTE);
        data.setInitialGPE(totalGPE);








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
            if(event.getCode() == KeyCode.F)
                p.flipShowData();

            if(event.getCode() == KeyCode.E)
                p.incParticleSize(50);
            if(event.getCode() == KeyCode.Q)
                p.incParticleSize(-50);

            if(event.getCode() == KeyCode.W)
                p.incDisplacement(0, -20);
            if(event.getCode() == KeyCode.A)
                p.incDisplacement(-20, 0);
            if(event.getCode() == KeyCode.S)
                p.incDisplacement(0, 20);
            if(event.getCode() == KeyCode.D)
                p.incDisplacement(20, 0);

            if(event.getCode() == KeyCode.ESCAPE) { //At this point all of the useful data should be given to the user
                //RECORDING THE FINAL DATA OF THE EXPERIMENT
                /** Recording the Kinetic and Thermal Energy (final)*/
                double totalFKE = 0;
                double totalFTE = 0;

                for (int i = 0; i < p.getParticles().size(); i++) {
                    totalFKE += p.getParticle(i).getKE();
                    totalFTE += p.getParticle(i).getThermalEnergy();
                }

                data.setFinalKE(totalFKE);
                data.setFinalTemperature(totalFTE);

                /**Finding the Center of Mass*/
                Point2D total = new Point2D(0, 0);
                double sumOfMasses = 0;

                for (int i = 0; i < p.getParticles().size(); i++) {
                    total = total.add(p.getParticle(i).getMass() * p.getParticle(i).getCenterLocation().getX(), p.getParticle(i).getMass() * p.getParticle(i).getCenterLocation().getY());
                    sumOfMasses += p.getParticle(i).getMass();
                }

                Point2D centerOfMass = new Point2D(total.getX()/sumOfMasses, total.getY()/sumOfMasses); //The Center of Mass

                /**Calculating the Gravitational Potential Energy*/
                double GPE = 0;
                for (int i = 0; i < p.getParticles().size(); i++) {
                    GPE -= (p.getGravConstant() * p.getParticle(i).getMass() * sumOfMasses)/(p.getParticle(i).getCenterLocation().distance(centerOfMass));
                }

                System.out.println("GPE: " + GPE);





                System.out.println(centerOfMass);

                System.out.println(data);
            }
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

//        double scaleIncrease = 1/Math.pow(10, 10); //For solar system model
        double scaleIncrease = 0.01; //For particles model

/*        scene.setOnScroll(event -> {
            if(event.getDeltaY() < 0) {
                p.incScale(scaleIncrease);
                p.incDisplacement(0, -d.getScreenHeight()*(0.5*scaleIncrease));
                p.incDisplacement(-d.getScreenWidth()*(0.5*scaleIncrease), 0);
            }
            else {
                p.incScale(-scaleIncrease);
                p.incDisplacement(0, d.getScreenHeight()*(0.5*scaleIncrease));
                p.incDisplacement(d.getScreenWidth()*(0.5*scaleIncrease), 0);
            }
        });*/

        scene.setOnScroll(event -> {
            if(event.getDeltaY() < 0) {
                zoom(scaleIncrease, MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
            }
            else {
                zoom(-scaleIncrease, MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
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
        graphics.fillText("Size Scaling: x" + round(p.getScale(), 2), d.getScreenWidth()-270, 48);
        graphics.fillText("Frames Elapsed: " + d.getFrames(), d.getScreenWidth()-270, 60);
        graphics.fillText("ETA (sec): " + ETA, d.getScreenWidth()-270, 72);

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
        if(!p.isPaused())
            d.incFrames();


        /** This part is responsible for restarting the simulation when data has been collected
         * The simulation should restart when the number of particles is smaller than 3 or a certain number of frames has elapsed*/

        /** Recording data in the Array after every frame*/
        dataArray[timesRecorded][(int) d.getFrames()] = String.valueOf(p.getParticles().size()); //index=frames, value=particle number

/*        //Updating the ETA
        if(d.getFrames() % 300 == 0)
            ETA = (int) ((framesToRecord - d.getFrames()) / getFPS());*/

        if(p.getParticles().size() < 3 || d.getFrames() > framesToRecord){
            p.getParticles().clear();
            addParticles(timesRecorded);

            /**Number of particles, time (by frame), should be recorded in CSV*/
            writeData();

            d.resetFrames();

            timesRecorded++;
        }
        if(timesRecorded >= timesToRecord)
            p.pause();
    }


    // TODO: 24/02/2020 All the maths methods should be extracted to another class
    //Generates a random number between "max" and "min"
    public static float rand(float min, float max) {
        return new Random().nextInt((int) (max - min + 1)) + min;
    }

    public static float randSign() {
        return Math.random() > 0.5 ? -1 : 1;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void zoom(double amount, double toX, double toY) {
        double oldZ = p.getScale();
        p.incScale(amount);

        double mouse_x = toX-p.getDisplacement().getX();
        double mouse_y = toY-p.getDisplacement().getY();

        double newx = mouse_x * (p.getScale()/oldZ);
        double newy = mouse_y * (p.getScale()/oldZ);


//        System.out.println(toX);

        p.setDisplacement(new Point2D(toX-newx, toY-newy));
    }

    public void addParticles(int iteration) {
        //RANDOM PARTICLE TEST
        if(iteration >= 0 && iteration <= 10) {
            for (int i = 0; i < 100; i++) { // TODO: 13/02/2020 Scale this appropriately
                p.addParticle(new Particle(
                        (int) rand(2, 200), //MASS
                        Color.BLACK, //COLOR
/*                    new Point2D(rand(
                            (d.getScreenWidth()-d.getScreenHeight())/2,
                            (d.getScreenWidth()-(d.getScreenWidth()-d.getScreenHeight())/2)
                    ), rand(0, d.getScreenHeight())),
                    new Point2D(rand(-1, 1), rand(-1, 1))*/
                        new Point2D(rand( //Adding particles beyond the screen
                                Main.d.getScreenHeight() * -5,
                                Main.d.getScreenHeight() * 5
                        ), rand(Main.d.getScreenHeight() * -5, d.getScreenHeight() * 5))
                ));
            }
        }
        else if(iteration > 10 && iteration <= 20) {
            for (int i = 0; i < 100; i++) { // TODO: 13/02/2020 Scale this appropriately
                p.addParticle(new Particle(
                        (int) rand(2, 200), //MASS
                        Color.BLACK, //COLOR
                        new Point2D(rand( //Adding particles beyond the screen
                                Main.d.getScreenHeight() * -5,
                                Main.d.getScreenHeight() * 5
                        ), rand(Main.d.getScreenHeight() * -5, d.getScreenHeight() * 5)),
                        new Point2D(randSign() * 0.2 *iteration/p.getTimeScale() * Math.random(), randSign() * 0.2 * iteration/p.getTimeScale() * Math.random())
                ));
            }
        }
    }

    public void writeData() { //Time, Position, Velocity, Average Velocity
        try {



            /*FileWriter myWriter = new FileWriter("C:\\Users\\Alexey\\Desktop\\N-Body.csv");
            myWriter.write(String.valueOf(d.getFrames()) + p.getParticles().size());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");*/

            /** Recording the Data in Excel using a CSV file*/
            BufferedWriter br = new BufferedWriter(new FileWriter("C:\\Users\\Alexey\\Desktop\\N-Body.csv"));
            StringBuilder sb = new StringBuilder();

            sb.append("Frames");
            sb.append(",");
            for (int i = 0; i < dataArray.length-1; i++) {
                sb.append("Particles_" + (i+1));
                sb.append(",");
            }

            for (int i = 0; i < dataArray[0].length; i++) {
                sb.append(i);
                sb.append(",");

                for (int j = 0; j < dataArray.length; j++) {
                    if(dataArray[j][i] != null) {
                        sb.append(dataArray[j][i]);
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }

            br.write(sb.toString());
            br.close();


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void excelDisplace(StringBuilder sb) {
        for (int i = 0; i < timesRecorded*2; i++) {
            sb.append(",");
        }
    }



    //Launches the Main function
    public static void main(String[] args) {
        launch(args);
    }

}