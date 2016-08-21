package com.tec.jca.faceverification.ui;
import com.tec.jca.faceverification.R;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * �̳���SwipeRefreshLayout,�Ӷ�ʵ�ֻ������ײ�ʱ�������ظ���Ĺ���.
 * 
 * @author mrsimple
 */
public class RefreshLayout extends SwipeRefreshLayout implements OnScrollListener {

  /**
   * ������������ʱ����������
   */

  private int mTouchSlop;
  /**
   * listviewʵ��
   */
  private ListView mListView;

  /**
   * ����������, ������ײ����������ز���
   */
  private OnLoadListener mOnLoadListener;

  /**
   * ListView�ļ�����footer
   */
  private View mListViewFooter;

  /**
   * ����ʱ��y����
   */
  private int mYDown;
  /**
   * ̧��ʱ��y����, ��mYDownһ�����ڻ������ײ�ʱ�ж���������������
   */
  private int mLastY;
  /**
   * �Ƿ��ڼ����� ( �������ظ��� )
   */
  private boolean isLoading = false;

  /**
   * @param context
   */
  public RefreshLayout(Context context) {
    this(context, null);
  }

  public RefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,
        false);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    // ��ʼ��ListView����
    if (mListView == null) {
      getListView();
    }
  }

  /**
   * ��ȡListView����
   */
  private void getListView() {
    int childs = getChildCount();
    if (childs > 0) {
      View childView = getChildAt(0);
      if (childView instanceof ListView) {
        mListView = (ListView) childView;
        // ���ù�����������ListView, ʹ�ù����������Ҳ�����Զ�����
        mListView.setOnScrollListener(this);
        Log.d(VIEW_LOG_TAG, "### �ҵ�listview");
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    final int action = event.getAction();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        // ����
        mYDown = (int) event.getRawY();
        break;

      case MotionEvent.ACTION_MOVE:
        // �ƶ�
        mLastY = (int) event.getRawY();
        break;

      case MotionEvent.ACTION_UP:
        // ̧��
        if (canLoad()) {
          loadData();
        }
        break;
      default:
        break;
    }

    return super.dispatchTouchEvent(event);
  }

  /**
   * �Ƿ���Լ��ظ���, �����ǵ�����ײ�, listview���ڼ�����, ��Ϊ��������.
   * 
   * @return
   */
  private boolean canLoad() {
    return isBottom() && !isLoading && isPullUp();
  }

  /**
   * �ж��Ƿ�����ײ�
   */
  private boolean isBottom() {

    if (mListView != null && mListView.getAdapter() != null) {
      return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
    }
    return false;
  }

  /**
   * �Ƿ�����������
   * 
   * @return
   */
  private boolean isPullUp() {
    return (mYDown - mLastY) >= mTouchSlop;
  }

  /**
   * ���������ײ�,��������������.��ôִ��onLoad����
   */
  private void loadData() {
    if (mOnLoadListener != null) {
      // ����״̬
      setLoading(true);
      //
      mOnLoadListener.onLoad();
    }
  }

  /**
   * @param loading
   */
  public void setLoading(boolean loading) {
    isLoading = loading;
    if (isLoading) {
      mListView.addFooterView(mListViewFooter);
    } else {
      mListView.removeFooterView(mListViewFooter);
      mYDown = 0;
      mLastY = 0;
    }
  }

  /**
   * @param loadListener
   */
  public void setOnLoadListener(OnLoadListener loadListener) {
    mOnLoadListener = loadListener;
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    // ����ʱ������ײ�Ҳ���Լ��ظ���
    if (canLoad()) {
      loadData();
    }
  }

  /**
   * ���ظ���ļ�����
   * 
   * @author mrsimple
   */
  public static interface OnLoadListener {
    public void onLoad();
  }
}