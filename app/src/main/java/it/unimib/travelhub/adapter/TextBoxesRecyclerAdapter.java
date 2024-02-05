package it.unimib.travelhub.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.unimib.travelhub.R;

public class TextBoxesRecyclerAdapter extends RecyclerView.Adapter<TextBoxesRecyclerAdapter.DestinationsViewHolder> {

    List<String> destinationsHints;
    List<String> destinationsTexts;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onKeyPressed(int position, String text);
    }

    private static final String TAG = TextBoxesRecyclerAdapter.class.getSimpleName();

    public List<String> getDestinationsTexts() {
        return destinationsTexts;
    }

    public void setDestinationsTexts(List<String> destinationsTexts) {
        this.destinationsTexts = destinationsTexts;
    }

    public void setDestinationsHints(List<String> destinationsHints) {
        this.destinationsHints = destinationsHints;
    }
    public List<String> getDestinationsHints() {
        return destinationsHints;
    }


    public TextBoxesRecyclerAdapter(List<String> destinationsHints, List<String> destinationsTexts , OnItemClickListener onItemClickListener) {
        this.destinationsHints = destinationsHints;
        this.destinationsTexts = destinationsTexts;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public DestinationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_input_item, parent, false);
        return new DestinationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationsViewHolder holder, int position) {
        TextInputLayout textInputLayout = holder.getTextInputLayout();
        TextInputEditText textInputEditText = holder.getTextInputEditText();
        textInputLayout.setVisibility(View.VISIBLE);
        textInputEditText.setVisibility(View.VISIBLE);
        textInputLayout.setHint(destinationsHints.get(position));
        textInputEditText.setText(destinationsTexts.get(position));

    }

    @Override
    public int getItemCount() {
        if(destinationsHints == null) {
            return 0;
        }
        return destinationsHints.size();
    }

    public class DestinationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher{
        private final TextInputLayout textInputLayout;
        private final TextInputEditText textInputEditText;
        private final Button button;

        public DestinationsViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d(TAG, "new view");
            button = (Button) itemView.findViewById(R.id.cancel_input_button);
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.txt_layout_in_recyclerview);
            textInputEditText = (TextInputEditText) itemView.findViewById(R.id.txt_edit_in_recyclerview);
            textInputEditText.addTextChangedListener((TextWatcher) this);
            button.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(onItemClickListener != null) {
                int position = getBindingAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onKeyPressed(position, s.toString());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

