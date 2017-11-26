package lol.lgtm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongri on 2017/11/26.
 */

public class HomeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String url = "https://lgtm.lol/api/list";
    private List<Item> list = new ArrayList<Item>();
    private ListView listView;
    private Adapter adapter;
    private int page;
    private boolean flag_loading;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new Adapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity().getApplication(), ItemActivity.class);
                intent.putExtra("id", list.get(position).getId());
                intent.putExtra("url", list.get(position).getUrl());
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount != 0 && firstVisibleItem != 0) {
                    if(flag_loading == false) {
                        flag_loading = true;
                        page++;
                        getData(page);
                    }
                }
            }
        });
        page = 1;
        getData(page);
    }

    public void getData(int page) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url+"?page="+ page,  null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray json_members = jsonObject.getJSONArray("items");
                        for (int i = 0; i < json_members.length(); i++) {
                            JSONObject member = json_members.getJSONObject(i);
                            int id = member.getInt("id");
                            String url = member.getString("url");
                            Item m = new Item();
                            m.setId(id);
                            m.setUrl(url);
                            list.add(m);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    flag_loading = false;
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        );
        Controller.getPermission().addToRequestQueue(jsonRequest);
    }

    @Override
    public void onRefresh() {
        page = 1;
        list.clear();
        getData(page);
    }

}
