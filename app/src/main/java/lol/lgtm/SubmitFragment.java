package lol.lgtm;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by dongri on 2017/11/26.
 */

public class SubmitFragment extends Fragment {

    private String url = "https://lgtm.lol/api/submit";
    private String urlUpload = "https://lgtm.lol/api/upload";
    private EditText editText;
    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView;
    private Bitmap bitmap;

    private Uri imageUri;

    public SubmitFragment() {
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
            public void onClick(View view) {
                submit(editText.getText().toString());
            }
        });

        Button cameraButton = (Button) view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, "My Picture");
                cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                imageUri = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, RESULT_CAMERA);
            }
        });

        imageView = (ImageView) view.findViewById(R.id.item_image);

        Button uploadButton = (Button) view.findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image = getStringImage(bitmap);
                //passing the image to volley
                SendImage(image);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA){
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), imageUri);
                    bitmap = resize(bitmap, 1000, 1000);
                    imageView.setImageBitmap(bitmap);
                    this.bitmap = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void submit(final String imageUrl) {
        final Loading loading = new Loading(getActivity());
        loading.show();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                            editText.setText("");
                            loading.close();
                        } catch (JSONException e) {
                            // error
                            Toast.makeText(getContext(), "Error:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            loading.close();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error
                        Toast.makeText(getContext(), "Error:" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        loading.close();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // パラメータ設定
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", imageUrl);
                return params;
            }
        };

        // 送信
        Controller.getPermission().addToRequestQueue(request);
    }


    private void SendImage(final String image) {
        final Loading loading = new Loading(getActivity());
        loading.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getContext(), "DONE", Toast.LENGTH_LONG).show();
                            loading.close();
                            imageView.setImageBitmap(null);
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("image", image);
                return params;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Controller.getPermission().addToRequestQueue(stringRequest);;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public static Bitmap resize(Bitmap bitmap, int newWidth, int newHeight) {

        if (bitmap == null) {
            return null;
        }

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        if (oldWidth < newWidth && oldHeight < newHeight) {
            // 縦も横も指定サイズより小さい場合は何もしない
            return bitmap;
        }

        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix scale = new Matrix();
        scale.postScale(scaleFactor, scaleFactor);

        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false);
        bitmap.recycle();

        return resizeBitmap;

    }

}




