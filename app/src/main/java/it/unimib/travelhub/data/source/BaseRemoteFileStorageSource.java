package it.unimib.travelhub.data.source;

import android.net.Uri;

import java.io.File;

public abstract class BaseRemoteFileStorageSource {
    protected RemoteFileStorageCallback remoteFileStorageCallback;

    public void setRemoteFileStorageCallback(RemoteFileStorageCallback remoteFileStorageCallback) {
        this.remoteFileStorageCallback = remoteFileStorageCallback;
    }

    //public abstract void uploadProfileImage(Uri file, String idToken);
    public abstract void upload(String remotePath, Uri imageUri, RemoteFileStorageSource.uploadCallback uploadCallback);

    public abstract void download(String downloadUrl, File file, RemoteFileStorageSource.downloadCallback downloadCallback);

    public abstract void delete(String downloadUrl);
}
