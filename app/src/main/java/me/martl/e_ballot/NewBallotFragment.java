package me.martl.e_ballot;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Mart on 10.05.2015.
 */
public class NewBallotFragment extends Fragment {
    public static final String ARG_TAB_NUMBER = "tab number";
    public boolean update = false;
    public String id;
    protected View rootView;
    protected EditText room_number;
    protected int round_number;
    protected MultiAutoCompleteTextView adjudicators;
    protected EditText motion;
    protected EditText og_team, oo_team, cg_team, co_team;
    protected EditText og_speaker_1, oo_speaker_1, cg_speaker_1, co_speaker_1;
    protected EditText og_speaker_1_points, oo_speaker_1_points, cg_speaker_1_points, co_speaker_1_points;
    protected EditText og_speaker_2, oo_speaker_2, cg_speaker_2, co_speaker_2;
    protected EditText og_speaker_2_points, oo_speaker_2_points, cg_speaker_2_points, co_speaker_2_points;
    protected int og_points_sum, oo_points_sum, cg_points_sum, co_points_sum;
    protected int og_position, oo_position, cg_position, co_position;
    protected int og_s1_points_int, og_s2_points_int, oo_s1_points_int, oo_s2_points_int, cg_s1_points_int, cg_s2_points_int, co_s1_points_int, co_s2_points_int;
    protected ArrayList<String> adjudicatorList = new ArrayList<String>();
    protected ArrayAdapter<String> adapter;

    public NewBallotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_new_ballot, container, false);
        update = getArguments().getBoolean("update");
        Log.d("UPDATING", Boolean.toString(update));

        Button button = (Button) rootView.findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormData();
                UploadToParse();
            }
        });

        int i = getArguments().getInt(ARG_TAB_NUMBER);
        String title = getResources().getStringArray(R.array.tabs_array)[i];
        getActivity().setTitle(title);

        initRoundSpinner();
        initPositionSpinner1();
        initPositionSpinner2();
        initPositionSpinner3();
        initPositionSpinner4();
        getFormData();
        setAllListeners();
        if(update == true){
            setFormData();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Adjudicator");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done( List<ParseObject> adjudicatorRes, ParseException e) {
                if (e == null) {
                    Log.d("Adjudicator", "Retrieved " + adjudicatorRes.size() + " Adjudicators");
                    for(ParseObject adj : adjudicatorRes){
                        adjudicatorList.add(adj.getString("name"));
                    }
                    adapter = new ArrayAdapter<String>(rootView.getContext(),
                            android.R.layout.simple_dropdown_item_1line, adjudicatorList);
                    adjudicators = (MultiAutoCompleteTextView) rootView.findViewById(R.id.adjudicators_value);
                    adjudicators.setAdapter(adapter);
                    adjudicators.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                } else {
                    Log.d("Adjudicator", "Error: " + e.getMessage());
                }
            }
        });



        return rootView;
    }

    public void setFormData(){
        id = getArguments().getString("id");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ballot");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    room_number.setText(object.getString("room_number"));
                    adjudicators.setText(object.getString("adjudicators"));
                    motion.setText(object.getString("motion"));
                    og_team.setText(object.getString("og_team"));
                    oo_team.setText(object.getString("oo_team"));
                    cg_team.setText(object.getString("cg_team"));
                    co_team.setText(object.getString("co_team"));
                    og_speaker_1.setText(object.getString("og_speaker_1"));
                    oo_speaker_1.setText(object.getString("oo_speaker_1"));
                    cg_speaker_1.setText(object.getString("cg_speaker_1"));
                    co_speaker_1.setText(object.getString("co_speaker_1"));
                    og_speaker_1_points.setText(object.getNumber("og_speaker_1_points").toString());
                    oo_speaker_1_points.setText(object.getNumber("oo_speaker_1_points").toString());
                    cg_speaker_1_points.setText(object.getNumber("cg_speaker_1_points").toString());
                    co_speaker_1_points.setText(object.getNumber("co_speaker_1_points").toString());
                    og_speaker_2.setText(object.getString("og_speaker_2"));
                    oo_speaker_2.setText(object.getString("oo_speaker_2"));
                    cg_speaker_2.setText(object.getString("cg_speaker_2"));
                    co_speaker_2.setText(object.getString("co_speaker_2"));
                    og_speaker_2_points.setText(object.getNumber("og_speaker_2_points").toString());
                    oo_speaker_2_points.setText(object.getNumber("oo_speaker_2_points").toString());
                    cg_speaker_2_points.setText(object.getNumber("cg_speaker_2_points").toString());
                    co_speaker_2_points.setText(object.getNumber("co_speaker_2_points").toString());

                    Spinner spinRound = (Spinner) rootView.findViewById(R.id.round_spinner);
                    spinRound.setSelection(object.getNumber("round").intValue() - 1);

                    Spinner spinPosition1 = (Spinner) rootView.findViewById(R.id.og_spinner);
                    spinPosition1.setSelection(object.getNumber("og_position").intValue() - 1);

                    Spinner spinPosition2 = (Spinner) rootView.findViewById(R.id.oo_spinner);
                    spinPosition2.setSelection(object.getNumber("oo_position").intValue() - 1);

                    Spinner spinPosition3 = (Spinner) rootView.findViewById(R.id.cg_spinner);
                    spinPosition3.setSelection(object.getNumber("cg_position").intValue() - 1);

                    Spinner spinPosition4 = (Spinner) rootView.findViewById(R.id.co_spinner);
                    spinPosition4.setSelection(object.getNumber("co_position").intValue() - 1);

                } else {
                    Toast.makeText(getActivity(), "Error retrieving data, soz m8...", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void getFormData() {
        room_number = (EditText) rootView.findViewById(R.id.room_number_value);
        adjudicators = (MultiAutoCompleteTextView) rootView.findViewById(R.id.adjudicators_value);
        motion = (EditText) rootView.findViewById(R.id.motion_value);
        try {
            //Opening gov data
            og_team = (EditText) rootView.findViewById(R.id.og_team);
            og_speaker_1 = (EditText) rootView.findViewById(R.id.og_speaker_1);
            og_speaker_1_points = (EditText) rootView.findViewById(R.id.og_speaker_1_points);
            og_speaker_2_points = (EditText) rootView.findViewById(R.id.og_speaker_2_points);
            og_speaker_2 = (EditText) rootView.findViewById(R.id.og_speaker_2);
            og_s1_points_int = Integer.parseInt(og_speaker_1_points.getText().toString());
            og_s2_points_int = Integer.parseInt(og_speaker_2_points.getText().toString());
            og_points_sum = og_s1_points_int + og_s2_points_int;
        } catch (Exception e) {
        }


        try {
            //Opening op data
            oo_team = (EditText) rootView.findViewById(R.id.oo_team);
            oo_speaker_1 = (EditText) rootView.findViewById(R.id.oo_speaker_1);
            oo_speaker_1_points = (EditText) rootView.findViewById(R.id.oo_speaker_1_points);
            oo_speaker_2_points = (EditText) rootView.findViewById(R.id.oo_speaker_2_points);
            oo_speaker_2 = (EditText) rootView.findViewById(R.id.oo_speaker_2);
            oo_s1_points_int = Integer.parseInt(oo_speaker_1_points.getText().toString());
            oo_s2_points_int = Integer.parseInt(oo_speaker_2_points.getText().toString());
            oo_points_sum = oo_s1_points_int + oo_s2_points_int;
        } catch (Exception e) {
        }


        try {
            //Closing gov data
            cg_team = (EditText) rootView.findViewById(R.id.cg_team);
            cg_speaker_1 = (EditText) rootView.findViewById(R.id.cg_speaker_1);
            cg_speaker_1_points = (EditText) rootView.findViewById(R.id.cg_speaker_1_points);
            cg_speaker_2_points = (EditText) rootView.findViewById(R.id.cg_speaker_2_points);
            cg_speaker_2 = (EditText) rootView.findViewById(R.id.cg_speaker_2);
            cg_s1_points_int = Integer.parseInt(cg_speaker_1_points.getText().toString());
            cg_s2_points_int = Integer.parseInt(cg_speaker_2_points.getText().toString());
            cg_points_sum = cg_s1_points_int + cg_s2_points_int;
        } catch (Exception e) {
        }


        try {
            //Closing op data
            co_team = (EditText) rootView.findViewById(R.id.co_team);
            co_speaker_1 = (EditText) rootView.findViewById(R.id.co_speaker_1);
            co_speaker_1_points = (EditText) rootView.findViewById(R.id.co_speaker_1_points);
            co_speaker_2_points = (EditText) rootView.findViewById(R.id.co_speaker_2_points);
            co_speaker_2 = (EditText) rootView.findViewById(R.id.co_speaker_2);
            co_s1_points_int = Integer.parseInt(co_speaker_1_points.getText().toString());
            co_s2_points_int = Integer.parseInt(co_speaker_2_points.getText().toString());
            co_points_sum = co_s1_points_int + co_s2_points_int;
        } catch (Exception e) {
        }

    }

    public void UploadToParse() {

        //laen parsesse info üles
        try {
            ParseObject newBallot;
            if(update == true){
                newBallot  = ParseObject.createWithoutData("Ballot", id);
            }
            else {
                newBallot = new ParseObject("Ballot");
            }
            newBallot.put("user", ParseUser.getCurrentUser());
            if(room_number.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty room number");}
            newBallot.put("room_number", room_number.getText().toString());
            newBallot.put("round", round_number);
            if(adjudicators.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty adjudicators field");}
            newBallot.put("adjudicators", adjudicators.getText().toString());
            if(motion.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty motion field");}
            newBallot.put("motion", motion.getText().toString());

            if(og_team.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty team name field");}
            newBallot.put("og_team", og_team.getText().toString());
            if(og_speaker_1.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OG speaker 1 name field");}
            newBallot.put("og_speaker_1", og_speaker_1.getText().toString());
            if(og_speaker_1_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OG speaker 1 points field");}
            newBallot.put("og_speaker_1_points", og_s1_points_int);
            if(og_speaker_2.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OG speaker 2 name field");}
            newBallot.put("og_speaker_2", og_speaker_2.getText().toString());
            if(og_speaker_2_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OG speaker 2 points field");}
            newBallot.put("og_speaker_2_points", og_s2_points_int);
            newBallot.put("og_points_sum", og_points_sum);
            newBallot.put("og_position", og_position);

            if(oo_team.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty team name field");}
            newBallot.put("oo_team", oo_team.getText().toString());
            if(oo_speaker_1.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OO speaker 1 name field");}
            newBallot.put("oo_speaker_1", oo_speaker_1.getText().toString());
            if(oo_speaker_1_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OO speaker 1 points field");}
            newBallot.put("oo_speaker_1_points", oo_s1_points_int);
            if(oo_speaker_2.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OO speaker 2 name field");}
            newBallot.put("oo_speaker_2", oo_speaker_2.getText().toString());
            if(oo_speaker_2_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty OO speaker 2 points field");}
            newBallot.put("oo_speaker_2_points", oo_s2_points_int);
            newBallot.put("oo_points_sum", oo_points_sum);
            newBallot.put("oo_position", oo_position);

            if(cg_team.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty team name field");}
            newBallot.put("cg_team", cg_team.getText().toString());
            if(cg_speaker_1.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CG speaker 1 name field");}
            newBallot.put("cg_speaker_1", cg_speaker_1.getText().toString());
            if(cg_speaker_1_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CG speaker 1 points field");}
            newBallot.put("cg_speaker_1_points", cg_s1_points_int);
            if(cg_speaker_2.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CG speaker 2 name field");}
            newBallot.put("cg_speaker_2", cg_speaker_2.getText().toString());
            if(cg_speaker_2_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CG speaker 2 points field");}
            newBallot.put("cg_speaker_2_points", cg_s2_points_int);
            newBallot.put("cg_points_sum", cg_points_sum);
            newBallot.put("cg_position", cg_position);

            if(co_team.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty team name field");}
            newBallot.put("co_team", co_team.getText().toString());
            if(co_speaker_1.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CO speaker 1 name field");}
            newBallot.put("co_speaker_1", co_speaker_1.getText().toString());
            if(co_speaker_1_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CO speaker 1 points field");}
            newBallot.put("co_speaker_1_points", co_s1_points_int);
            if(co_speaker_2.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CO speaker 2 name field");}
            newBallot.put("co_speaker_2", co_speaker_2.getText().toString());
            if(co_speaker_2_points.getText().toString().isEmpty()){throw new IllegalArgumentException("Empty CO speaker 2 points field");}
            newBallot.put("co_speaker_2_points", co_s2_points_int);
            newBallot.put("co_points_sum", co_points_sum);
            newBallot.put("co_position", co_position);

            HashMap<String, Integer> points = new HashMap<>();
            ValueComparator bvc =  new ValueComparator(points);
            TreeMap<String,Integer> sorted_map = new TreeMap<>(bvc);
            points.put("og", og_points_sum);
            points.put("oo", oo_points_sum);
            points.put("cg", cg_points_sum);
            points.put("co", co_points_sum);

            sorted_map.putAll(points);
            int i = 1;
            for(Map.Entry<String,Integer> entry : sorted_map.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                System.out.println(key + " => " + value);

                if(key.equals("og")){
                    if( i != og_position) throw new IllegalArgumentException("OG position does not match its speakerpoints");
                }else  if(key.equals("oo")){
                    if( i != oo_position) throw new IllegalArgumentException("OO position does not match its speakerpoints");
                }else  if(key.equals("cg")){
                    if( i != cg_position) throw new IllegalArgumentException("CG position does not match its speakerpoints");
                }else  if(key.equals("co")){
                    if( i != co_position) throw new IllegalArgumentException("CO position does not match its speakerpoints");
                }
                i++;
            }



            //kui salvestatud, siis callback
            newBallot.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "Submitted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error saving to server! Check the fields, try again!", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR", e.toString());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("ERR", e.toString());
            new AlertDialog.Builder(rootView.getContext())
                    .setTitle("Oops")
                    .setMessage("It seems, that you didn't fill out the form correctly. Check if all the fields are filled and points/positions correct! Exact error: " + e.getMessage())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    public void initRoundSpinner() {
        Spinner spinRound;
        spinRound = (Spinner) rootView.findViewById(R.id.round_spinner);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.rounds));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRound.setAdapter(adapter);
        spinRound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                round_number = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                round_number = 1;
            }
        });
    }

    public void initPositionSpinner1() {
        Spinner spinPosition;
        spinPosition = (Spinner) rootView.findViewById(R.id.og_spinner);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Position));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPosition.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        spinPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                og_position = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                og_position = 1;
            }
        });
    }

    public void initPositionSpinner2() {
        Spinner spinPosition;
        spinPosition = (Spinner) rootView.findViewById(R.id.oo_spinner);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Position));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPosition.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        spinPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                oo_position = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                oo_position = 1;
            }
        });
    }

    public void initPositionSpinner3() {
        Spinner spinPosition;
        spinPosition = (Spinner) rootView.findViewById(R.id.cg_spinner);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Position));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPosition.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        spinPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                cg_position = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                cg_position = 1;
            }
        });
    }

    public void initPositionSpinner4() {
        Spinner spinPosition;
        spinPosition = (Spinner) rootView.findViewById(R.id.co_spinner);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Position));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPosition.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        spinPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                co_position = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                co_position = 1;
            }
        });
    }


    public void setAllListeners() {
        og_speaker_1_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView og_sum = (TextView) rootView.findViewById(R.id.og_points_sum);
                try {
                    getFormData();
                    og_sum.setText(Integer.toString(og_s1_points_int + og_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        og_speaker_2_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView og_sum = (TextView) rootView.findViewById(R.id.og_points_sum);
                try {
                    getFormData();
                    og_sum.setText(Integer.toString(og_s1_points_int + og_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        oo_speaker_1_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView oo_sum = (TextView) rootView.findViewById(R.id.oo_points_sum);
                try {
                    getFormData();
                    oo_sum.setText(Integer.toString(oo_s1_points_int + oo_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        oo_speaker_2_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView oo_sum = (TextView) rootView.findViewById(R.id.oo_points_sum);
                try {
                    getFormData();
                    oo_sum.setText(Integer.toString(oo_s1_points_int + oo_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cg_speaker_1_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView cg_sum = (TextView) rootView.findViewById(R.id.cg_points_sum);
                try {
                    getFormData();
                    cg_sum.setText(Integer.toString(cg_s1_points_int + cg_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        cg_speaker_2_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView cg_sum = (TextView) rootView.findViewById(R.id.cg_points_sum);
                try {
                    getFormData();
                    cg_sum.setText(Integer.toString(cg_s1_points_int + cg_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        co_speaker_1_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView co_sum = (TextView) rootView.findViewById(R.id.co_points_sum);
                try {
                    getFormData();
                    co_sum.setText(Integer.toString(co_s1_points_int + co_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        co_speaker_2_points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView co_sum = (TextView) rootView.findViewById(R.id.co_points_sum);
                try {
                    getFormData();
                    co_sum.setText(Integer.toString(co_s1_points_int + co_s2_points_int));
                } catch (Exception e) {
                    Log.d("Error", "try/catch in addTextChangedListener");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
