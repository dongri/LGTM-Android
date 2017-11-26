package lol.lgtm;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dongri on 2017/11/26.
 */

public class ItemActivity extends AppCompatActivity {

    public ItemActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        Intent intent = getIntent();

        ResizableImageView imageView = (ResizableImageView)findViewById(R.id.item_image);
        imageView.setImageUrl(intent.getStringExtra("url"), Controller.getPermission().getImageLoader());

        int id = intent.getIntExtra("id", 0);
        final TextView textView = (TextView)findViewById(R.id.text_view_id);
        textView.setText("![LGTM](https://lgtm.lol/p/"+String.valueOf(id)+")");

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

}
