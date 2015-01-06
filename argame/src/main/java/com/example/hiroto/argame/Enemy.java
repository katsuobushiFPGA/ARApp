package com.example.hiroto.argame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


class Enemy {
	final float ENEMY_DISTANCE = 10f;
	private float posX;
	private float posY;
	private float posZ;
	private float rotateZ;
	private float rotateY;
	private float velocity;
	private double theta;
	private double phi;

	private IntBuffer mVertexBuffer;
	private IntBuffer mColorBuffer;
	private ByteBuffer mIndexBuffer;

	public Enemy() {
		velocity = 0.01f;

		initPosition();

		int one = 0x10000;
		int vertices[] = { -one, -one, -one, one, -one, -one, one, one, -one,
				-one, one, -one, -one, -one, one, one, -one, one, one, one,
				one, -one, one, one, };

		int colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one, 0,
				one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one,
				one, 0, one, one, one, };

		byte indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7,
				3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

		// Buffers to be passed to gl*Pointer() functions
		// must be direct, i.e., they must be placed on the
		// native heap where the garbage collector cannot
		// move them.
		//
		// Buffers with multi-byte datatypes (e.g., short, int, float)
		// must have their byte order set to native order

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asIntBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix();
		// �ړ�
		gl.glTranslatef(posX, posY, posZ);
		// ��]
		gl.glRotatef(rotateY, 0, 1, 0);
		gl.glRotatef(rotateZ, 0, 0, 1);
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
				mIndexBuffer);
		gl.glPopMatrix();
	}

	public void initPosition() {
		// ���W������������
		theta = Math.PI * Math.random() - Math.PI / 2;
		phi = 2 * Math.PI * Math.random();
		posY = (float) (ENEMY_DISTANCE * Math.sin(theta));
		posX = (float) (ENEMY_DISTANCE * Math.cos(theta) * Math.cos(phi));
		posZ = (float) (ENEMY_DISTANCE * Math.cos(theta) * Math.sin(phi));
		rotateY = (float) Math.toDegrees(-phi);
		rotateZ = (float) Math.toDegrees(theta);
		// �ړ����x�𑝂₷
		velocity *= 1.1;

	}

	public void move(GL10 gl) {
		// �v���[���[�̕����Ɉړ�����
		posY += velocity * Math.sin(-theta);
		posX += velocity * Math.cos(-theta) * Math.cos(phi + Math.PI);
		posZ += velocity * Math.cos(-theta) * Math.sin(phi + Math.PI);
	}

	public boolean isHit() {
		// �v���[���[�ɐG�ꂽ�����肷��
		double distance = Math.sqrt(posY * posY + posX * posX + posZ * posZ);
		if (distance < 2.1)
			return true;
		return false;
	}
}
