package br.saraceni.research.opengl.objects;

import java.util.Random;

import br.saraceni.research.opengl.util.Geometry.Point;
import br.saraceni.research.opengl.util.Geometry.Vector;
import android.opengl.Matrix;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class ParticleShooter {
	
	// Position of this object in the scenario
	private final Point position;
	// Color of this object
	private final int color;
	
	// Angle variance of the particles
	private final float angleVariance;
	// Speed variance of the particles
	private final float speedVariance;
	
	// Random number generator class
	private final Random random = new Random();
	
	// Matrix for applying rotation
	private float[] rotationMatrix = new float[16];
	
	// Direction of the particle matrix
	private float[] directionVector = new float[4];
	// Vector containing the result of rotating and accelerating particle
	private float[] resultVector = new float[4];
	
	public ParticleShooter(Point position, Vector direction, int color, 
			float angleVarianceInDegrees, float speedVariance)
	{
		this.position = position;
		this.color = color;
		this.angleVariance = angleVarianceInDegrees;
		this.speedVariance = speedVariance;
		
		directionVector[0] = direction.x;
		directionVector[1] = direction.y;
		directionVector[2] = direction.z;
	}
	
	public void addParticles(ParticleSystem particleSystem, float currentTime, int count)
	{
		// Create a determined number of particles
		for(int i = 0; i < count; i++)
		{
			// Rotates in an random proportion of the angle variance
			Matrix.setRotateEulerM(rotationMatrix, 0, 
					(random.nextFloat() - 0.5f) * angleVariance, 
					(random.nextFloat() - 0.5f) * angleVariance, 
					(random.nextFloat() - 0.5f) * angleVariance);
			
			// Add rotation and orientation to the particle
			Matrix.multiplyMV(resultVector, 0, rotationMatrix, 0, directionVector, 0);
			
			float speedAdjustment = 1f + random.nextFloat() * speedVariance;
			
			Vector thisDirection = new Vector(
					resultVector[0] * speedAdjustment,
					resultVector[1] * speedAdjustment,
					resultVector[2] * speedAdjustment);
			// Add particle to the system
			particleSystem.addParticle(position, color, thisDirection, currentTime);
		}
	}

}
