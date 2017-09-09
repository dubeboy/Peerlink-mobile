package com.dubedivine.samples.features.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by divine on 2017/09/09.
 */

public class InstantAutoComplete extends ArrayAdapter<String> implements Filterable {

    private List<String> items = new ArrayList<>();

    public InstantAutoComplete(@NonNull Context context,
                               int resource,
                               @NonNull List<String> items) {
        super(context, resource, items);
        this.items = items;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults fl = new FilterResults();
                if (charSequence != null) {
                    Timber.i("performFiltering: the filterd data is %s", charSequence);
                    fl.values = items;
                }
                return fl;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence != null && charSequence.length() > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    
}
