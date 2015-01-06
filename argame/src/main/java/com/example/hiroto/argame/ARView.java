package com.example.hiroto.argame;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

//public class ARView extends SurfaceView implements SurfaceHolder.Callback {
public class ARView extends View {

	private int displayX;
	private int displayY;

	private int mScore;
	private int mState;

	public ARView(Context context) {
		super(context);
		// ��ʃT�C�Y�̎擾
		Display disp = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		displayX = disp.getWidth();
		displayY = disp.getHeight();
		mState = GLRenderer.STATE_START;
	}

	// �`�揈��
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		// �X�R�A�̕`��
		paint.setTextSize(16);
		paint.setColor(Color.WHITE);
		canvas.drawText("SCORE:" + Integer.toString(mScore), 10, 20, paint);
		
		if(mState == GLRenderer.STATE_START){
			String str = new String("TAP TO START!");
			paint.setTextSize(32);
			paint.setColor(Color.WHITE);
			float textWidth = paint.measureText(str) / 2;
			canvas.drawText(str, displayX/2-textWidth, displayY/2, paint);
		}else if (mState == GLRenderer.STATE_GAMEOVER){
			String str = new String("GAME OVER");
			paint.setTextSize(32);
			paint.setColor(Color.WHITE);
			float textWidth = paint.measureText(str) / 2;
			canvas.drawText(str, displayX/2-textWidth, displayY/2, paint);
		}

	}

	public void drawScreen(int state, int score) {
		// ��Ԃ̍X�V
		mState = state;
		mScore = score;
		// onDraw���Ăяo���čĕ`��
		invalidate();
	}

}
