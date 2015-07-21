package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.EditProfileActivity;
import com.example.thang.mobile_dating_app_v20.Activity.HobbyActivity;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 7/17/2015.
 */
public class EditPassword extends Fragment {
    private static final String URL_UPDATE = MainActivity.URL_CLOUD + "/Service/updateprofile";
    private MaterialDialog materialDialog;
    MaterialEditText currentPassword;
    MaterialEditText newPassword;
    MaterialEditText rePassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_password_fragment, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //update actionbar title
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //actionBar.setTitle(R.string.change_password);

        currentPassword = (MaterialEditText) view.findViewById(R.id.current_password);
        newPassword = (MaterialEditText) view.findViewById(R.id.new_password);
        rePassword = (MaterialEditText) view.findViewById(R.id.re_new_password);

        //set up loading dialog
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .content(R.string.progress_dialog)
                .progress(true, 0);
        materialDialog = dialogBuilder.build();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_password_fragment, menu);
        menu.findItem(R.id.commit_update).setVisible(false);
        menu.findItem(R.id.update_password).setVisible(false);
        menu.findItem(R.id.update_hobby).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fragment_commit_password:
                if (validate()) {
                    Person person = DBHelper.getInstance(getActivity()).getCurrentUser();
                    person.setPassword(newPassword.getText().toString());
                    List<Person> persons = new ArrayList<>();
                    persons.add(person);
                    new UpdateProfileTask().execute(persons);
                    return true;
                }
                break;
            case R.id.fragment_update_profile:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.fragment_update_hobby:
                Intent intent1 = new Intent(getActivity(), HobbyActivity.class);
                startActivity(intent1);
                return true;
        }
        return false;
    }

    private boolean validate() {
        String error = getResources().getString(R.string.error_field_required);
        boolean a = currentPassword.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean b = newPassword.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean c = rePassword.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean matchPassword = true;
        if (!newPassword.getText().toString().trim().equals(rePassword.getText().toString())) {
            matchPassword = false;
            rePassword.setError(getString(R.string.error_password));
        }
        return (a && b && c && matchPassword);
    }

    private class UpdateProfileTask extends AsyncTask<List<Person>, Integer, String> {

        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(getActivity());
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            } else {
                materialDialog.show();
            }
        }

        @Override
        protected String doInBackground(List<Person>... persons) {
            List<Person> p = persons[0];
            return ConnectionTool.makePostRequest(URL_UPDATE, p);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //close loading dialog
                if (materialDialog != null) {
                    materialDialog.dismiss();
                }

                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                if (personList != null) {

                    //move to avatar activity

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getText(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
