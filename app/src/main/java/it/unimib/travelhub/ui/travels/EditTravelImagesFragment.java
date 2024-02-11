package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.PICS_FOLDER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.ActivityAddTravelBinding;
import it.unimib.travelhub.databinding.FragmentEditTravelImagesBinding;

public class EditTravelImagesFragment extends Fragment {
    public int TAKE_PHOTO_CODE = 0;
    public static int count = 0;

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if(result.getResultCode() != getActivity().RESULT_OK){
            Log.d(TAG, "Error taking picture");
            return;
        }
        Intent intent = result.getData();
        if(intent == null){
            Log.d(TAG, "Intent is null");
            return;
        }
        Log.d(TAG, "Picture taken");
    });

    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + PICS_FOLDER;
    private static final String TAG = EditTravelImagesFragment.class.getSimpleName();
    private FragmentEditTravelImagesBinding binding;

    public EditTravelImagesFragment() {
        // Required empty public constructor
    }

    public static EditTravelImagesFragment newInstance() {
        EditTravelImagesFragment fragment = new EditTravelImagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTravelImagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeFolder(dir);

        binding.cameraButton.setOnClickListener(v -> {
            count++;
            String file = dir + count + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (Exception e) {
                Log.e(TAG, "Error creating file: " + e.getMessage());
            }

            Uri outputFileUri = Uri.fromFile(newfile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            mGetContent.launch(cameraIntent);
        });


    }

    /*TODO: fix picture taking and saving*/
    private void makeFolder(String s) {
        File newFolder = new File(s);
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}