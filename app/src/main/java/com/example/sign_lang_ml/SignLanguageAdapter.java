package com.example.sign_lang_ml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sign_lang_ml.R;
import com.example.sign_lang_ml.SignLanguageCard;

import java.util.List;

public class SignLanguageAdapter extends RecyclerView.Adapter<SignLanguageAdapter.SignLanguageViewHolder> {
    private Context context;
    private List<SignLanguageCard> signLanguageList;
    private OnCardClickListener onCardClickListener;

    public interface OnCardClickListener {
        void onCardClick(View view, SignLanguageCard signLanguageCard);
    }

    public SignLanguageAdapter(Context context, List<SignLanguageCard> signLanguageList, OnCardClickListener onCardClickListener) {
        this.context = context;
        this.signLanguageList = signLanguageList;
        this.onCardClickListener = onCardClickListener;
    }

    @NonNull
    @Override
    public SignLanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sign_language_card, parent, false);
        return new SignLanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignLanguageViewHolder holder, int position) {
        SignLanguageCard signLanguageCard = signLanguageList.get(position);
        holder.textView.setText(signLanguageCard.getLetter());
        holder.imageView.setImageResource(signLanguageCard.getImageResourceId());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.onCardClick(v, signLanguageCard);
            }
        });
    }

    @Override
    public int getItemCount() {
        return signLanguageList.size();
    }

    public static class SignLanguageViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        CardView cardView;

        public SignLanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
