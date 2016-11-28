package kennyjwilli.twofactorsync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText codeMinLengthTxt = (EditText) findViewById(R.id.codeMinTxt);
        codeMinLengthTxt.setText(Util.minCodeLength + "");
        codeMinLengthTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                String s = cs.toString();
                if (!s.isEmpty()) {
                    Util.minCodeLength = Integer.parseInt(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_matchers_listview, Util.codeMatchers);
        ListView listView = (ListView) findViewById(R.id.matchersListView);
        listView.setAdapter(adapter);
    }
}
