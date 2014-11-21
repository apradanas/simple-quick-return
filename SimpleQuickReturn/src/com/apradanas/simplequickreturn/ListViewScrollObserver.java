package com.apradanas.simplequickreturn;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * @author apradanas
 */
public class ListViewScrollObserver implements OnScrollListener {
    private OnListViewScrollListener mListViewScrollListener;
    private int mLastFirstVisibleItem;
    private int mLastTop;
    private int mScrollPosition;
    private int mLastHeight;

    public interface OnListViewScrollListener {
        void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact);
        void onScrollIdle();
    }

    public ListViewScrollObserver(ListView listView) {
        listView.setOnScrollListener(this);
    }

    public void setOnScrollUpAndDownListener(OnListViewScrollListener listener) {
        this.mListViewScrollListener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        View firstChild = view.getChildAt(0);
        if (firstChild == null) {
            return;
        }
        int top = firstChild.getTop();
        int height = firstChild.getHeight();
        int delta;
        int skipped = 0;
        if (mLastFirstVisibleItem == firstVisibleItem) {
            delta = mLastTop - top;
        } else if (firstVisibleItem > mLastFirstVisibleItem) {
            skipped = firstVisibleItem - mLastFirstVisibleItem - 1;
            delta = skipped * height + mLastHeight + mLastTop - top;
        } else {
            skipped = mLastFirstVisibleItem - firstVisibleItem - 1;
            delta = skipped * -height + mLastTop - (height + top);
        }
        boolean exact = skipped > 0;
        mScrollPosition += -delta;
        if (mListViewScrollListener != null) {
            mListViewScrollListener.onScrollUpDownChanged(-delta, mScrollPosition, exact);
        }
        mLastFirstVisibleItem = firstVisibleItem;
        mLastTop = top;
        mLastHeight = firstChild.getHeight();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mListViewScrollListener != null && scrollState == SCROLL_STATE_IDLE) {
            mListViewScrollListener.onScrollIdle();
        }
    }
}
