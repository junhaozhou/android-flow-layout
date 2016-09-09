package com.littlechoc.flowlayout.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.littlechoc.flowlayout.FlowLayout;

/**
 * Description
 *
 * @author 周俊皓 2016/09/06
 **/

public class MainActivity extends AppCompatActivity {

  private FlowLayout mFlowLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Adapter mAdapter = new Adapter();
    mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
    mFlowLayout.setAdapter(mAdapter);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.gravity_start:
        mFlowLayout.setGravity(FlowLayout.START);
        return true;
      case R.id.gravity_center:
        mFlowLayout.setGravity(FlowLayout.CENTER);
        return true;
      case R.id.gravity_end:
        mFlowLayout.setGravity(FlowLayout.END);
        return true;
      case R.id.gravity_align:
        mFlowLayout.setGravity(FlowLayout.ALIGN);
        return true;
      case R.id.lines_unlimited:
        mFlowLayout.setMaxLines(FlowLayout.UNLIMITED_LINES);
        return true;
      case R.id.lines_one:
        mFlowLayout.setMaxLines(1);
        return true;
      case R.id.lines_two:
        mFlowLayout.setMaxLines(2);
        return true;
      case R.id.lines_three:
        mFlowLayout.setMaxLines(3);
        return true;
      case R.id.choice_mode_none:
        mFlowLayout.setChoiceMode(FlowLayout.CHOICE_MODE_NONE);
        return true;
      case R.id.choice_mode_single:
        mFlowLayout.setChoiceMode(FlowLayout.CHOICE_MODE_SINGLE);
        return true;
      case R.id.choice_mode_multi:
        mFlowLayout.setChoiceMode(FlowLayout.CHOICE_MODE_MULTI);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
