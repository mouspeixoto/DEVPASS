package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostUsuarioFragment extends Fragment {


    public PostUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_usuario, container, false);
    }

}
