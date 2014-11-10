package br.saraceni.research.opengl.util;

/*
 * This class was taken from the book
 * Open GL ES 2 for Android A Quick-Start Guide
 * by Kevin Brothaler
 */

public class Geometry {
	
	// Class representing a 3D point in space
	public static class Point
	{
		public final float x, y, z;
		public Point(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
	}
	
	// Class representing a 3D vector in space
	public static class Vector
	{
		public final float x, y, z;
		
		public Vector(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
	}

}






