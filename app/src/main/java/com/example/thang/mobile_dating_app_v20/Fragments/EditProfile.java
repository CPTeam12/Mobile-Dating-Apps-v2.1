package com.example.thang.mobile_dating_app_v20.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    MaterialEditText fullname,password,confirm_password,age,phone,address;
    RadioButton male,female;
    Button update;
    MaterialMultiAutoCompleteTextView hobby;
    Person currentPerson;
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
        setupProfile();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    currentPerson.setFullName(fullname.getText().toString());
                    currentPerson.setAge(Integer.parseInt(age.getText().toString()));
                    currentPerson.setHobbies(hobby.getText().toString());
                    if(!password.getText().toString().isEmpty()) currentPerson.setPassword(password.getText().toString());
                    if(!phone.getText().toString().isEmpty()) currentPerson.setPhone(Integer.parseInt(phone.getText().toString()));
                    if(!address.getText().toString().isEmpty()) currentPerson.setAddress(address.getText().toString());
                    if(male.isChecked()) currentPerson.setGender("male");
                    if(female.isChecked()) currentPerson.setGender("female");
                    DBHelper helper = new DBHelper(getActivity().getApplicationContext());
                    helper.updatePerson(currentPerson);
                    Toast.makeText(getActivity(),"Your Profile has been updated !",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
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
}
