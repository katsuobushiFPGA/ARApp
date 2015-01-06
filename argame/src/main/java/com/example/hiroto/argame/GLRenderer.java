package com.example.hiroto.argame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Vibrator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Render a pair of tumbling cubes.
 */

class GLRenderer implements GLSurfaceView.Renderer {

	private Context mContext;
	private Vibrator vibrator;

	float camThetaXZ;
	float camThetaY;
	final float CAMERA_R = 10;

	private Enemy enemy;

	private int touchX;
	private int touchY;

	private boolean isTouched = false;

	public static final int STATE_START = 0;
	public static final int STATE_PLAY = 1;
	public static final int STATE_GAMEOVER = 2;

	private int state;
	private int score;

	public GLRenderer(Context context) {
		mContext = context;
		// �U����̗p��
		vibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
		initialize();
	}

	private void initialize() {
		enemy = new Enemy();
		state = STATE_START;
		score = 0;
	}

	public void onDrawFrame(GL10 gl) {
		switch (state) {
		case STATE_START:
			if (isTouched) {
				isTouched = false;
				state = STATE_PLAY;
			}
			break;
		case STATE_PLAY:
			// �`�揈�����s���O�Ƀo�b�t�@�̏������s��
			// GL_COLOR_BUFFER_BIT�ł̓J���[�o�b�t�@��
			// GL10.GL_DEPTH_BUFFER_BIT�ł͉A�ʏ����Ɏg����f�v�X�o�b�t�@��
			// �w�肵�Ă��܂�
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			// ���Z�Ώۂ����f���r���[�ɂ���
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// ���Z�s���P�ʍs��ɂ���
			gl.glLoadIdentity();

			// �J�����̈ʒu�ƁA�[���̌������王�_�̒��S�����߂�
			float centerY = CAMERA_R * (float) Math.sin(camThetaY);
			float centerX = CAMERA_R * (float) Math.cos(camThetaY)
					* (float) Math.cos(-camThetaXZ);
			float centerZ = CAMERA_R * (float) Math.cos(camThetaY)
					* (float) Math.sin(-camThetaXZ);
			// �J�����̈ʒu�ƁA���_�̒��S���w�肷��
			GLU.gluLookAt(gl, 0, 0, 0, centerX, centerY, centerZ, 0f, 1f, 0f);

			// ���_�z���L�������܂�
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// �J���[�z���L�������܂�
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

			// �I�u�W�F�N�g���ړ����܂�
			enemy.move(gl);
			// �I�u�W�F�N�g��`�悵�܂�
			enemy.draw(gl);

			if (enemy.isHit()) {
				state = STATE_GAMEOVER;
				vibrator.vibrate(3000);
			}

			
			if (isTouched) {
				isTouched = false;
				slap(gl);
			}


			break;
		case STATE_GAMEOVER:
			if (isTouched) {
				isTouched = false;
				initialize();
			}
			break;
		}
	}

	public void onTouch(int x, int y) {
		isTouched = true;
		touchX = x;
		touchY = y;
	}

	private void slap(GL10 gl) {
		int color[] = new int[1];
		ByteBuffer buf = ByteBuffer.allocateDirect(4);
		buf.order(ByteOrder.nativeOrder());
		gl.glReadPixels(touchX, touchY, 1, 1, GL10.GL_RGBA,
				GL10.GL_UNSIGNED_BYTE, buf);
		buf.asIntBuffer().get(color);
		if (color[0] != 0) {
			vibrator.vibrate(100);
			enemy.initPosition();
			score += 1;
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// �`��̈悪�ύX���ꂽ�Ƃ��ɌĂяo����܂�

		// �`���̈�s���̈���w�肵�܂�
		// �����ł͉�ʑS�̂��w�肵�Ă��܂�
		gl.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		// �ˉe�s���I�������Ԃɂ��܂�
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		// �ˉe���@�����ߖ@���g�p���铧���ˉe�Ƃ��ĕ`��̈���w�肵�܂�
		gl.glFrustumf(-ratio, ratio, -1.0f, 1.0f, 1f, 10000f);
		// �f�B�U�����𖳌������A�Ȃ߂炩�ȕ\�����s���܂�
		gl.glDisable(GL10.GL_DITHER);
		// �����ˉe�̒��x���������x���d���������̂��w�肵�܂�
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		// �w�i�𓧖��ɐݒ肵�܂�
		gl.glClearColor(0, 0, 0, 0);
		// �|���S���̔w�ʂ�`�悵�Ȃ悤�ɂ��܂�
		gl.glEnable(GL10.GL_CULL_FACE);
		// �ʂ̕`����Ȃ߂炩�ɂ���悤�ɂ��܂�
		gl.glShadeModel(GL10.GL_SMOOTH);
		// �f�v�X�o�b�t�@��L�������܂�
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// �e�N�X�`����L�������܂�
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// �`��̈悪�쐬���ꂽ�Ƃ��ɌĂяo����܂�
	}

	public void setState(float thetaXZ, float thetaY) {
		// �J�����̊p�x���擾���܂�
		camThetaXZ = -thetaXZ;
		camThetaY = (float) (-Math.PI / 2 - thetaY);
	}

	public int getState() {
		return state;
	}

	public int getScore() {
		return score;
	}

}
