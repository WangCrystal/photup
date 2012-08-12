package uk.co.senab.photup.adapters;

import java.util.List;

import uk.co.senab.photup.PhotoUploadController;
import uk.co.senab.photup.PhotupApplication;
import uk.co.senab.photup.listeners.OnPickFriendRequestListener;
import uk.co.senab.photup.listeners.OnSingleTapListener;
import uk.co.senab.photup.model.PhotoSelection;
import uk.co.senab.photup.views.MultiTouchImageView;
import uk.co.senab.photup.views.PhotoTagItemLayout;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class SelectedPhotosViewPagerAdapter extends PagerAdapter {

	private final Context mContext;
	private final PhotoUploadController mController;
	private final OnSingleTapListener mTapListener;
	private final OnPickFriendRequestListener mFriendPickRequestListener;

	private List<PhotoSelection> mItems;

	public SelectedPhotosViewPagerAdapter(Context context, OnSingleTapListener tapListener,
			OnPickFriendRequestListener friendRequestListener) {
		mContext = context;
		mTapListener = tapListener;
		mFriendPickRequestListener = friendRequestListener;

		PhotupApplication app = PhotupApplication.getApplication(context);
		mController = app.getPhotoUploadController();

		refreshData();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public int getCount() {
		return null != mItems ? mItems.size() : 0;
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public PhotoSelection getItem(int position) {
		if (position >= 0 && position < getCount()) {
			return mItems.get(position);
		}
		return null;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		PhotoSelection upload = mItems.get(position);

		PhotoTagItemLayout view = new PhotoTagItemLayout(mContext, mController, upload, mFriendPickRequestListener);
		view.setPosition(position);

		upload.setFaceDetectionListener(view);

		MultiTouchImageView imageView = view.getImageView();
		imageView.requestFullSize(upload, true, null);
		imageView.setSingleTapListener(mTapListener);
		imageView.setRotation(upload.getUserRotation());

		((ViewPager) container).addView(view);

		return view;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public void notifyDataSetChanged() {
		refreshData();
		super.notifyDataSetChanged();
	}

	protected Context getContext() {
		return mContext;
	}

	private void refreshData() {
		mItems = mController.getSelectedPhotoUploads();
	}

}
