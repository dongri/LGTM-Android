package lol.lgtm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongri on 2017/11/26.
 */

public class RandomFragment extends Fragment {

    private EditText editText;

    public RandomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        editText = view.findViewById(R.id.plain_text_input);

        Button button = view.findViewById(R.id.button_submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                submit(editText.getText().toString());
            }
        });
    }

    private void submit(final String imageUrl) {
        String url = "https://lgtm.lol/api/submit";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        } catch (JSONException e) {
                            // error
                            Toast.makeText(getContext(), "Error:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //error
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                // パラメータ設定
                Map<String,String> params = new HashMap<String,String>();
                params.put("url",imageUrl);
                return params;
            }
        };

        // 送信
        queue.add(request);
    }
}
