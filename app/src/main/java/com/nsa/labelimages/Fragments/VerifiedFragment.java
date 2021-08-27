package com.nsa.labelimages.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsa.labelimages.Adapters.AppliedAdaper;
import com.nsa.labelimages.Adapters.VerifiedAdaper;
import com.nsa.labelimages.R;
import com.nsa.labelimages.activities.MyTasksActivity;


import static com.nsa.labelimages.activities.TaskActivity.verifiedModelList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifiedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifiedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VerifiedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifiedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifiedFragment newInstance(String param1, String param2) {
        VerifiedFragment fragment = new VerifiedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
   RecyclerView verifiedRV;
    VerifiedAdaper verifiedAdaper;
    TextView infoView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_verified, container, false);
        verifiedRV=view.findViewById(R.id.verifiedRV);
        infoView=view.findViewById(R.id.infoView);
        checkSize();
        verifiedRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        verifiedAdaper=new VerifiedAdaper(verifiedModelList,getContext());
        MyTasksActivity.verifiedAdaper=verifiedAdaper;
        verifiedRV.setAdapter(verifiedAdaper);

        verifiedAdaper.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkSize();
                super.onChanged();
            }
        });

        return view;
    }

    private void checkSize() {
        if(verifiedModelList.size()>0){
            infoView.setVisibility(View.GONE);
        }else{
            infoView.setVisibility(View.VISIBLE);
        }
    }

    String TAG="verified";
}