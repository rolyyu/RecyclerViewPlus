package edu.jnu.recyclerviewplus.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.jnu.recyclerviewplus.R;

/**
 * Created by roly on 2016/10/29.
 *
 */
public class RecyclerViewPlusAdapter extends BaseAdapter {

    private Context context;

    private List<String> list;

    public RecyclerViewPlusAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder)holder;
        Picasso.with(context)
                .load(list.get(position))
                .placeholder(context.getResources().getDrawable(R.mipmap.ic_launcher))
                .error(context.getResources().getDrawable(R.mipmap.ic_launcher))
                .into(viewHolder.image);
    }

    @Override
    protected int getCount() {
        if (list == null || list.isEmpty())
            return 0;
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
