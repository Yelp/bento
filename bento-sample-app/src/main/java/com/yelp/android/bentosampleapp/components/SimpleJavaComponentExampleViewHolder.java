package com.yelp.android.bentosampleapp.components;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.yelp.android.bento.componentcontrollers.SimpleComponentViewHolder;
import com.yelp.android.bentosampleapp.R;

public class SimpleJavaComponentExampleViewHolder extends SimpleComponentViewHolder<Void> {
    private TextView mTextView;

    public SimpleJavaComponentExampleViewHolder() {
        super(R.layout.simple_component_example);
    }

    @Override
    protected void onViewCreated(@NonNull View itemView) {
        mTextView = itemView.findViewById(R.id.text);
    }

    @Override
    public void bind(@NonNull Void presenter) {
        mTextView.setText("This is a simple component written in Java.");
    }
}
