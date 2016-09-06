package com.littlechoc.flowlayout;

import android.view.View;
import android.view.ViewGroup;

/**
 * FlowLayoutAdapter
 *
 * @author 周俊皓 2016/09/06
 **/

public abstract class FlowLayoutAdapter {

  public int getCount() {
    return 0;
  }

  public Object getItem(int position) {
    return null;
  }

  public abstract View getView(ViewGroup parent, int position);

  public void notifyDataSetChanged() {
    // TODO Not support yet
  }
}
