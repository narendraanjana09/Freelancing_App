package com.nsa.flexjobs.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nsa.flexjobs.Adapters.TaskAdaper;
import com.nsa.flexjobs.R;

import static com.nsa.flexjobs.activities.TaskActivity.getTasks;
import static com.nsa.flexjobs.activities.TaskActivity.taskModelList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreTaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreTaskFragment() {
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
    public static ExploreTaskFragment newInstance(String param1, String param2) {
        ExploreTaskFragment fragment = new ExploreTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static TaskAdaper taskAdaper;
    RecyclerView taskRV;
   public static SwipeRefreshLayout progressView;
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
    TextView infoTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"oncreateview");
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_explore_task, container, false);
        taskRV=view.findViewById(R.id.taskRecylerView);
        infoTV=view.findViewById(R.id.infoView);
        progressView=view.findViewById(R.id.swipeContainer);
        progressView.setRefreshing(true);

        progressView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                    progressView.setRefreshing(true);

                getTasks();
            }
        });
        progressView.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        taskAdaper=new TaskAdaper(taskModelList, getContext());
        taskRV.setAdapter(taskAdaper);
        taskAdaper.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                progressView.setRefreshing(false);
                infoTV.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onChanged() {
                if(taskModelList.size()==0){
                    infoTV.setVisibility(View.VISIBLE);
                }else{
                    infoTV.setVisibility(View.GONE);
                }
                super.onChanged();
            }
        });

    return view;
    }

}