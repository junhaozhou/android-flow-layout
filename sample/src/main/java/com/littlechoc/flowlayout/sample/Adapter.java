package com.littlechoc.flowlayout.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlechoc.flowlayout.FlowLayoutAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adapter
 *
 * @author 周俊皓 2016/09/06
 **/

public class Adapter extends FlowLayoutAdapter {

  private static final List<String> LABELS = new ArrayList<>(Arrays.asList(new String[]{"标签 1", "标签 2", "标签 3", "标签 444", "标签 555", "标签 6666", "标签777", "标签 888", "标签999", "标签 10",}));

  @Override
  public int getCount() {
    return LABELS.size();
  }

  @Override
  public Object getItem(int position) {
    return LABELS.get(position);
  }

  @Override
  public View getView(ViewGroup parent, int position) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View child = inflater.inflate(R.layout.item_tag, parent, false);
    TextView tv = (TextView) child.findViewById(R.id.tag);
    tv.setText(LABELS.get(position));
    return child;
  }

  public void add() {
    LABELS.add("标签" + (int) (Math.random() * 100));
    notifyDataSetChanged();
  }

  public void delete() {
    if (!LABELS.isEmpty()) {
      LABELS.remove(LABELS.size() - 1);
      notifyDataSetChanged();
    }
  }

  public void clear() {
    LABELS.clear();
    notifyDataSetChanged();
  }
}
