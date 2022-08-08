package com.team2.getfitwithhenry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class WrongIngredientFragment extends DialogFragment {

    private EditText actual;
    private String predicted;

    public WrongIngredientFragment() {
        // Required empty public constructor
    }

    public interface IWrongIngredientFragment {
        void itemClicked(String content);
    }

    private IWrongIngredientFragment iWrongIngredientFragment;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iWrongIngredientFragment = (IWrongIngredientFragment) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        predicted = getArguments().getString("predicted");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_wrong_ingredient, container, false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("predicted");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sorry, We got it wrong... :(" );
        builder.setMessage("It wasn't " + predicted + "??");


        // Edited: Overriding onCreateView is not necessary in your case
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_wrong_ingredient, null);
        actual = view.findViewById(R.id.actual_ing);
        builder.setView(view);

        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                actual = view.findViewById(R.id.actual_ing);
                if(actual.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Enter something!", Toast.LENGTH_SHORT).show();
                }
                else{
                    // send json to server.
                    iWrongIngredientFragment.itemClicked(actual.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });




        return builder.create();
    }
}