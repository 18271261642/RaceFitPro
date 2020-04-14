package com.example.bozhilun.android.imagepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

public class TempActivity extends AppCompatActivity {

    PickerManager pickerManager;
    private static Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pickerManager = GlobalHolder.getInstance().getPickerManager();
        if(pickerManager != null){
            this.pickerManager.setActivity(TempActivity.this);
            this.pickerManager.pickPhotoWithPermission();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case PickerManager.REQUEST_CODE_SELECT_IMAGE:
                try {

                    if (data != null)
                        uri = data.getData();
                    else
                        uri = pickerManager.getImageFile();

                    pickerManager.setUri(uri);
                    pickerManager.startCropActivity();
                }catch (Exception e){
                    e.getMessage();
                }

                break;
            case REQUEST_CROP:
                try {
                    if (data != null) {
                        pickerManager.handleCropResult(data);
                    } else
                        finish();
                }catch (Exception e){
                    e.getMessage();
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PickerManager.REQUEST_CODE_IMAGE_PERMISSION)
                pickerManager.handlePermissionResult(grantResults);
        else
            finish();

    }

}
