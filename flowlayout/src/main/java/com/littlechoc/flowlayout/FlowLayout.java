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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter support FlowLayout.
 */
public class FlowLayout extends ViewGroup {

  /**
   * 默认模式
   */
  private static final int MODE_NONE = 0;

  /**
   * 两端对齐模式
   */
  private static final int MDOE_ALIGN = 1;

  /**
   * 压缩模式
   */
  private static final int MODE_COMPRESS = 2;

  private int mMode;

  private Context mContext;
  private int usefulWidth; // the space of a line we can use(line's width minus the sum of left and right padding
  private int mLinePadding = 0; // the spacing between lines in flow layout
  private List<View> childList = new ArrayList<>();
  private List<Integer> lineNumList = new ArrayList<>();

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
        R.styleable.FlowLayout_linePadding, 0);
    mMode = mTypedArray.getInt(R.styleable.FlowLayout_mode, MODE_NONE);
    mTypedArray.recycle();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int mPaddingLeft = getPaddingLeft();
    int mPaddingRight = getPaddingRight();
    int mPaddingTop = getPaddingTop();
    int mPaddingBottom = getPaddingBottom();

    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int lineUsed = mPaddingLeft + mPaddingRight;
    int lineY = mPaddingTop;
    int lineHeight = 0;
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
        lineY += lineHeight + mLinePadding;
        lineUsed = mPaddingLeft + mPaddingRight;
        lineHeight = 0;
      }
      if (spaceHeight > lineHeight) {
        lineHeight = spaceHeight;
      }
      lineUsed += spaceWidth;
    }
    setMeasuredDimension(
        widthSize,
        heightMode == MeasureSpec.EXACTLY ? heightSize : lineY + lineHeight + mPaddingBottom
    );
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int mPaddingLeft = getPaddingLeft();
    int mPaddingRight = getPaddingRight();
    int mPaddingTop = getPaddingTop();

    int lineX = mPaddingLeft;
    int lineY = mPaddingTop;
    int lineWidth = r - l;
    usefulWidth = lineWidth - mPaddingLeft - mPaddingRight;
    int lineUsed = mPaddingLeft + mPaddingRight;
    int lineHeight = 0;
    int lineNum = 0;

    lineNumList.clear();
    for (int i = 0; i < this.getChildCount(); i++) {
      View child = this.getChildAt(i);
      if (child.getVisibility() == GONE) {
        continue;
      }
      int spaceWidth = 0;
      int spaceHeight = 0;
      int left;
      int top;
      int right;
      int bottom;
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();

      LayoutParams childLp = child.getLayoutParams();
      if (childLp instanceof MarginLayoutParams) {
        MarginLayoutParams mlp = (MarginLayoutParams) childLp;
        spaceWidth = mlp.leftMargin + mlp.rightMargin;
        spaceHeight = mlp.topMargin + mlp.bottomMargin;
        left = lineX + mlp.leftMargin;
        top = lineY + mlp.topMargin;
        right = lineX + mlp.leftMargin + childWidth;
        bottom = lineY + mlp.topMargin + childHeight;
      } else {
        left = lineX;
        top = lineY;
        right = lineX + childWidth;
        bottom = lineY + childHeight;
      }
      spaceWidth += childWidth;
      spaceHeight += childHeight;

      if (lineUsed + spaceWidth > lineWidth) {
        //approach the limit of width and move to next line
        lineNumList.add(lineNum);
        lineY += lineHeight + mLinePadding;
        lineUsed = mPaddingLeft + mPaddingRight;
        lineX = mPaddingLeft;
        lineHeight = 0;
        lineNum = 0;
        if (childLp instanceof MarginLayoutParams) {
          MarginLayoutParams mlp = (MarginLayoutParams) childLp;
          left = lineX + mlp.leftMargin;
          top = lineY + mlp.topMargin;
          right = lineX + mlp.leftMargin + childWidth;
          bottom = lineY + mlp.topMargin + childHeight;
        } else {
          left = lineX;
          top = lineY;
          right = lineX + childWidth;
          bottom = lineY + childHeight;
        }
      }
      child.layout(left, top, right, bottom);
      lineNum++;
      if (spaceHeight > lineHeight) {
        lineHeight = spaceHeight;
      }
      lineUsed += spaceWidth;
      lineX += spaceWidth;
    }
    // add the num of last line
    lineNumList.add(lineNum);
  }

  public void setAdapter(FlowLayoutAdapter adapter) {
    if (adapter == null) {
      throw new IllegalArgumentException("");
    }
    removeAllViews();
    int count = adapter.getCount();
    for (int i = 0; i < count; i++) {
      addView(adapter.getView(this, i));
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
   * cut the flowlayout to the specified num of lines
   *
   * @param line_num
   */
  public void specifyLines(int line_num) {
    int childNum = 0;
    if (line_num > lineNumList.size()) {
      line_num = lineNumList.size();
    }
    for (int i = 0; i < line_num; i++) {
      childNum += lineNumList.get(i);
    }
    List<View> viewList = new ArrayList<>();
    for (int i = 0; i < childNum; i++) {
      viewList.add(getChildAt(i));
    }
    removeAllViews();
    for (View v : viewList) {
      addView(v);
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