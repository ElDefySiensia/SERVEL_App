package com.example.servel_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.servel_app.CandidatoModelo;
import com.example.servel_app.R;
import java.util.ArrayList;

public class CandidatosAdapter extends RecyclerView.Adapter<CandidatosAdapter.CandidatoViewHolder> {

    private ArrayList<CandidatoModelo> candidatos;

    public CandidatosAdapter(ArrayList<CandidatoModelo> candidatos) {
        this.candidatos = candidatos;
    }

    @NonNull
    @Override
    public CandidatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidato, parent, false);
        return new CandidatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatoViewHolder holder, int position) {
        CandidatoModelo candidato = candidatos.get(position);
        holder.itemNombreCandidato.setText(candidato.getNombre());
        holder.itemPartidoCandidato.setText(candidato.getPartido());
    }

    @Override
    public int getItemCount() {
        return candidatos.size();
    }

    static class CandidatoViewHolder extends RecyclerView.ViewHolder {
        TextView itemNombreCandidato, itemPartidoCandidato;

        public CandidatoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNombreCandidato = itemView.findViewById(R.id.itemNombreCandidato);
            itemPartidoCandidato = itemView.findViewById(R.id.itemPartidoCandidato);
        }
    }
}
