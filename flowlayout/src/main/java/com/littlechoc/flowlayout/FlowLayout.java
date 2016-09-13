/*
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 陶方鑫
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.littlechoc.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * FlowLayout
 * <p/>
 * gravity: start, center, end, align
 * <p/>
 * choice mode: none, single, multi
 * <p/>
 * linePadding
 * <p/>
 * maxLines
 */
public class FlowLayout extends ViewGroup {

  /**
   * max lines is unlimited
   */
  public static final int UNLIMITED_LINES = 0;

  /**
   * gravity start
   */
  public static final int START = 0;

  /**
   * gravity center
   */
  public static final int CENTER = 1;

  /**
   * gravity end
   */
  public static final int END = 2;

  /**
   * gravity justify align
   */
  public static final int ALIGN = 3;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({START, CENTER, END, ALIGN})
  public @interface Gravity {
  }

  /**
   * support no choice
   */
  public static final int CHOICE_MODE_NONE = 0;

  /**
   * support single choice only
   */
  public static final int CHOICE_MODE_SINGLE = 1;

  /**
   * support multi choice
   */
  public static final int CHOICE_MODE_MULTI = 2;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({CHOICE_MODE_NONE, CHOICE_MODE_SINGLE, CHOICE_MODE_MULTI})
  public @interface ChoiceMode {
  }

  /**
   * gravity, default is start
   */
  private int mGravity;

  /**
   * padding between lines except the header and footer, default is 0
   */
  private int mLinePadding;

  /**
   * max lines to be shown the rest item would not be shown, default is unlimited
   */
  private int mMaxLines;

  /**
   * choice mode,default is none
   */
  private int mChoiceMode;

  private Context mContext;

  private int usefulWidth; // the space of a line we can use(line's width minus the sum of left and right padding

  private List<View> childList = new ArrayList<>();

  private DataSetObserver mDataSetObserver;

  private FlowLayoutAdapter mAdapter;

  public FlowLayout(Context context) {
    this(context, null);
  }

  public FlowLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
        R.styleable.FlowLayout);
    mLinePadding = mTypedArray.getDimensionPixelSize(
        R.styleable.FlowLayout_fl_linePadding, 0);
    mMaxLines = mTypedArray.getInteger(R.styleable.FlowLayout_fl_maxLines, UNLIMITED_LINES);
    mGravity = mTypedArray.getInt(R.styleable.FlowLayout_fl_gravity, START);
    mChoiceMode = mTypedArray.getInt(R.styleable.FlowLayout_fl_choiceMode, CHOICE_MODE_NONE);
    mTypedArray.recycle();
  }

  public int getGravity() {
    return mGravity;
  }

  public void setGravity(@Gravity int gravity) {
    this.mGravity = gravity;
    requestLayout();
  }

  public int getLinePadding() {
    return mLinePadding;
  }

  public void setLinePadding(int linePadding) {
    this.mLinePadding = linePadding < 0 ? 0 : linePadding;
    requestLayout();
  }

  public int getMaxLines() {
    return mMaxLines;
  }

  public void setMaxLines(int maxLines) {
    this.mMaxLines = maxLines <= 0 ? UNLIMITED_LINES : maxLines;
    requestLayout();
  }

  public int getChoiceMode() {
    return mChoiceMode;
  }

  public void setChoiceMode(@ChoiceMode int choiceMode) {
    this.mChoiceMode = choiceMode;
  }

  public void setAdapter(FlowLayoutAdapter adapter) {
    if (adapter == null) {
      throw new IllegalArgumentException("");
    }

    // clear
    if (mAdapter != null && mDataSetObserver != null) {
      mAdapter.unregisterDataSetObserver(mDataSetObserver);
    }
    removeAllViews();

    mAdapter = adapter;
    mDataSetObserver = new AdapterDataSetObserver();
    mAdapter.registerDataSetObserver(mDataSetObserver);

    int count = mAdapter.getCount();
    for (int i = 0; i < count; i++) {
      addView(adapter.getView(this, i));
    }
  }

  private class AdapterDataSetObserver extends DataSetObserver {
    @Override
    public void onChanged() {
      removeAllViews();
      int count = mAdapter.getCount();
      for (int i = 0; i < count; i++) {
        addView(mAdapter.getView(FlowLayout.this, i));
      }
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int paddingLeft = getPaddingLeft();
    int paddingRight = getPaddingRight();
    int paddingTop = getPaddingTop();
    int paddingBottom = getPaddingBottom();

    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int lineUsed = paddingLeft + paddingRight;
    int lineY = paddingTop;
    int lineHeight = 0;

    int lines = 0;

    for (int i = 0; i < this.getChildCount(); i++) {
      View child = this.getChildAt(i);
      if (child.getVisibility() == GONE) {
        continue;
      }
      int spaceWidth = 0;
      int spaceHeight = 0;
      LayoutParams childLp = child.getLayoutParams();
      if (childLp instanceof MarginLayoutParams) {
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, lineY);
        MarginLayoutParams mlp = (MarginLayoutParams) childLp;
        spaceWidth = mlp.leftMargin + mlp.rightMargin;
        spaceHeight = mlp.topMargin + mlp.bottomMargin;
      } else {
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
      }

      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      spaceWidth += childWidth;
      spaceHeight += childHeight;

      if (lineUsed + spaceWidth > widthSize) {
        //approach the limit of width and move to next line
        lineY += lineHeight;
        lineUsed = paddingLeft + paddingRight;
        lineHeight = 0;
        lines++;
        if (mMaxLines != UNLIMITED_LINES && lines >= mMaxLines) {
          break;
        }
        lineY += mLinePadding;
      }
      if (spaceHeight > lineHeight) {
        lineHeight = spaceHeight;
      }
      lineUsed += spaceWidth;
    }

    setMeasuredDimension(
        widthSize,
        heightMode == MeasureSpec.EXACTLY ? heightSize : lineY + lineHeight + paddingBottom
    );
  }

  private List<LineData> mLines = new ArrayList<>();

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int paddingLeft = getPaddingLeft();
    int paddingRight = getPaddingRight();
    int paddingTop = getPaddingTop();

    int width = r - l;
    int lineWidth = 0;
    int lineHeight = 0;

    LineData lineData = LineData.generate();
    mLines.clear();
    int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);
      if (child.getVisibility() == GONE) { // do not layout if child is gone
        continue;
      }
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

      if (lineWidth + childWidth + params.rightMargin + params.leftMargin > width - paddingLeft - paddingRight) { // the rest space can not store this child
        lineData.lineWidth = lineWidth;
        lineData.lineHeight = lineHeight;
        mLines.add(lineData);

        // reset
        lineWidth = 0;
        lineHeight = 0;
        lineData = LineData.generate();
      }
      lineWidth += childWidth + params.rightMargin + params.leftMargin;
      lineHeight = Math.max(lineHeight, childHeight + params.bottomMargin + params.topMargin);
      lineData.lineChild.add(child);
    }
    // add last line
    lineData.lineWidth = lineWidth;
    lineData.lineHeight = lineHeight;
    mLines.add(lineData);

    usefulWidth = width - paddingLeft - paddingRight;
    // begin layout
    int lines = mLines.size();
    int showLines = mMaxLines == UNLIMITED_LINES ? lines : Math.min(mMaxLines, lines);

    int left;
    int top = paddingTop;

    for (int i = 0; i < showLines; i++) {
      lineData = mLines.get(i);
      int space = 0;
      switch (mGravity) { // left position
        case START:
          left = paddingLeft;
          break;
        case CENTER:
          left = paddingLeft + (width - lineData.lineWidth) / 2;
          break;
        case END:
          left = width - paddingRight - lineData.lineWidth;
          break;
        case ALIGN:
          left = paddingLeft;
          int count = lineData.lineChild.size();
          space = count == 1 ? 0 : (width - lineData.lineWidth) / (count - 1);
          break;
        default:
          left = paddingLeft;
          break;
      }

      for (int j = 0; j < lineData.lineChild.size(); j++) {
        View child = lineData.lineChild.get(j);
        if (child.getVisibility() == GONE) {
          continue;
        }
        MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
        int ll = left + params.leftMargin;
        int tt = top + params.topMargin;
        int bb = tt + child.getMeasuredHeight();
        int rr = ll + child.getMeasuredWidth();
        child.layout(ll, tt, rr, bb);
        left += child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
        if (mGravity == ALIGN) {
          left += space;
        }
      }
      top += lineData.lineHeight + mLinePadding;
    }
  }

  /**
   * resort child elements to use lines as few as possible
   */
  public void relayoutToCompress() {
    int childCount = this.getChildCount();
    if (0 == childCount) {
      //no need to sort if flowlayout has no child view
      return;
    }
    int count = 0;
    for (int i = 0; i < childCount; i++) {
      View v = getChildAt(i);
      if (v instanceof BlankView) {
        //BlankView is just to make childs look in alignment, we should ignore them when we relayout
        continue;
      }
      count++;
    }
    View[] childs = new View[count];
    int[] spaces = new int[count];
    int n = 0;
    for (int i = 0; i < childCount; i++) {
      View v = getChildAt(i);
      if (v instanceof BlankView) {
        //BlankView is just to make childs look in alignment, we should ignore them when we relayout
        continue;
      }
      childs[n] = v;
      LayoutParams childLp = v.getLayoutParams();
      int childWidth = v.getMeasuredWidth();
      if (childLp instanceof MarginLayoutParams) {
        MarginLayoutParams mlp = (MarginLayoutParams) childLp;
        spaces[n] = mlp.leftMargin + childWidth + mlp.rightMargin;
      } else {
        spaces[n] = childWidth;
      }
      n++;
    }
    int[] compressSpaces = new int[count];
    for (int i = 0; i < count; i++) {
      compressSpaces[i] = spaces[i] > usefulWidth ? usefulWidth : spaces[i];
    }
    sortToCompress(childs, compressSpaces);
    this.removeAllViews();
    for (View v : childList) {
      this.addView(v);
    }
    childList.clear();
  }

  private void sortToCompress(View[] childs, int[] spaces) {
    int childCount = childs.length;
    int[][] table = new int[childCount + 1][usefulWidth + 1];
    for (int i = 0; i < childCount + 1; i++) {
      for (int j = 0; j < usefulWidth; j++) {
        table[i][j] = 0;
      }
    }
    boolean[] flag = new boolean[childCount];
    for (int i = 0; i < childCount; i++) {
      flag[i] = false;
    }
    for (int i = 1; i <= childCount; i++) {
      for (int j = spaces[i - 1]; j <= usefulWidth; j++) {
        table[i][j] = (table[i - 1][j] > table[i - 1][j - spaces[i - 1]] + spaces[i - 1]) ? table[i - 1][j] : table[i - 1][j - spaces[i - 1]] + spaces[i - 1];
      }
    }
    int v = usefulWidth;
    for (int i = childCount; i > 0 && v >= spaces[i - 1]; i--) {
      if (table[i][v] == table[i - 1][v - spaces[i - 1]] + spaces[i - 1]) {
        flag[i - 1] = true;
        v = v - spaces[i - 1];
      }
    }
    int rest = childCount;
    View[] restArray;
    int[] restSpaces;
    for (int i = 0; i < flag.length; i++) {
      if (flag[i]) {
        childList.add(childs[i]);
        rest--;
      }
    }

    if (0 == rest) {
      return;
    }
    restArray = new View[rest];
    restSpaces = new int[rest];
    int index = 0;
    for (int i = 0; i < flag.length; i++) {
      if (!flag[i]) {
        restArray[index] = childs[i];
        restSpaces[index] = spaces[i];
        index++;
      }
    }
    table = null;
    childs = null;
    flag = null;
    sortToCompress(restArray, restSpaces);
  }

  /**
   * add some blank view to make child elements look in alignment
   */
  public void relayoutToAlign() {
    int childCount = this.getChildCount();
    if (0 == childCount) {
      //no need to sort if flowlayout has no child view
      return;
    }
    int count = 0;
    for (int i = 0; i < childCount; i++) {
      View v = getChildAt(i);
      if (v instanceof BlankView) {
        //BlankView is just to make childs look in alignment, we should ignore them when we relayout
        continue;
      }
      count++;
    }
    View[] childs = new View[count];
    int[] spaces = new int[count];
    int n = 0;
    for (int i = 0; i < childCount; i++) {
      View v = getChildAt(i);
      if (v instanceof BlankView) {
        //BlankView is just to make childs look in alignment, we should ignore them when we relayout
        continue;
      }
      childs[n] = v;
      LayoutParams childLp = v.getLayoutParams();
      int childWidth = v.getMeasuredWidth();
      if (childLp instanceof MarginLayoutParams) {
        MarginLayoutParams mlp = (MarginLayoutParams) childLp;
        spaces[n] = mlp.leftMargin + childWidth + mlp.rightMargin;
      } else {
        spaces[n] = childWidth;
      }
      n++;
    }
    int lineTotal = 0;
    int start = 0;
    this.removeAllViews();
    for (int i = 0; i < count; i++) {
      if (lineTotal + spaces[i] > usefulWidth) {
        int blankWidth = usefulWidth - lineTotal;
        int end = i - 1;
        int blankCount = end - start;
        if (blankCount >= 0) {
          if (blankCount > 0) {
            int eachBlankWidth = blankWidth / blankCount;
            MarginLayoutParams lp = new MarginLayoutParams(eachBlankWidth, 0);
            for (int j = start; j < end; j++) {
              this.addView(childs[j]);
              BlankView blank = new BlankView(mContext);
              this.addView(blank, lp);
            }
          }
          this.addView(childs[end]);
          start = i;
          i--;
          lineTotal = 0;
        } else {
          this.addView(childs[i]);
          start = i + 1;
          lineTotal = 0;
        }
      } else {
        lineTotal += spaces[i];
      }
    }
    for (int i = start; i < count; i++) {
      this.addView(childs[i]);
    }
  }

  /**
   * use both of relayout methods together
   */
  public void relayoutToCompressAndAlign() {
    this.relayoutToCompress();
    this.relayoutToAlign();
  }

  /**
   * LineData store line height, line width and the child in this line
   */
  private static class LineData {
    int lineHeight;
    int lineWidth;
    List<View> lineChild;

    public LineData() {
      this.lineHeight = 0;
      this.lineWidth = 0;
      this.lineChild = new ArrayList<>();
    }

    static LineData generate() {
      return new LineData();
    }
  }

  @Override
  protected LayoutParams generateLayoutParams(LayoutParams p) {
    return new MarginLayoutParams(p);
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(super.generateDefaultLayoutParams());
  }

  class BlankView extends View {

    public BlankView(Context context) {
      super(context);
    }
  }
}