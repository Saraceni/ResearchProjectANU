package br.saraceni.research.opengl.objects;

import br.saraceni.research.opengl.data.Constants;
import br.saraceni.research.opengl.data.VertexArray;
import br.saraceni.research.opengl.programs.ParticleShaderProgram;
import br.saraceni.research.opengl.util.Geometry.Point;
import br.saraceni.research.opengl.util.Geometry.Vector;
import android.graphics.Color;
import android.opengl.GLES20;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class ParticleSystem {
	
	// Constants definitions
	private static final int POSITION_COMPONENT_COUNT = 3;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int VECTOR_COMPONENT_COUNT = 3;
	private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
	
	private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT +
			COLOR_COMPONENT_COUNT +
			VECTOR_COMPONENT_COUNT +
			PARTICLE_START_TIME_COMPONENT_COUNT;
	
	private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;
	
	// Variables
	private final float[] particles;
	private final VertexArray vertexArray;
	private final int maxParticleCount;
	
	private int currentParticleCount;
	private int nextParticle;
	
	// Constructor
	public ParticleSystem(int maxParticleCount)
	{
		particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
		vertexArray = new VertexArray(particles);
		this.maxParticleCount = maxParticleCount;
	}
	
	public void addParticle(Point position, int color, Vector direction, float particleStartTime)
	{
		final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
		
		int currentOffset = particleOffset;
		nextParticle++;
		
		if(currentParticleCount < maxParticleCount)
		{
			currentParticleCount++;
		}
		
		if(nextParticle == maxParticleCount)
		{
			// Start over at the beginning, but keep currentParticleCount so 
			// that all the other particles still get drawn.
			nextParticle = 0;
		}
		
		// Update the particles data in the determined offset
		particles[currentOffset++] = position.x;
		particles[currentOffset++] = position.y;
		particles[currentOffset++] = position.z;
		
		particles[currentOffset++] = Color.red(color) / 255;
		particles[currentOffset++] = Color.green(color) / 255;
		particles[currentOffset++] = Color.blue(color) / 255;
		
		particles[currentOffset++] = direction.x;
		particles[currentOffset++] = direction.y;
		particles[currentOffset++] = direction.z;
		
		particles[currentOffset++] = particleStartTime;
		
		vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
	}
	
	// Bind this particle data to the shader programs 
	public void bind(ParticleShaderProgram particleProgram)
	{
		int dataOffset = 0;
		// Bind position data
		vertexArray.setVertexAttribPointer(dataOffset, 
				particleProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, STRIDE);
		dataOffset += POSITION_COMPONENT_COUNT;
		
		// bind color data
		vertexArray.setVertexAttribPointer(dataOffset, 
				particleProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, STRIDE);
		dataOffset += COLOR_COMPONENT_COUNT;
		
		// bind vector data
		vertexArray.setVertexAttribPointer(dataOffset, 
				particleProgram.getDirectionVectorAttributeLocation(), 
				VECTOR_COMPONENT_COUNT, STRIDE);
		dataOffset += VECTOR_COMPONENT_COUNT;
		
		// bind start time data
		vertexArray.setVertexAttribPointer(dataOffset, 
				particleProgram.getParticleStartTimeAttributeLocation(), 
				PARTICLE_START_TIME_COMPONENT_COUNT, 
				STRIDE);
	}
	
	// Method for drawing in the screen
	public void draw()
	{
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);
	}
}









