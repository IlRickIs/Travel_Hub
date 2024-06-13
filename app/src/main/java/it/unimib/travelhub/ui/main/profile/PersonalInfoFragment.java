package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.PICS_FOLDER;
import static it.unimib.travelhub.util.Constants.PROFILE_IMAGE_REMOTE_PATH;
import static it.unimib.travelhub.util.Constants.PROFILE_PICTURE_FILE_NAME;
import static it.unimib.travelhub.util.Constants.USERNAME;
import static it.unimib.travelhub.util.Constants.USER_BIRTHDATE;
import static it.unimib.travelhub.util.Constants.USER_NAME;
import static it.unimib.travelhub.util.Constants.USER_SURNAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.data.source.RemoteFileStorageSource;
import it.unimib.travelhub.databinding.FragmentPersonalInfoBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.welcome.UserViewModel;
import it.unimib.travelhub.ui.welcome.UserViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;

public class PersonalInfoFragment extends Fragment {
    public boolean isCameraPermissionGranted = false;
    private Uri capturedImageUri;
    private String dir = null;
    private final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
                    displayPicture(capturedImageUri, binding.personalInfoImage);
                }
            });

    private final ActivityResultLauncher<String> mGetContentFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        saveToFile(requireContext(), uri, dir + PROFILE_PICTURE_FILE_NAME); //TODO: maybe save path in Costants
                        displayPicture(uri, binding.personalInfoImage);
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
    private static final String TAG = PersonalInfoFragment.class.getSimpleName();
    private Uri imageUri;
    private File profileImage;
    private FragmentPersonalInfoBinding binding;
    final Calendar myCalendar= Calendar.getInstance();
    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private IUserRepository userRepository;

    public PersonalInfoFragment() {
        // Required empty public constructor
    }

    public static PersonalInfoFragment newInstance() {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() == null) {
            Log.e(TAG, "Unexpected error: Context is null");
            return;
        }
        dir = getContext().getFilesDir() + PICS_FOLDER;

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageUri = null;
        // Inflate the layout for this fragment
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatePickerDialog.OnDateSetListener date1 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
            Objects.requireNonNull(binding.textFieldBirth.getEditText()).setText(sdf.format(myCalendar.getTime()));
        };

        binding.textEditBirth.setOnClickListener(v -> {
            if (getContext() != null)
                if (Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty()) {
                    new DatePickerDialog(getContext(), date1,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    new DatePickerDialog(getContext(), date1,
                            Integer.parseInt(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().split("/")[2]),
                            Integer.parseInt(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().split("/")[1]) - 1,
                            Integer.parseInt(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().split("/")[0])).show();
                }
            else
                Log.e(TAG, "Unexpected error: Context is null");
        });

        try {

            String name = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_NAME);
            Objects.requireNonNull(binding.textFieldName.getEditText()).setHint(name);
            binding.textFieldName.getEditText().setText(name);

            String surname = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_SURNAME);
            Objects.requireNonNull(binding.textFieldSurname.getEditText()).setHint(surname);
            binding.textFieldSurname.getEditText().setText(surname);

            String birthDate = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_BIRTHDATE);
            Objects.requireNonNull(binding.textFieldBirth.getEditText()).setHint(birthDate);
            binding.textFieldBirth.getEditText().setText(birthDate);

            String profileImagePath = dir + PROFILE_PICTURE_FILE_NAME;
            Log.d(TAG, "Profile image: " + profileImagePath);
            try {
                File file = new File(profileImagePath);
                Log.d(TAG, "File exist: " + file.exists());
                imageUri = Uri.fromFile(file);
                displayPicture(imageUri, binding.personalInfoImage);
            } catch (Exception e){
                Log.d(TAG, "Error while reading profile image", e);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error while reading data from encrypted shared preferences", e);
        }


        binding.buttonLogout.setOnClickListener(v -> {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
            builder1.setMessage(R.string.logout_message);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> userViewModel.logout(dataEncryptionUtil, requireActivity().getApplication()).observe(getViewLifecycleOwner(), result -> {
                        if (result.isSuccess()) {
                            try {
                                String mail = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
                                String password = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
                                String username = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USERNAME);
                                Log.d(TAG, "Username from encrypted SharedPref: " + username);
                                Log.d(TAG, "Email address from encrypted SharedPref: " + mail);
                                Log.d(TAG, "Password from encrypted SharedPref: " + password);

                            } catch (GeneralSecurityException | IOException e) {
                                e.printStackTrace();
                            }
                            Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_welcomeActivity);
                            requireActivity().finish();
                        } else {
                            Snackbar.make(view,
                                    requireActivity().getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }));

            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();


        });

        binding.buttonSaveSettings.setOnClickListener(v -> {
            if (somethingIsChanged()) {
                saveUserDataRemote();
            } else {
                Log.d(TAG, "Nothing is changed");
                Snackbar.make(requireView(),
                        requireActivity().getString(R.string.personal_info_saved),
                        Snackbar.LENGTH_SHORT).show();
            }

        });

        binding.personalInfoConstraintImage.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
            @SuppressLint("InflateParams") View view1 = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_layout_personal_info, null);
            bottomSheetDialog.setContentView(view1);
            bottomSheetDialog.show();

            view1.findViewById(R.id.button_take_picture).setOnClickListener(v1 -> {
                if (getContext() == null) {
                    Log.e(TAG, "Unexpected error: Context is null");
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

                profileImage = createImageFile(dir);
                if (profileImage == null) {
                    Log.e(TAG, "Unexpected error: File is null");
                    return;
                }
                capturedImageUri = FileProvider.getUriForFile(getContext(),
                        getContext().getApplicationContext().getPackageName() + ".provider",
                        profileImage);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mGetContent.launch(cameraIntent);
                bottomSheetDialog.dismiss();
            });

            view1.findViewById(R.id.button_choose_gallery).setOnClickListener(v1 -> {
                openGallery();
                bottomSheetDialog.dismiss();
            });
        });

        binding.buttonBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private Boolean somethingIsChanged() {
        String name = Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString();
        String surname = Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString();
        String birthDate = Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString();

        try {
            String saved_name = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_NAME);
            String saved_surname = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_SURNAME);
            String saved_birthDate = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_BIRTHDATE);

            boolean nameCheck = name == null ? saved_name != null : !name.equals(saved_name == null ? "" : saved_name);
            boolean surnameCheck = surname == null ? saved_surname != null : !surname.equals(saved_surname == null ? "" : saved_surname);
            boolean birthDateCheck = birthDate == null ? saved_birthDate != null : !birthDate.equals(saved_birthDate == null ? "" : saved_birthDate);

            return imageUri != null || nameCheck || surnameCheck || birthDateCheck;
        } catch (Exception e) {
            Log.d(TAG, "Error while reading data from encrypted shared preferences - somethingIsChanged - ", e);
            return false;
        }
    }

    private void saveUserDataRemote() {
        String name = Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString();
        String surname = Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString();
        Long birthDate = Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty() ?
                null : parseStringToDateLong(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString());

        try {
            String idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            String username = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USERNAME);
            String email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            User user = new User(username, name, surname, birthDate, null, email, idToken);

            String remotePath = PROFILE_IMAGE_REMOTE_PATH + idToken + ".png";

            Log.d(TAG, "PATH: " + remotePath);

            if (imageUri != null) {
                userRepository.uploadProfileImage(remotePath, imageUri, new RemoteFileStorageSource.uploadCallback() {
                    @Override
                    public void onSuccessUpload(String downloadUrl) {
                        user.setPhotoUrl(downloadUrl);
                        userViewModel.updateUserData(user);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Error while uploading image", e);
                        Snackbar.make(requireView(),
                                requireActivity().getString(R.string.error_upload_image),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                userViewModel.updateUserData(user);
            }

            Observer<Result> observer = new Observer<Result>() {
                @Override
                public void onChanged(Result result) {
                    if (result.isSuccess()) {
                        try {
                            Log.d(TAG, "User data saved successfully");

                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_NAME, name);
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_SURNAME, surname);
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USER_BIRTHDATE,
                                    parseDateToString(birthDate == null ? null : new Date(birthDate)));

                            Snackbar.make(requireView(),
                                    requireActivity().getString(R.string.personal_info_saved),
                                    Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(TAG, "Error while writing data to encrypted shared preferences", e);
                        }
                    } else {
                        Snackbar.make(requireView(),
                                requireActivity().getString(R.string.unexpected_error),
                                Snackbar.LENGTH_SHORT).show();
                    }

                    userViewModel.getUserMutableLiveData().removeObserver(this);
                }
            };

            userViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), observer);
        } catch (Exception e) {
            Log.d(TAG, "Error while reading data from encrypted shared preferences - saveUserDataRemote - ", e);
        }


    }

    private String parseDateToString(Date birthDate) {
        if (birthDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        return sdf.format(birthDate);
    }

    public Long parseStringToDateLong(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate == null ? 0L : parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private void openGallery() {
        //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetContentFromGallery.launch("image/*");
    }

    private File createImageFile(String dir) {
        File file = new File(dir);
        if(!file.exists())
            if (!file.mkdir())
                return null;
        String fileName = dir + PROFILE_PICTURE_FILE_NAME;
        file = new File(fileName);
        try {
            if (file.createNewFile())
                Log.d(TAG, "File created");
        } catch (Exception e) {
            Log.d(TAG, "Error creating file");
        }
        return file;
    }

    private void displayPicture(Uri uri, View view){
        if(view == null){
            Log.e(TAG, "View is null");
        }else if(view instanceof ImageView){
            ((ImageView) view).setImageURI(uri);
            imageUri = uri;
        }
    }

    public static void saveToFile(Context context, Uri uri, String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(context.getContentResolver()
                .openInputStream(uri)); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath,
                false))) {
            byte[] buffer = new byte[1024];

            int bytes = bis.read(buffer);
            if (bytes == -1) {
                return;
            }
            do {
                bos.write(buffer);
            } while (bis.read(buffer) != -1);
        } catch (IOException ignored) {

        }
    }

}