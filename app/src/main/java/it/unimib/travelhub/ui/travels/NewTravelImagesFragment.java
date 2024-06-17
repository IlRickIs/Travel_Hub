package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.PICS_FOLDER;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import it.unimib.travelhub.databinding.FragmentEditTravelImagesBinding;

public class NewTravelImagesFragment extends Fragment {
    public boolean isCameraPermissionGranted = false;
    public static int count = 0;

    private Uri capturedImageUri;

    private final ActivityResultLauncher<Intent> mGetContentFromCam = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() != Activity.RESULT_OK){
                        Log.d(TAG, "Error taking picture " + result.getResultCode());
                        return;
                    }
                    Intent intent = result.getData();
                    if(intent == null){
                        Log.d(TAG, "Intent is null");
                        return;
                    }
                    Log.d(TAG, "Picture taken at" + capturedImageUri.toString());
                    displayPicture(capturedImageUri, binding.imageView);
                }
            });

    private final ActivityResultLauncher<String> mGetContentFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        displayPicture(uri, binding.imageView2);
                    } else {
                        Log.d(TAG, "No image selected");
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if(isGranted){
            Log.d(TAG, "Permission for camera granted");
        }else{
            Log.d(TAG, "Permission denied");
        }
    });
    private final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + PICS_FOLDER;
    private static final String TAG = NewTravelImagesFragment.class.getSimpleName();
    private FragmentEditTravelImagesBinding binding;

    public NewTravelImagesFragment() {
        // Required empty public constructor
    }

    public static NewTravelImagesFragment newInstance() {
        NewTravelImagesFragment fragment = new NewTravelImagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTravelImagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeFolder(dir);
        binding.cameraButton.setOnClickListener(v -> {
            if (getContext() == null) {
                Log.e(TAG, "Context is null");
                return;
            }
            if(ContextCompat.checkSelfPermission(
                    getContext(), android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {

                isCameraPermissionGranted = true;
            } else {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
                return;
            }

            capturedImageUri = FileProvider.getUriForFile(getContext(),
                    getContext().getApplicationContext().getPackageName() + ".provider",
                    createImageFile(dir));

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mGetContentFromCam.launch(cameraIntent);

        });

        binding.galleryButton.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContentFromGallery.launch("image/*");
    }

    private File createImageFile(String dir){
        count++;
        String fileName = dir + count + ".png";
        File newFile = new File(fileName);
        try {
            if(newFile.createNewFile())
                Log.d(TAG, "File created");
        } catch (Exception e) {
            Log.d(TAG, "Error creating file");
        }
        return newFile;
    }
    private void makeFolder(String dir) {
        File newFolder = new File(dir);
        if(!newFolder.exists()){
            if(!newFolder.mkdir())
                Log.e(TAG, "Error creating folder");
        }else{
            Log.d(TAG, "Folder already exists");
        }

    }

    private void displayPicture(Uri uri, View view){
        if(view == null){
            Log.d(TAG, "View is null");
        }else if(view instanceof ImageView){
            ((ImageView) view).setImageURI(uri);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}