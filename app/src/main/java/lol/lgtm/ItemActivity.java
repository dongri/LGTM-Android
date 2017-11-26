package lol.lgtm;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by dongri on 2017/11/26.
 */

public class ItemActivity extends AppCompatActivity {

    private static final String url = "https://lgtm.lol/api/item";
    private ResizableImageView imageView;
    private int itemId;

    public ItemActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        Intent intent = getIntent();

        imageView = (ResizableImageView)findViewById(R.id.item_image);

        String data = intent.getDataString(); // https://lgtm.lol/i/234 | lgtm://i/234
        if (data != null) {
            String pathId = data.substring(data.lastIndexOf("/") + 1); // 234
            itemId = Integer.valueOf(pathId);
            setImageFromItemId(itemId);
        } else {
            itemId = intent.getIntExtra("id", 0);
            imageView.setImageUrl(intent.getStringExtra("url"), Controller.getPermission().getImageLoader());
        }

        final TextView textView = (TextView)findViewById(R.id.text_view_id);
        textView.setText("![LGTM](https://lgtm.lol/p/"+String.valueOf(itemId)+")");

        Button buttonCopy = (Button)findViewById(R.id.button_copy);
        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                copyText(getApplicationContext(), textView.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void copyText(Context context, String text){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB){
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }else{
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void setImageFromItemId(int itemId) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url + "/" + itemId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String url = jsonObject.getString("url");
                            imageView.setImageUrl(url, Controller.getPermission().getImageLoader());
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        Controller.getPermission().addToRequestQueue(jsonRequest);
    }

}
