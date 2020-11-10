package alireza.sn.mycontacts.adapters;

import android.media.Image;
import android.sax.ElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import alireza.sn.mycontacts.R;
import alireza.sn.mycontacts.models.MyInfo;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    private List<MyInfo> list;
    public List<MyInfo> listAll;

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyInfo> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (MyInfo myContacts : listAll) {
                    if (myContacts.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(myContacts);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends MyInfo>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void callClick(int position);

        void itemClick(int position, ImageView pic, Button call, TextView phone);

        void starClick(int position, ImageView star);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RecyclerAdapter(List<MyInfo> list) {
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.phone.setText(list.get(position).getPhone());

        if (list.get(position).isMark()) {
            holder.star.setImageResource(R.drawable.icon_favorites_full);
        } else {
            holder.star.setImageResource(R.drawable.icon_favorites);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView phone;

        public ImageView picture;
        public ImageView star;
        public Button call;

        public ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
            callClick();
            starClick();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(getAdapterPosition(), picture, call, phone);
                }
            });
        }

        private void starClick() {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.starClick(getAdapterPosition(), star);
                }
            });
        }

        private void callClick() {
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.callClick(getAdapterPosition());
                }
            });
        }

        private void findViews(View itemView) {
            name = itemView.findViewById(R.id.name);
            picture = itemView.findViewById(R.id.icon_person_contacts);
            phone = itemView.findViewById(R.id.phone);
            call = itemView.findViewById(R.id.icon_call);
            star = itemView.findViewById(R.id.mark);
        }
    }
}
