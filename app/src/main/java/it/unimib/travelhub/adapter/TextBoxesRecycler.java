package it.unimib.travelhub.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import it.unimib.travelhub.R;

public class TextBoxesRecycler extends RecyclerView.Adapter<TextBoxesRecycler.ViewHolder> {
    @NonNull
    @Override
    public TextBoxesRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TextBoxesRecycler.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextInputLayout textInputLayout;
        private final TextInputEditText textInputEditText;
        private final Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            button = (Button) itemView.findViewById(R.id.cancel_input_button);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.txt_layout_in_recyclerview);
            textInputEditText = (TextInputEditText) itemView.findViewById(R.id.txt_edit_in_recyclerview);
        }

        public TextInputLayout getTextInputLayout() {
            return textInputLayout;
        }

        public TextInputEditText getTextInputEditText() {
            return textInputEditText;
        }

        public Button getButton() {
            return button;
        }
    }
}

