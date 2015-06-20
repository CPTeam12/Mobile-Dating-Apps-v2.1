package com.example.thang.mobile_dating_app_v20.Fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {
    private static final String URL_UPDATE = MainActivity.URL_CLOUD + "/Service/updateprofile";
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    MaterialEditText fullname,password,confirm_password,age,phone,address;
    RadioButton male,female;
    Button update;
    MaterialMultiAutoCompleteTextView hobby;
    Person currentPerson;
    CircleImageView profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile,container,false);
        DBHelper helper = new DBHelper(getActivity().getApplicationContext());
        currentPerson = helper.getCurrentUser();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        fullname = (MaterialEditText) getView().findViewById(R.id.edit_fullname);
        password = (MaterialEditText) getView().findViewById(R.id.edit_password);
        confirm_password = (MaterialEditText) getView().findViewById(R.id.edit_confirm_password);
        age = (MaterialEditText) getView().findViewById(R.id.edit_age);
        phone = (MaterialEditText) getView().findViewById(R.id.edit_phone);
        address = (MaterialEditText) getView().findViewById(R.id.edit_address);
        male = (RadioButton) getView().findViewById(R.id.edit_male_rb);
        female = (RadioButton) getView().findViewById(R.id.edit_female_rb);
        update = (Button) getView().findViewById(R.id.edit_accept);

        hobby = (MaterialMultiAutoCompleteTextView) getView().findViewById(R.id.edit_hobby);
        String[] hobbies = getResources().getStringArray(R.array.register_hobbies);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.simple_dropdown_item,hobbies);
        hobby.setAdapter(adapter);
        hobby.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        profile = (CircleImageView) getView().findViewById(R.id.edit_profile_avatar);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 200);
            }
        });

        setupProfile();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    currentPerson.setFullName(fullname.getText().toString());
                    currentPerson.setAge(Integer.parseInt(age.getText().toString()));
                    currentPerson.setHobbies(hobby.getText().toString());
                    if(!password.getText().toString().isEmpty()) currentPerson.setPassword(password.getText().toString());
                    if(!phone.getText().toString().isEmpty()) currentPerson.setPhone(phone.getText().toString());
                    if(!address.getText().toString().isEmpty()) currentPerson.setAddress(address.getText().toString());
                    if(male.isChecked()) currentPerson.setGender("Male");
                    if(female.isChecked()) currentPerson.setGender("Female");
                    List<Person> persons = new ArrayList<Person>();
                    persons.add(currentPerson);
                    new updateProfileTask().execute(persons);
            }
            }
        });
    }

    private void setupProfile() {
        if(currentPerson!=null){
            fullname.setText(currentPerson.getFullName());
            age.setText(currentPerson.getAge() + "");
            hobby.setText(currentPerson.getHobbies());
            if(currentPerson.getGender().equalsIgnoreCase("male")){
                male.setChecked(true);
                female.setChecked(false);
            } else {
                male.setChecked(false);
                female.setChecked(true);
            }
            address.setText(currentPerson.getAddress());
            if(currentPerson.getPhone() == null) currentPerson.setPhone("");
            phone.setText(currentPerson.getPhone());
            profile.setImageBitmap(currentPerson.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(currentPerson.getAvatar()));
        }
    }

    public boolean validate(){
        String error = getResources().getString(R.string.error_field_required);
        boolean a = fullname.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean b = age.validateWith(new RegexpValidator(getResources().getString(R.string.error_invalid_age), "^(\\d{2})([\\.|,]\\d{1,2})?$"));
        boolean c = hobby.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean d = password.getText().toString().equals(confirm_password.getText().toString());
        if(!d){
            confirm_password.setError(getResources().getText(R.string.error_password));
        }
        return (a && b && c && d);
    }

    private class updateProfileTask extends AsyncTask<List<Person>, Integer, String> {

        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(getActivity());
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
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
                if(personList != null){
                    //update current user into database
                    DBHelper helper = new DBHelper(getActivity().getApplicationContext());
                    helper.updatePerson(currentPerson);

                    //move to main activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.error_connection),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == -1 && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap avatar = (Utils.resizeBitmap(BitmapFactory.decodeFile(picturePath)));
            profile.setImageBitmap(avatar);
            //Convert avatar to Base64 for transfer to service
            currentPerson.setAvatar(Utils.encodeBitmapToBase64String(avatar));
        }
    }

}
