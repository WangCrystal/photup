package uk.co.senab.photup;

import uk.co.senab.photup.base.PhotupActivity;
import uk.co.senab.photup.model.PhotoUpload;
import uk.co.senab.photup.platform.Platform;
import uk.co.senab.photup.views.CropImageView;
import uk.co.senab.photup.views.HighlightView;
import uk.co.senab.photup.views.PhotupImageView.OnPhotoLoadListener;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class CropImageActivity extends PhotupActivity implements OnPhotoLoadListener {

	static PhotoUpload CROP_SELECTION;

	private CropImageView mCropImageView;
	private HighlightView mHighlightView;

	private PhotoUpload mPhotoUpload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		mCropImageView = new CropImageView(this, null);
		Platform.disableHardwareAcceleration(mCropImageView);

		setContentView(mCropImageView);

		// FIXME Hack
		mPhotoUpload = CROP_SELECTION;
		CROP_SELECTION = null;

		setProgressBarIndeterminateVisibility(true);
		mCropImageView.requestFullSize(mPhotoUpload, false, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_photo_crop, menu);

		// Remove OK Menu Item if we're not loaded yet
		if (null == mHighlightView) {
			menu.removeItem(R.id.menu_crop_ok);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_crop_cancel:
				setResult(RESULT_CANCELED);
				finish();
				return true;

			case R.id.menu_crop_ok:
				if (null != mHighlightView) {
					mPhotoUpload.setCropValues(mHighlightView.getCropRect());
					setResult(RESULT_OK);
					finish();
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void makeHighlight(final Bitmap bitmap) {
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final Rect imageRect = new Rect(0, 0, width, height);

		RectF cropRect;
		if (mPhotoUpload.beenCropped()) {
			cropRect = mPhotoUpload.getCropValues(width, height);
		} else {
			cropRect = getDefaultCropRect(width, height);
		}

		mHighlightView = new HighlightView(mCropImageView);
		mHighlightView.setup(mCropImageView.getImageMatrix(), imageRect, cropRect, false);

		mCropImageView.setHighlight(mHighlightView);

		// Refresh Menu so we have the OK item
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void onPhotoLoadFinished(Bitmap bitmap) {
		setProgressBarIndeterminateVisibility(false);

		if (null != bitmap) {
			makeHighlight(bitmap);
		}
	}

	static RectF getDefaultCropRect(final int width, final int height) {
		return new RectF(width * 0.1f, height * 0.1f, width * 0.9f, height * 0.9f);
	}
}
