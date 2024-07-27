package com.depec.depechuancayosur;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InscritosAdapter extends RecyclerView.Adapter<InscritosAdapter.InscritosViewHolder> {

    private List<Inscrito> inscritosList;

    public InscritosAdapter(List<Inscrito> inscritosList) {
        this.inscritosList = inscritosList;
    }

    @NonNull
    @Override
    public InscritosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inscrito, parent, false);
        return new InscritosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InscritosViewHolder holder, int position) {
        Inscrito inscrito = inscritosList.get(position);
        holder.tvName.setText(inscrito.getName());
        holder.tvPhoneNumber.setText(inscrito.getPhoneNumber());
        holder.tvChurch.setText(inscrito.getChurch());
        holder.tvAlergia.setText(inscrito.getAlergia());
        holder.tvAge.setText(String.valueOf(inscrito.getEdad()));

        setLongClickListener(holder.tvName, inscrito.getName(), holder.itemView.getContext());
        setLongClickListener(holder.tvPhoneNumber, inscrito.getPhoneNumber(), holder.itemView.getContext());
        setLongClickListener(holder.tvChurch, inscrito.getChurch(), holder.itemView.getContext());
        setLongClickListener(holder.tvAlergia, inscrito.getAlergia(), holder.itemView.getContext());
        setLongClickListener(holder.tvAge, String.valueOf(inscrito.getEdad()), holder.itemView.getContext());

        // Add click listener to phone number TextView to add contact
        holder.tvPhoneNumber.setOnClickListener(v -> addToContacts("KIMO " + getFirstName(inscrito.getName()), inscrito.getPhoneNumber(), holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return inscritosList.size();
    }

    public static class InscritosViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhoneNumber, tvChurch, tvAlergia, tvAge;

        public InscritosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvChurch = itemView.findViewById(R.id.tvChurch);
            tvAlergia = itemView.findViewById(R.id.tvAlergia);
            tvAge = itemView.findViewById(R.id.tvAge);
        }
    }

    private void setLongClickListener(TextView textView, String text, Context context) {
        textView.setOnLongClickListener(v -> {
            copyToClipboard(text, context);
            return true;
        });
    }

    private void copyToClipboard(String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }

    private void addToContacts(String name, String phoneNumber, Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        context.startActivity(intent);
    }

    private String getFirstName(String fullName) {
        if (fullName.contains(" ")) {
            return fullName.substring(0, fullName.indexOf(" "));
        }
        return fullName;
    }
}
