package com.nsa.flexjobs.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsa.flexjobs.Adapters.AppliedAdaper;
import com.nsa.flexjobs.R;
import com.nsa.flexjobs.activities.MyTasksActivity;

import static com.nsa.flexjobs.activities.TaskActivity.appliedModelList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppliedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppliedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppliedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppliedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppliedFragment newInstance(String param1, String param2) {
        AppliedFragment fragment = new AppliedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    RecyclerView appliedRV;
    AppliedAdaper appliedAdaper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.e(TAG,"onCreate");

    }

    String TAG="appiedfrag";
    TextView infoView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"oncreateview");
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_applied, container, false);
       appliedRV=view.findViewById(R.id.appliedRV);
        infoView=view.findViewById(R.id.infoView);
        checkSize();
        appliedRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        appliedAdaper=new AppliedAdaper(appliedModelList,getContext());
        MyTasksActivity.appliedAdaper=appliedAdaper;
        appliedRV.setAdapter(appliedAdaper);
        appliedAdaper.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkSize();
                super.onChanged();
            }
        });


    return view;
    }

    private void checkSize() {
        if(appliedModelList.size()>0){
            infoView.setVisibility(View.GONE);
        }else{
            infoView.setVisibility(View.VISIBLE);
        }
    }
}