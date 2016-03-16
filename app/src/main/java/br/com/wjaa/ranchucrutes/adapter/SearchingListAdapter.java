package br.com.wjaa.ranchucrutes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.service.RanchucrutesConstants;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 05/10/15.
 */
public class SearchingListAdapter extends RecyclerView.Adapter<SearchingListAdapter.MyViewHolder>{


    private Context mContext;
    private List<SearchingListModel> mList;
    private LayoutInflater mLayoutInflater;
    private float scale;
    private int width;



    public SearchingListAdapter(Context c, List<SearchingListModel> l){
        this(c, l, true, true);
    }
    public SearchingListAdapter(Context c, List<SearchingListModel> l, boolean wa, boolean wcl){
        mContext = c;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.custom_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvModel.setText(mList.get(position).getName());
        myViewHolder.setItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mList) ? 0 : mList.size();
    }

    public void addListItem(SearchingListModel c, int position){
        mList.add(c);
        notifyItemInserted(position);
    }


    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvModel;
        private SearchingListModel item;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvModel = (TextView) itemView.findViewById(R.id.txtViewItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra(RanchucrutesConstants.PARAM_RESULT_SEARCH,item);
            ((Activity) mContext).setResult(1, i );
            ((Activity) mContext).finish();

        }

        public void setItem(SearchingListModel item) {
            this.item = item;
        }
    }



}
