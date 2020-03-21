package main;

import javafx.geometry.Point2D;

public class Data {
    private Point2D temperature; //First cell is initial, second cell is final
    private Point2D kineticEnergy;
    private Point2D GPE;

    public Data() {
        this.temperature = new Point2D(0, 0);
        this.kineticEnergy = new Point2D(0, 0);
        this.GPE = new Point2D(0, 0);
    }

    //Temperature getters and setters
    public double getInitialTemperature() {
        return temperature.getX();
    }

    public void setInitialTemperature(double temperature) {
        this.temperature = new Point2D(temperature, this.temperature.getY());
    }

    public double getFinalTemperature() {
        return temperature.getY();
    }

    public void setFinalTemperature(double temperature) {
        this.temperature = new Point2D(this.temperature.getX(), temperature);
    }

    //GPE getters and setters
    public double getInitialGPE() {
        return GPE.getX();
    }

    public double getFinalGPE() {
        return GPE.getY();
    }

    public void setInitialGPE(double GPE) {
        this.GPE = new Point2D(GPE, this.GPE.getY());
    }

    public void setFinalGPE(double GPE) {
        this.GPE = new Point2D(this.GPE.getX(), GPE);
    }

    //Kinetic energy getters and setters
    public double getInitialKE() {
        return kineticEnergy.getX();
    }

    public double getFinalKE() {
        return kineticEnergy.getY();
    }

    public void setInitialKE(double kineticEnergy) {
        this.kineticEnergy = new Point2D(kineticEnergy, this.kineticEnergy.getY());
    }

    public void setFinalKE(double kineticEnergy) {
        this.kineticEnergy = new Point2D(this.kineticEnergy.getX(), kineticEnergy);
    }

    @Override
    public String toString() {
        return "Data{" +
                "temperature=" + temperature +
                ", kineticEnergy=" + kineticEnergy +
                '}';
    }
}
