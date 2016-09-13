package com.littlechoc.flowlayout;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * FlowLayoutAdapter
 *
 * @author 周俊皓 2016/09/06
 **/

public abstract class FlowLayoutAdapter {

  private DataSetObservable mDataSetObservable = new DataSetObservable();

  public int getCount() {
    return 0;
  }

  public Object getItem(int position) {
    return null;
  }

  public abstract View getView(ViewGroup parent, int position);

  public void notifyDataSetChanged() {
    mDataSetObservable.notifyChanged();
  }

  public void registerDataSetObserver(DataSetObserver observer) {
    mDataSetObservable.registerObserver(observer);
  }

  public void unregisterDataSetObserver(DataSetObserver observer) {
    mDataSetObservable.unregisterObserver(observer);
  }
}
