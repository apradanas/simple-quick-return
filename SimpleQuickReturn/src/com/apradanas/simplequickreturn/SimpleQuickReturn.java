package com.apradanas.simplequickreturn;

import com.apradanas.simplequickreturn.ListViewScrollObserver.OnListViewScrollListener;

import android.content.Context;
import android.os.Build;
//import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;

/**
 * @author apradanas
 */
public class SimpleQuickReturn {
	protected static final String TAG = "SimpleQuickReturn";
	
	private Animation mHeaderAnimation;
	private Animation mFooterAnimation;
	private boolean mIsSnapped = true;
	private boolean mIsWaitingForExactHeaderHeight = true;
    private boolean mIsScrollingUp; // True if the last scroll movement was in the "up" direction.
    private boolean mIsHeaderAnimated = false;
    private boolean mIsUsingHeader = false;
    private boolean mIsUsingFooter = false;
	private Context mContext;
	private int mContentResId;
	private int mHeaderResId;
	private int mFooterResId;
	private int mHeaderHeight;
	private int mFooterHeight;
	private int mHeaderTop;
	private int mFooterBottom;
	private LayoutInflater mInflater;
	private LayoutParams mRealHeaderLayoutParams;
	private LayoutParams mRealFooterLayoutParams;
	private ListView mListView;
	private OnSnappedChangeListener mOnSnappedChangeListener;
	private View mContent;
	private View mDummyHeader;
	private View mDummyFooter;
	private View mRealHeader;
	private View mRealFooter;
    private ViewGroup mRoot;
    
    /**
     * Maximum time it takes the show/hide animation to complete. Maximum because it will take much less time if the
     * header is already partially hidden or shown.
     * <p>
     * In milliseconds.
     */
    private static final long ANIMATION_DURATION = 400;
    
    public interface OnSnappedChangeListener {
        void onSnappedChange(boolean snapped);
    }
    
    public void setOnSnappedChangeListener(OnSnappedChangeListener onSnapListener) {
    	mOnSnappedChangeListener = onSnapListener;
    }
    
    public SimpleQuickReturn(Context context) {
    	mContext = context;
    }
	
	public SimpleQuickReturn setContent(int contentResId) {
		mContentResId = contentResId;
		return this;
	}
	
	public SimpleQuickReturn setHeader(int headerResId) {
		mHeaderResId = headerResId;
		mIsUsingHeader = true;
		return this;
	}
	
	public SimpleQuickReturn setFooter(int footerResId) {
		mFooterResId = footerResId;
		mIsUsingFooter = true;
		return this;
	}

	public View createView() {
        mInflater = LayoutInflater.from(mContext);
        mContent = mInflater.inflate(mContentResId, null);

        if(mIsUsingHeader) {
	        mRealHeader = mInflater.inflate(mHeaderResId, null);
	        mRealHeaderLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        mRealHeaderLayoutParams.gravity = Gravity.TOP;
        }
        if(mIsUsingFooter) {
	        mRealFooter = mInflater.inflate(mFooterResId, null);
	        mRealFooterLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        mRealFooterLayoutParams.gravity = Gravity.BOTTOM;
        }

        // Use measured height here as an estimate of the header height 
        // later on after the layout is complete we'll use the actual height
        int widthMeasureSpec = 0;
        int heightMeasureSpec = 0;
        // http://stackoverflow.com/a/20147939
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        	widthMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.AT_MOST);
	        heightMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST);
        } else {
	        widthMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY);
	        heightMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.EXACTLY);
        }
        
        if(mIsUsingHeader) {
	        mRealHeader.measure(widthMeasureSpec, heightMeasureSpec);
	        mHeaderHeight = mRealHeader.getMeasuredHeight();
        }
        if(mIsUsingFooter) {
        	mRealFooter.measure(widthMeasureSpec, heightMeasureSpec);
        	mFooterHeight = mRealFooter.getMeasuredHeight();
        }

        mListView = (ListView) mContent.findViewById(android.R.id.list);
        if (mListView != null) {
            createListView();
        }
        return mRoot;
    }
	
	private void createListView() {
        mRoot = (FrameLayout) mInflater.inflate(R.layout.sqr__listview_container, null);
        mRoot.addView(mContent);

        mListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        ListViewScrollObserver observer = new ListViewScrollObserver(mListView);
        observer.setOnScrollUpAndDownListener(new OnListViewScrollListener() {
            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                onNewScroll(delta);
                snap(mHeaderTop == scrollPosition);
            }

            @Override
            public void onScrollIdle() {
                SimpleQuickReturn.this.onScrollIdle();
            }
        });

        if(mIsUsingHeader) {
        	mRoot.addView(mRealHeader, mRealHeaderLayoutParams);
        	
        	mDummyHeader = new View(mContext);
	        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight);
	        mDummyHeader.setLayoutParams(headerParams);
	        mListView.addHeaderView(mDummyHeader);
        }
        if(mIsUsingFooter) {
	        mRoot.addView(mRealFooter, mRealFooterLayoutParams);
	        
	        mDummyFooter = new View(mContext);
	        AbsListView.LayoutParams footerParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mFooterHeight);
	        mDummyFooter.setLayoutParams(footerParams);
	        mListView.addFooterView(mDummyFooter);
        }
    }
    
    private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			if (mIsWaitingForExactHeaderHeight) {
				mIsWaitingForExactHeaderHeight = false;
				
				if(mIsUsingHeader) {
					if(mDummyHeader.getHeight() > 0) {
			            mHeaderHeight = mDummyHeader.getHeight();		            
			            AbsListView.LayoutParams params = (AbsListView.LayoutParams) mDummyHeader.getLayoutParams();
			            params.height = mHeaderHeight;
			            mDummyHeader.setLayoutParams(params);
					}
				}
				if(mIsUsingFooter) {
					if(mDummyFooter.getHeight() > 0) {
			            mFooterHeight = mDummyFooter.getHeight();		            
			            AbsListView.LayoutParams params = (AbsListView.LayoutParams) mDummyFooter.getLayoutParams();
			            params.height = mFooterHeight;
			            mDummyFooter.setLayoutParams(params);
					}
				}
	        }
		}
	};

    /**
     * Invoked when the user stops scrolling the content. In response we might start an animation to leave the header in
     * a fully open or fully closed state.
     */
    private void onScrollIdle() {
        if (mIsSnapped) {
            // Only animate when header is out of its natural position (truly over the content).
            return;
        }
        if (mHeaderTop > 0 || mHeaderTop <= -mHeaderHeight) {
            // Fully hidden, to need to animate.
            return;
        }
        if (mIsScrollingUp) {
        	if(mIsUsingHeader) {
        		hideHeader();
        	}
        	if(mIsUsingFooter) {
        		hideFooter();
        	}
        } else {
        	if(mIsUsingHeader) {
        		showHeader();
        	}
        	if(mIsUsingFooter) {
        		showFooter();
        	}
        }
    }

    /**
     * Shows the header using a simple downwards translation animation.
     */
    private void showHeader() {
        animateHeader(mHeaderTop, 0);
    }
    
    /**
     * Shows the footer using a simple downwards translation animation.
     */
    private void showFooter() {
    	animateFooter(-mFooterHeight, mFooterBottom);
    }

    /**
     * Hides the header using a simple upwards translation animation.
     */
    private void hideHeader() {
        animateHeader(mHeaderTop, -mHeaderHeight);
    }
    
    /**
     * Hides the footer using a simple upwards translation animation.
     */
    private void hideFooter() {
    	animateFooter(mFooterHeight, -mFooterBottom);
    }

    /**
     * Animates the marginTop property of the header between two specified values.
     * @param startTop Initial value for the marginTop property.
     * @param endTop End value for the marginTop property.
     */
    private void animateHeader(final float startTop, float endTop) {
        cancelAnimation();
        mIsHeaderAnimated = false;
        //Log.i(TAG, "animateHeader");
        //Log.i(TAG, "headerTop=" + mHeaderTop);
        final float deltaTop = endTop - startTop;
        //Log.i(TAG, "deltaTop=" + deltaTop);
        mHeaderAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	mIsHeaderAnimated = true;
                mHeaderTop = (int) (startTop + deltaTop * interpolatedTime);
                //Log.i(TAG, "applyTransformation:headerTop=" + mHeaderTop);
                mRealHeaderLayoutParams.topMargin = mHeaderTop;
                mRealHeader.setLayoutParams(mRealHeaderLayoutParams);
            }
        };
        long duration = (long) (deltaTop / (float) mHeaderHeight * ANIMATION_DURATION);
        //Log.i(TAG, "animateHeaderDuration=" + Math.abs(duration));
        mHeaderAnimation.setDuration(Math.abs(duration));
        mRealHeader.startAnimation(mHeaderAnimation);
    }
    
    /**
     * Animates the marginBottom property of the footer between two specified values.
     * @param startBottom Initial value for the marginBottom property.
     * @param endBottom End value for the marginBottom property.
     */
    private void animateFooter(final float startBottom, float endBottom) {
        cancelAnimation();
        //Log.i(TAG, "animateFooter");
        //Log.i(TAG, "footerBottom=" + mFooterBottom);
        final float deltaBottom = endBottom - startBottom;
        //Log.i(TAG, "deltaBottom=" + deltaBottom);
        mFooterAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
            	if(mIsHeaderAnimated) {
	                mFooterBottom = (int) (startBottom + deltaBottom * interpolatedTime);
	                //Log.i(TAG, "applyTransformation:footerBottom=" + mFooterBottom);
	                mRealFooterLayoutParams.bottomMargin = mFooterBottom;
	                mRealFooter.setLayoutParams(mRealFooterLayoutParams);
            	}
            }
        };
        long duration = (long) (deltaBottom / (float) mFooterHeight * ANIMATION_DURATION);
        //Log.i(TAG, "animateFooterDuration=" + Math.abs(duration));
        mFooterAnimation.setDuration(Math.abs(duration));
        mRealFooter.startAnimation(mFooterAnimation);
    }

    private void cancelAnimation() {
        if (mHeaderAnimation != null) {
            mRealHeader.clearAnimation();
            mHeaderAnimation = null;
        }
        if(mFooterAnimation != null) {
        	mRealFooter.clearAnimation();
        	mFooterAnimation = null;
        }
    }

    private void onNewScroll(int delta) {
        cancelAnimation();
        if (delta > 0) {
        	if(mIsUsingHeader) {
	            if (mHeaderTop + delta > 0) {
	                delta = -mHeaderTop;
	            }
        	} else {
        		if (mFooterBottom + delta > 0) {
	                delta = -mFooterBottom;
	            }
        	}
        } else if (delta < 0) {
        	if(mIsUsingHeader) {
	            if (mHeaderTop + delta < -mHeaderHeight) {
	                delta = -(mHeaderHeight + mHeaderTop);
	            }
        	} else {
        		if (mFooterBottom + delta < -mFooterHeight) {
	                delta = -(mFooterHeight + mFooterBottom);
	            }
        	}
        } else {
            return;
        }
        mIsScrollingUp = delta < 0;
        
        // I'm aware that offsetTopAndBottom is more efficient, but it gave me trouble when scrolling to the bottom of the list
        if(mIsUsingHeader) {
        	mHeaderTop += delta;
	        if (mRealHeaderLayoutParams.topMargin != mHeaderTop) {
	            mRealHeaderLayoutParams.topMargin = mHeaderTop;
	            //Log.v(TAG, "topMargin=" + mHeaderTop);
	            mRealHeader.setLayoutParams(mRealHeaderLayoutParams);
	        }
        }
        if(mIsUsingFooter) {
        	mFooterBottom += delta;
        	if (mRealFooterLayoutParams.bottomMargin != mFooterBottom) {
            	mRealFooterLayoutParams.bottomMargin = mFooterBottom;
            	//Log.v(TAG, "bottomMargin=" + mFooterBottom);
                mRealFooter.setLayoutParams(mRealFooterLayoutParams);
            }
        }
    }

    private void snap(boolean newValue) {
        if (mIsSnapped == newValue) {
            return;
        }
        mIsSnapped = newValue;
        if (mOnSnappedChangeListener != null) {
            mOnSnappedChangeListener.onSnappedChange(mIsSnapped);
        }
    }
}
