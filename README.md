# Android Flow Layout
A android flow layout which can add child view via a adapter.

## Features
- support adapter
- support gravity: start, center, end and align
- support line padding

## Usage
In xml：
```
<com.littlechoc.flowlayout.FlowLayout
        android:id="@+id/flow_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:fl_linePadding="2dp" // the padding between lines except header and footer
        app:fl_maxLines="2"
        app:fl_gravity="align"
        />
```
In code:

Extend `FlowLayoutAdapter` and add the adapter to `FlowLayout` via `FlowLayout.setAdapter()`.
You can see this code in module `sample`.

## Install
```
dependecies{
  compile 'com.littlechoc.flowlayout:flowlayout:0.0.1'
}
```

## TODO
- support `notifyDataSetChanged`
- support choice mode `single`and`multi`
