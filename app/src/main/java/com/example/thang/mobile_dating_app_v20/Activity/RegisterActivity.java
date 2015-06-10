package com.example.thang.mobile_dating_app_v20.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends ActionBarActivity {

    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText confirm_password;
    MaterialEditText fullname;
    MaterialEditText birthyear;
    RadioButton male, female;
    Button accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (MaterialEditText) findViewById(R.id.register_email);
        password = (MaterialEditText) findViewById(R.id.register_password);
        confirm_password = (MaterialEditText) findViewById(R.id.register_confirm_password);
        fullname = (MaterialEditText) findViewById(R.id.register_fullname);
        birthyear = (MaterialEditText) findViewById(R.id.register_birthyear);
        male = (RadioButton) findViewById(R.id.register_male);
        female = (RadioButton) findViewById(R.id.register_female);
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
                    if(male.isChecked()){
                        person.setGender("Male");
                    } else {
                        person.setGender("Female");
                    }
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty()){
            email.setText(bundle.getString(""));
            fullname.setText(bundle.getString(""));
            birthyear.setText("");
            String a = "";
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

    private boolean validateLogin() {
        //username.validate("^(?=\\s*\\S).*$","Username cannot empty.");
        String error = getResources().getString(R.string.error_field_required);
        boolean a = email.validateWith(new RegexpValidator(error, "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean c = confirm_password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean d = fullname.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean e = birthyear.validateWith(new RegexpValidator("Only Number Please !", "^[A-Z._%+-]+@[A-Z.-]+\\.[A-Z]{2,6}$"));
        return (a && b && c && d && e);
    }
}
