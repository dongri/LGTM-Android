package lol.lgtm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by dongri on 2017/11/26.
 */
public class Adapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> DataList;
    ImageLoader imageLoader = Controller.getPermission().getImageLoader();

    public Adapter(Activity activity, List<Item> billionairesItems) {
        this.activity = activity;
        this.DataList = billionairesItems;
    }

    @Override
    public int getCount() {
        return DataList.size();
    }

    @Override
    public Object getItem(int location) {
        return DataList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.fragment_item, null);

        if (imageLoader == null)
            imageLoader = Controller.getPermission().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.list_image);
        Item m = DataList.get(position);
        thumbNail.setImageUrl(m.getUrl(), imageLoader);

        return convertView;
    }

}