package it.unimib.travelhub.data.source;

import static it.unimib.travelhub.util.Constants.PROFILE_IMAGE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import it.unimib.travelhub.util.SharedPreferencesUtil;

public class RemoteFileStorageSource extends BaseRemoteFileStorageSource {
    private final FirebaseStorage storage;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public RemoteFileStorageSource(SharedPreferencesUtil sharedPreferencesUtil) {
        this.storage = FirebaseStorage.getInstance();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    public interface uploadCallback {
        void onSuccessUpload(String url);
        void onFailure(Exception e);
    }
    @Override
    public void upload(String remotePath, Uri imageUri, RemoteFileStorageSource.uploadCallback uploadCallback) {
        StorageReference storageReference = storage.getReference().child(remotePath);
        UploadTask uploadTask = storageReference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    uploadCallback.onFailure(task.getException());
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, PROFILE_IMAGE, imageUri.getPath());
                    Log.d("RemoteFileStorageSource", "File uploaded successfully" + imageUri.getPath());
                    uploadCallback.onSuccessUpload(task.getResult().toString());
                } else {
                    uploadCallback.onFailure(task.getException());
                }
            }
        });
    }

    @Override
    public void download(String downloadUrl, File file) {
        StorageReference storageReference = storage.getReferenceFromUrl(downloadUrl);
        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                remoteFileStorageCallback.onSuccessDownload(file.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                remoteFileStorageCallback.onFailure(exception);
            }
        });
    }

    @Override
    public void delete(String downloadUrl) {
        StorageReference storageReference = storage.getReferenceFromUrl(downloadUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteFileStorageCallback.onSuccessDelete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                remoteFileStorageCallback.onFailure(exception);
            }
        });
    }
}
