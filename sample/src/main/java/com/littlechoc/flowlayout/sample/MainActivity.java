package com.littlechoc.flowlayout.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.littlechoc.flowlayout.FlowLayout;

/**
 * Description
 *
 * @author 周俊皓 2016/09/06
 **/

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Adapter mAdapter = new Adapter();
    FlowLayout mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
    mFlowLayout.setAdapter(mAdapter);

  }
}
