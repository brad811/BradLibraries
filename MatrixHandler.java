package com.bradsproject.spacetest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;

public class MatrixHandler
{
  public static float[] mMVPMatrix = new float[16];
	public static float[] mMVMatrix = new float[16];
	
	public static float[] mProjMatrix = new float[16];
	public static float[] mModelMatrix = new float[16];
	public static float[] mViewMatrix = new float[16];
	
	public static Stack<float[]> mProjStack = new Stack<float[]>();
	public static Stack<float[]> mModelStack = new Stack<float[]>();
	public static Stack<float[]> mViewStack = new Stack<float[]>();
	
	public static float[] mTempMatrix = new float[16];
	
	// Set the model, view, and projection matrices to the identity matrix
	public static void setIdentity()
	{
		setIdentityModel();
		setIdentityView();
		setIdentityProj();
	}
	
	// Set the model matrix to the identity matrix
	public static void setIdentityModel()
	{
		Matrix.setIdentityM(MatrixHandler.mModelMatrix, 0);
	}
	
	// Set the view matrix to the identity matrix
	public static void setIdentityView()
	{
		Matrix.setIdentityM(MatrixHandler.mViewMatrix, 0);
	}
	
	// Set the projection matrix to the identity matrix
	public static void setIdentityProj()
	{
		Matrix.setIdentityM(MatrixHandler.mProjMatrix, 0);
	}
	
	// Set the view matrix to look at the given coordinates from the given position and up vector
	public static void setLookAt(
			float eyeX, float eyeY, float eyeZ,
			float posX, float posY, float posZ,
			float upX, float upY, float upZ
		)
	{
		Matrix.setLookAtM(mViewMatrix, 0,
				eyeX, eyeY, eyeZ,
				posX, posY, posZ,
				upX, upY, upZ
			);
	}
	
	// Set the viewport, and projection matrix to match
	public static void setViewport(int width, int height, float bottom, float top, float near, float far)
	{
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		
		Matrix.frustumM(MatrixHandler.mProjMatrix, 0,
				-ratio, ratio,
				bottom, top,
				near, far
			);
	}
	
	// Build a float buffer using the given array of floats
	public static FloatBuffer buildBuffer(float[] data)
	{
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		buffer.put(data);
		buffer.position(0);
		
		return buffer;
	}
	
	// Rotate the model matrix
	public static void rotate(float angle, float x, float y, float z)
	{
		Matrix.rotateM(MatrixHandler.mModelMatrix, 0, angle, x, y, z);
	}
	
	// Translate the model matrix
	public static void translate(float x, float y, float z)
	{
		Matrix.translateM(MatrixHandler.mModelMatrix, 0, x, y, z);
	}
	
	// Recalculate the MVP matrix given the current model, view, and projection matrices
	public static void prepareMVP()
	{
		Matrix.multiplyMM(MatrixHandler.mMVMatrix, 0, MatrixHandler.mViewMatrix, 0, MatrixHandler.mModelMatrix, 0);
		
		Matrix.multiplyMM(MatrixHandler.mMVPMatrix, 0, MatrixHandler.mViewMatrix, 0, MatrixHandler.mModelMatrix, 0);
		Matrix.multiplyMM(MatrixHandler.mMVPMatrix, 0, MatrixHandler.mProjMatrix, 0, MatrixHandler.mMVPMatrix, 0);
	}
	
	// Push the current model matrix onto the stack
	public static void pushModelMatrix()
	{
		mTempMatrix = new float[16];
		System.arraycopy(mModelMatrix, 0, mTempMatrix, 0, mModelMatrix.length);
		mModelStack.push(mTempMatrix);
		
		// This proved to be slower in practice
		// mModelStack.push(mModelMatrix.clone());
	}
	
	// Pop the top stored model matrix
	public static void popModelMatrix()
	{
		System.arraycopy(mModelStack.pop(), 0, mModelMatrix, 0, mModelMatrix.length);
	}
	
	// Converts 2D screen coordinates to 3D world coordinates
	public static float[] unProject(int winX, int winY, float winZ, float[] model, float[] view, float[] proj, int[] viewport)
	{
		float[] obj = new float[4];
		
		float[] mMVMatrix = new float[16];
		Matrix.multiplyMM(mMVMatrix, 0, view, 0, model, 0);
		
		GLU.gluUnProject(
				winX, winY, winZ,
				mMVMatrix, 0,
				proj, 0,
				viewport, 0,
				obj, 0
			);
		
		return obj;
	}
	
	// Converts 3D world coordinates to 2D screen coordinates
	public static float[] project(float objX, float objY, float objZ, float[] model, float[] view, float[] proj, int[] viewport)
	{
		float[] win = new float[4];
		
		float[] mMVMatrix = new float[16];
		Matrix.multiplyMM(mMVMatrix, 0, view, 0, model, 0);
		
		GLU.gluProject(
				objX, objY, objZ,
				mMVMatrix, 0,
				proj, 0,
				viewport, 0,
				win, 0
			);
		
		return win;
	}
}
