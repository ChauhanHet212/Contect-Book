package com.example.contactbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactbook.ClickListener;
import com.example.contactbook.Models.Contacts;
import com.example.contactbook.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    Context context;
    List<Contacts> arrcontacts;
    ClickListener listener;

    public ContactsAdapter(Context context, List<Contacts> arrcontacts, ClickListener listener) {
        this.context = context;
        this.arrcontacts = arrcontacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        holder.item_name.setText(arrcontacts.get(position).getName());
        holder.item_number.setText(arrcontacts.get(position).getNumber());
        String C = arrcontacts.get(position).getName().substring(0, 1).toUpperCase();
        holder.item_img.setText(C);
        holder.item_img.setBackground(context.getResources().getDrawable(getColor()));

        holder.item_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(arrcontacts.get(holder.getAdapterPosition()));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(arrcontacts.get(holder.getAdapterPosition()), holder.cardView);
                return true;
            }
        });
    }

    public int getColor(){
        List<Integer> bgList = new ArrayList<>();
        bgList.add(R.drawable.contact_bg1);
        bgList.add(R.drawable.contact_bg2);
        bgList.add(R.drawable.contact_bg3);
        bgList.add(R.drawable.contact_bg4);
        bgList.add(R.drawable.contact_bg5);
        bgList.add(R.drawable.contact_bg6);
        bgList.add(R.drawable.contact_bg7);
        bgList.add(R.drawable.contact_bg8);

        int r = new Random().nextInt(bgList.size());
        return bgList.get(r);
    }

    @Override
    public int getItemCount() {
        return arrcontacts.size();
    }

    public void filteredList(List<Contacts> filterlist) {
        arrcontacts = filterlist;
        notifyDataSetChanged();
    }

    public void normal(List<Contacts> allcontacts) {
        arrcontacts = allcontacts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView item_name, item_number, item_img;
        ImageView item_call;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            item_name = itemView.findViewById(R.id.item_name);
            item_number = itemView.findViewById(R.id.item_number);
            item_img = itemView.findViewById(R.id.item_img);
            item_call = itemView.findViewById(R.id.item_call);
        }
    }
}
