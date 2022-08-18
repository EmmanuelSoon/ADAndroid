package com.team2.getfitwithhenry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.getfitwithhenry.model.Constants;
import com.team2.getfitwithhenry.model.DietRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class AddWaterFragment extends DialogFragment {

    private List<Double> cupValues = Arrays.asList(236.0, 330.0, 500.0);
    private IAddWater mCallBack;
    private boolean isAdding;
    private String ADD_WATER = "#6f956c";
    private String REMOVE_WATER = "#93000A";
    private String[] txtItems = {"Small cup - 236ml", "Medium cup - 330ml", "Large cup - 500ml"};
    private int plusImg = R.drawable.ic_plus_svgrepo_com;
    private int minusImg = R.drawable.ic_minus_svgrepo_com;
    private final String CUSTOM_ADAPTER_IMAGE = "image";
    private final String CUSTOM_ADAPTER_TEXT = "text";

    public AddWaterFragment() {
        // Required empty public constructor
    }

    public AddWaterFragment(boolean isAdding) {
        this.isAdding = isAdding;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(isAdding == true? "Add water": "Remove water");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(20,30,20,30);
        textView.setTextSize(25F);
        textView.setBackgroundColor(isAdding == true? Color.parseColor(ADD_WATER) : Color.parseColor(REMOVE_WATER));

        List<Map<String, Object>> dialogItemList = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < txtItems.length; i++)
        {
            Map<String, Object> itemMap = new HashMap<String, Object>();

            if(isAdding)
            {
                itemMap.put(CUSTOM_ADAPTER_IMAGE,plusImg);
                itemMap.put(CUSTOM_ADAPTER_TEXT,txtItems[i]);
            }
            else
            {
                itemMap.put(CUSTOM_ADAPTER_IMAGE,minusImg);
                itemMap.put(CUSTOM_ADAPTER_TEXT,txtItems[i]);
            }

            dialogItemList.add(itemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), dialogItemList,R.layout.water_level_img_txt, new String[]{CUSTOM_ADAPTER_IMAGE, CUSTOM_ADAPTER_TEXT}, new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_add_water, null))
                .setCustomTitle(textView)
                .setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallBack.onSelectedData(isAdding == true? cupValues.get(which): cupValues.get(which)*-1);
                    }
                })
//                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Add water but how
//                    }
//                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddWaterFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_water, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mCallBack = (IAddWater) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("must implement NoticeDialogListener");
        }

    }

    public interface IAddWater {
        void onSelectedData(Double selectedMils);
    }






}