package lol.lgtm;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongri on 2017/11/26.
 */

public class HomeFragment extends Fragment {

    private static final String url = "https://lgtm.lol/api/list";
    private List<Item> list = new ArrayList<Item>();
    private ListView listView;
    private Adapter adapter;

    public HomeFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // ListViewに表示するデータ
//        final ArrayList<String> items = new ArrayList<>();
//        items.add("データ1");
//        items.add("データ2");
//        items.add("データ3");

        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new Adapter(getActivity(), list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 詳細画面へ値を渡す
                Intent intent = new Intent(getActivity().getApplication(), ItemActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("url", list.get(position).getUrl());

                startActivity(intent);
            }
        });

        //非同期 json request
        //String api_url = "https://lgtm.lol/api/list";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,  null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {

                            JSONArray json_members = jsonObject.getJSONArray("items");

                            for (int i = 0; i < json_members.length(); i++) {

                                //取得
                                JSONObject member = json_members.getJSONObject(i);

                                int id = member.getInt("id");
                                String url = member.getString("url");

                                //表示
                                Item m = new Item();

                                m.setId(id);
                                m.setUrl(url);

                                list.add(m);

                                //
                            }

                        } catch (Exception e) {

                        }

                        adapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                    }
                }
        );

        //実行（キュー）
        Controller.getPermission().addToRequestQueue(jsonRequest);

    }

}
