package it.unimib.travelhub.data.source;

import android.net.Uri;

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
                    uploadCallback.onSuccessUpload(task.getResult().toString());
                } else {
                    uploadCallback.onFailure(task.getException());
                }
            }
        });
    }

    public interface downloadCallback {
        void onSuccessDownload(String url);
        void onFailure(Exception e);
    }
    @Override
    public void download(String downloadUrl, File file, RemoteFileStorageSource.downloadCallback downloadCallback) {
        StorageReference storageReference = storage.getReferenceFromUrl(downloadUrl);
        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                downloadCallback.onSuccessDownload(file.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                downloadCallback.onFailure(exception);
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
