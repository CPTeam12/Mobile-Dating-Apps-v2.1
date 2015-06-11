package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {
    private static final String URL_REGISTER = "http://datingappservice2.groundctrl.nl/datingapp/Service/register";
    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText confirm_password;
    MaterialEditText fullname;
    MaterialEditText birthyear;
    MaterialEditText datingage;
    RadioButton rb_male, rb_female;
    Button accept, back;
    MultiAutoCompleteTextView hobby;
    CheckBox cb_male,cb_female;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (MaterialEditText) findViewById(R.id.register_email);
        password = (MaterialEditText) findViewById(R.id.register_password);
        confirm_password = (MaterialEditText) findViewById(R.id.register_confirm_password);
        fullname = (MaterialEditText) findViewById(R.id.register_fullname);
        birthyear = (MaterialEditText) findViewById(R.id.register_birthyear);
        datingage = (MaterialEditText) findViewById(R.id.register_datingage);
        rb_male = (RadioButton) findViewById(R.id.register_male_rb);
        rb_female = (RadioButton) findViewById(R.id.register_female_rb);
        back = (Button) findViewById(R.id.register_back_login);
        hobby = (MultiAutoCompleteTextView) findViewById(R.id.register_hobby);
        cb_male = (CheckBox) findViewById(R.id.register_male_cb);
        cb_female = (CheckBox) findViewById(R.id.register_female_cb);
        String[] hobbies = getResources().getStringArray(R.array.register_hobbies);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_dropdown_item,hobbies);
        hobby.setAdapter(adapter);
        hobby.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        accept = (Button) findViewById(R.id.register_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateLogin()){
                    Person person = new Person();
                    person.setEmail(email.getText().toString());
                    person.setPassword(password.getText().toString());
                    person.setFullName(fullname.getText().toString());
                    person.setAge(Calendar.YEAR - Integer.parseInt(birthyear.getText().toString()));
                    person.setHobbies(hobby.getText().toString());
                    person.setDatingAge(Integer.parseInt(datingage.getText().toString()));
                    if(cb_male.isChecked()){
                        person.setDatingMen(true);
                    } else {
                        person.setDatingMen(false);
                    }
                    if(cb_female.isChecked()){
                        person.setDatingWomen(true);
                    } else {
                        person.setDatingWomen(false);
                    }
                    if(rb_male.isChecked()){
                        person.setGender("Male");
                    } else {
                        person.setGender("Female");
                    }
                    List<Person> persons = new ArrayList<Person>();
                    persons.add(person);
                    new getInformationFriend().execute(persons);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trimCache(getApplicationContext());
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            email.setText(bundle.getString("email"));
            fullname.setText(bundle.getString("name"));
            String gender = bundle.getString("gender");
            if(gender.equalsIgnoreCase("Male")){
                rb_male.setChecked(true);
                rb_female.setChecked(false);
            } else {
                rb_male.setChecked(false);
                rb_female.setChecked(true);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void trimCache(Context context) {
        File dir = context.getCacheDir();
        if(dir!= null && dir.isDirectory()){
            File[] children = dir.listFiles();
            if (children == null) {
                // Either dir does not exist or is not a directory
            } else {
                File temp;
                for (int i = 0; i < children.length; i++) {
                    temp = children[i];
                    temp.delete();
                }
            }
        }
    }

    private boolean validateLogin() {
        //username.validate("^(?=\\s*\\S).*$","Username cannot empty.");
        String error = getResources().getString(R.string.error_field_required);
        //boolean a = email.validateWith(new RegexpValidator(error, "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"));
        boolean a = email.validateWith(new RegexpValidator(error, "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean c = confirm_password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean d = fullname.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean e = birthyear.validateWith(new RegexpValidator("4 Digit Numbers Please !", "^(\\d{4})([\\.|,]\\d{1,2})?$"));
        boolean f = datingage.validateWith(new RegexpValidator("2 Digit Numbers Please !", "^(\\d{2})([\\.|,]\\d{1,2})?$"));
        return (a && b && c && d && e && f);
    }

    private class getInformationFriend extends AsyncTask<List<Person>, Integer, Boolean> {
        Person person;
        @Override
        protected Boolean doInBackground(List<Person>... persons) {
            List<Person> p = persons[0];
            person = p.get(0);
            return ConnectionTool.sendPersonRegister(URL_REGISTER, p);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                DBHelper helper = new DBHelper(getApplicationContext());
                helper.insertPerson(person,DBHelper.USER_FLAG_CURRENT);
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Register fail ! Check your connection again !",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
