package me.martl.e_ballot;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Mart on 16.05.2015.
 */


public class CustomAdapter extends ParseQueryAdapter<ParseObject> {
    public ArrayList<String> ballotIds = new ArrayList<>();

    public CustomAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Ballot");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.adapter_item, null);
        }

        super.getItemView(object, v, parent);

        TextView roomTextView = (TextView) v.findViewById(R.id.text1);
        roomTextView.setText(object.getString("room_number"));

        TextView roundTextView = (TextView) v.findViewById(R.id.round);
        roundTextView.setText(Integer.toString(object.getInt("round")));

        TextView motionTextView = (TextView) v.findViewById(R.id.updated);
        motionTextView.setText(object.getString("motion"));
        ballotIds.add(object.getObjectId());
        //Log.d("Adapteris", object.getObjectId());
        return v;
    }

    public ArrayList<String> getList(){
        return ballotIds;
    }

}
