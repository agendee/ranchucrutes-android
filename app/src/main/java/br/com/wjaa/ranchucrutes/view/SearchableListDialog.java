package br.com.wjaa.ranchucrutes.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;

/**
 * Created by wagner on 30/09/15.
 */
public class SearchableListDialog<T extends SearchableListModel> implements View.OnClickListener {

    private SearchableListDialogCallback<T> callback;
    private Context context;
    private String title;
    private T [] objects;
    public SearchableListDialog(SearchableListDialogCallback callback, Context context){
        this.callback = callback;
        this.context = context;
    }

    public SearchableListDialog addTitle(String title){
        this.title = title;
        return this;
    }

    public void openDialog(T [] list){
        if (list != null && list.length > 0){
            objects = list;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            ListView modeList = new ListView(context);
            modeList.setItemsCanFocus(true);
            View v = LayoutInflater.from(context).inflate(R.layout.custom_title, null);
            TextView tv = (TextView) v.findViewById(R.id.titleDefault);
            tv.setText(title);
            builder.setCustomTitle(v);
            builder.setView(modeList);
            modeList.setBackgroundColor(context.getResources()
                    .getColor(android.support.v7.appcompat.R.color.primary_material_dark));
            final Dialog dialog = builder.create();
            SearchableListAdapter<T> modeAdapter = new SearchableListAdapter(context,
                    android.R.layout.simple_list_item_1, android.R.id.text1, list, dialog);
            modeList.setAdapter(modeAdapter);
            dialog.show();
        }else{
            AndroidUtils.showMessageDlg("Ops!", "A lista está vazia!", context);
        }
    }


    @Override
    public void onClick(View v) {

    }


    class SearchableListAdapter<T extends SearchableListModel> extends ArrayAdapter<T>{

        private Dialog dialog;
        public SearchableListAdapter(Context context, int resource, int textViewResourceId,
                                     T[] objects, Dialog dialog) {
            super(context, resource, textViewResourceId, objects);
            this.dialog = dialog;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                View row;
                LayoutInflater inflater = LayoutInflater.from(getContext());
                if (convertView == null){
                    row = inflater.inflate(R.layout.custom_item, null);
                }else{
                    row = convertView;
                }
                TextView t = (TextView) row.findViewById(R.id.txtViewItem);
                t.setOnClickListener(new SeachableListClickListener(objects[position],dialog));
                t.setText(objects[position].getName());
                return t;
            }else{
                TextView t = new TextView(getContext());
                t.setOnClickListener(new SeachableListClickListener(objects[position], dialog));
                t.setText(objects[position].getName());
                t.setTextSize(25);
                t.setPadding(20, 20, 20, 20);
                t.setBackgroundResource(R.drawable.edt_border);
                return t;

            }

        }

    }


    class SeachableListClickListener implements View.OnClickListener{

        private T objectSelect;
        private Dialog dialog;

        public SeachableListClickListener(T objectSelect, Dialog dialog){
            this.objectSelect = objectSelect;
            this.dialog = dialog;
        }


        @Override
        public void onClick(View v) {
            callback.onResult(objectSelect);
            dialog.dismiss();
        }
    }


}
