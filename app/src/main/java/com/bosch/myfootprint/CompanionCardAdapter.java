package com.bosch.myfootprint;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.smooch.ui.ConversationActivity;

/**
 * Created by BHY1MTP on 10/31/2016.
 */

public class CompanionCardAdapter extends RecyclerView.Adapter<CompanionCardAdapter.MyViewHolder>  {
    private Context mContext;
    private List<CompanionCard> cardsList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public CardView card_view;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.description);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            card_view=(CardView)view.findViewById(R.id.card_view);
            thumbnail.bringToFront();

        }


        }



    public CompanionCardAdapter(Context mContext, List<CompanionCard> cardsList) {
        this.mContext = mContext;
        this.cardsList = cardsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.companion_card, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position= holder.getAdapterPosition();
                 CompanionCard card = cardsList.get(position);
                if(card.getKey().equals("Zunder"))
                {


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.bosch.bzo");

                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            } else {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.bosch.bzo"));
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }
                else if(card.getKey().equals("Live Chat"))
                {


                    ConversationActivity.show(mContext);
                }

                else if(card.getKey().equals("Bosch Buddy")) {
//                    Intent intent = new Intent(mContext, WebAct.class);
//                    mContext.startActivity(intent);

                }
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        CompanionCard card = cardsList.get(position);
        holder.title.setText(card.getName());
        holder.count.setText(card.getDescription());

        // loading album cover using Glide library
        Glide.with(mContext).load(card.getThumbnail()).into(holder.thumbnail);


    }




    @Override
    public int getItemCount() {
        return cardsList.size();
    }
}

