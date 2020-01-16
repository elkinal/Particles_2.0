
# Particles
This is a visual representation of an N-body simulation. This project will demonstrate how celestial bodies interact with each other when under the influence of gravity. This project incorporates realistic physics in order to make the simulation as accurate as possible.

## How To Use & Installation
The project can be installed via the link on my website:

http://www.alexeyelkin.com

In order to run the project, it must be extracted from its **rar** format. This can be done with **WinRar** Software. 

## Controls

**M** - Show Mesh

**SPACE** - Pause

**Left Mouse Click** - Generate Particle

**Right Mouse Click + drag** - Generate Particle with an initial velocity

**SCROLL** - Change the initial size of the generated particle.

## Physics Explained
### The Following Formulae are used in the simulation:
***
![Gravity Equation](http://www.alexeyelkin.com/images/gravity.gif)
>**Newton's Law of Universal Gravitation** states that every [particle](https://en.wikipedia.org/wiki/Particle "Particle") attracts every other particle in the universe with a [force](https://en.wikipedia.org/wiki/Force "Force") which is [directly proportional](https://en.wikipedia.org/wiki/Proportionality_(mathematics)#Direct_proportionality "Proportionality (mathematics)") to the product of their masses and [inversely proportional](https://en.wikipedia.org/wiki/Proportionality_(mathematics)#Inverse_proportionality "Proportionality (mathematics)") to the square of the distance between their centers" - _**Wikipedia**_
```java
//The First Line makes sure that the force is not calculated between a particle and itself
double force = (particles.get(i) != particles.get(j)) ?  

//The Second Line represents the top half of the equation (m1 * m2)

        GRAV_CONSTANT * (particles.get(i).getMass() * particles.get(j).getMass() /
        
//The Third Line represents the bottom half of the equation
         Math.pow(particles.get(j).getLocation().distance(particles.get(i).getLocation()), 2)
          
//The Fourth Line adds the dampening factor in. This ensures that a division by zero is not performed
+ DAMPENING) : -1;
```
```java
//Overall, the compact source code itself looks like this:
double force = (particles.get(i) != particles.get(j)) ?  
        GRAV_CONSTANT * (particles.get(i).getMass() * particles.get(j).getMass() /
         Math.pow(particles.get(j).getLocation().distance(particles.get(i).getLocation()), 2) + DAMPENING) : -1;
```
***
![Gravity Equation](http://www.alexeyelkin.com/images/force.gif)
>**Newton's Second Law**: In an inertial frame of reference, the vector  [sum](https://en.wikipedia.org/wiki/Vector_sum "Vector sum")  of the  [forces](https://en.wikipedia.org/wiki/Forces "Forces")  **F**  on an object is equal to the  [mass](https://en.wikipedia.org/wiki/Mass "Mass")  _m_  of that object multiplied by the  [acceleration](https://en.wikipedia.org/wiki/Acceleration "Acceleration")  **a**  of the object:  **F**  =  _m_**a**. (It is assumed here that the mass  _m_  is constant). - _**Wikipedia**_
```java
//The First 2 Lines are used to determine the difference between the locations fo the two particles
double xDifference = particles.get(i).getLocation().getX() - particles.get(j).getLocation().getX();  
double yDifference = particles.get(i).getLocation().getY() - particles.get(j).getLocation().getY();  

/* The Third Line ensures that the particle is only accelerate if the force is larger than zero. 
Thus, impossible "negative forces" are ruled out */
if (force > 0) {  

    particles.get(j).accelerate( //Acceleration Function
	    new Point2D(Math.signum(xDifference) * force / particles.get(j).getMass(),  //X axis acceleration
			  Math.signum(yDifference) * force / particles.get(j).getMass()) //Y axis acceleration
	);  
	
/* Math.signum() is a function that returns the absolute value of a number while factoring in the coefficient. 
eg: signum(35) = 1, signum(-78) = -1, signum(-174) = -1 
	
The signum() value is used in the program to set the direction for the particle to travel in
	
Since F = ma, a = F/m, so the acceleration of the particle is it's (force / mass) */
}
```
```java
//Overall, the compact source code looks like this:
double xDifference = particles.get(i).getLocation().getX() - particles.get(j).getLocation().getX();  
double yDifference = particles.get(i).getLocation().getY() - particles.get(j).getLocation().getY();  
if (force > 0) {  
    particles.get(j).accelerate(new Point2D(Math.signum(xDifference) * force / particles.get(j).getMass(),  
  Math.signum(yDifference) * force / particles.get(j).getMass()));  
}
```
***
![Gravity Equation](http://www.alexeyelkin.com/images/moment2.gif)
>A **Perfectly Inelastic Collision** occurs when the maximum amount of kinetic energy of a system is lost. In a perfectly inelastic collision, i.e., a zero [coefficient of restitution](https://en.wikipedia.org/wiki/Coefficient_of_restitution "Coefficient of restitution"), the colliding particles stick together. In such a collision, kinetic energy is lost by bonding the two bodies together. - _**Wikipedia**_
>
![Gravity Equation](http://www.alexeyelkin.com/images/moment.gif)
>Where  **_V_**  is the final velocity, which is given by the formula above.
```java
particles.get(j).setVelocity(  
        new Point2D(  
				//This represents the X velocity of the particle      
                (particles.get(j).getMass() * particles.get(j).getVelocity().getX() +  
                        particles.get(i).getMass() * particles.get(i).getVelocity().getX()) /  
                        (particles.get(j).getMass() + particles.get(i).getMass()),  
  
				//This represents the Y velocity of the particle
			(particles.get(j).getMass() * particles.get(j).getVelocity().getY() +  
                        particles.get(i).getMass() * particles.get(i).getVelocity().getY()) /  
                        (particles.get(j).getMass() + particles.get(i).getMass())  
        )  
);
/* particles.get(j) represents the larger particle, and particles.get(i) represents the smaller particle
When the two particles collide, the larger particle will absorb the smaller particle.
Therefore, the velocity of the particle produced will change along with its mass
The code above represents the new velocity of this particle. */

```
```java
//Overall, the compact source code looks like this
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
particles.get(i).addMass(particles.get(j).getMass());  
particles.remove(particles.get(j));
```
***
## Images
![Gravity Equation](http://www.alexeyelkin.com/images/many.png)
 *The Image above shows particles converging to the center of the screen. In This simulation, the particles started in a random arrangement from 0px - 1080px in a square of dimensions 1080 x 1080px in which they were evenly spread out. This meant that the overall center of gravity was approximately in the center of the screen. During the course of this simulation, the particles converged to the center of the screen and merged, until only one very large stationary particle was remaining.*

![Gravity Equation](http://www.alexeyelkin.com/images/orbital.png)

 *The Image above shows a small particle in a stable orbit around a larger particle*
 ![Gravity Equation](http://www.alexeyelkin.com/images/arrangement.png)
 *The image above shows the forces acting on the particles through the "mesh" function. The user can enable the mesh by pressing 'M' on their keyboard while running the simulation. The mesh demonstrates that every particle exerts a gravitational force on every other particle.*
