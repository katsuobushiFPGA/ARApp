package com.example.hiroto.argame;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	private Camera camera;

	// �R���X�g���N�^
	public CameraView(Context context) {
		super(context);

		// �T�[�t�F�C�X�z���_�[�̎擾�ƃR�[���o�b�N�ʒm��̎w��
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		// SurfaceView�̎�ʂ��v�b�V���o�b�t�@�[�ɕύX���܂�
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(surfaceholder);
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// �v���r���[�̊J�n
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}
}
