package it.unimib.travelhub.data.source;

import it.unimib.travelhub.model.User;

public interface RemoteFileStorageCallback {
    void onSuccessProfileImageUpload(String downloadUrl, User user);
    void onSuccessDownload(String filePath);
    void onSuccessDelete();
    void onFailure(Exception exception);
}
