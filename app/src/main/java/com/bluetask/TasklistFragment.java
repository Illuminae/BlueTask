package com.bluetask;

/**
 * Created by Sunacni on 3/11/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class TasklistFragment extends Fragment {
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasklist, container, false);
    }
}