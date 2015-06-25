package me.martl.e_ballot;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Mart on 12.05.2015.
 */
public class YourBallotsFragment extends Fragment {
    protected View rootView;
    public static final String ARG_TAB_NUMBER = "tab number";


    public YourBallotsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_your_ballots, container, false);
        // Uses a layout with a ListView (id: "listview"), which uses our Adapter.

        final CustomAdapter myAdapter = new CustomAdapter(rootView.getContext());

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(myAdapter);
        myAdapter.loadObjects();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("ASD", Integer.toString(position));
                ArrayList<String> ballotIds = myAdapter.getList();
                //Log.d("asd2", ballotIds.get(position) );
                //Toast.makeText(getActivity(), ballotIds.get(position), Toast.LENGTH_SHORT).show();

                Fragment fragment = new NewBallotFragment();
                Bundle args = new Bundle();
                args.putBoolean("update", true);
                args.putString("id", ballotIds.get(position));
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });
        return rootView;
    }


}
