package com.example.canteen.controller.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;

import java.util.List;

public class FoodInputAdapter extends RecyclerView.Adapter<FoodInputAdapter.VH> {

    public interface Callback {
        void onTextChanged(int position, @NonNull String text);

        void onDeleteClicked(int position);
    }

    private final List<String> data;
    private final Callback callback;

    public FoodInputAdapter(@NonNull List<String> data, @NonNull Callback callback) {
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(data.get(position), position, callback);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        private final EditText etName;
        private final Button btnDelete;
        private TextWatcher watcher;

        VH(@NonNull View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.etFoodName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(@NonNull String value, int position, @NonNull Callback callback) {
            if (watcher != null) {
                etName.removeTextChangedListener(watcher);
            }

            if (!TextUtils.equals(etName.getText(), value)) {
                etName.setText(value);
                etName.setSelection(etName.length());
            }

            watcher = new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    int p = getBindingAdapterPosition();
                    if (p != RecyclerView.NO_POSITION) {
                        callback.onTextChanged(p, s == null ? "" : s.toString());
                    }
                }
            };
            etName.addTextChangedListener(watcher);

            btnDelete.setOnClickListener(v -> {
                int p = getBindingAdapterPosition();
                if (p != RecyclerView.NO_POSITION) {
                    callback.onDeleteClicked(p);
                }
            });
        }
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
